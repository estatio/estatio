package org.estatio.module.capex.dom.invoice;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class IncomingInvoice_validateForAmounts_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceItemRepository mockIncomingInvoiceItemRepository;

    @Mock
    EventBusService mockEventBusService;

    States eventBusInteractions = context.states("not-recognised");

    IncomingInvoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new IncomingInvoice() {
            @Override
            protected EventBusService getEventBusService() {
                return mockEventBusService;
            }

            @Override
            public String getAtPath() {
                return "/FRA";
            }
        };
        context.checking(new Expectations() {{
            ignoring(mockEventBusService);
            when(eventBusInteractions.isNot("recognised"));
        }});
    }

    public static class validator_Test extends IncomingInvoice_validateForAmounts_Test {

        @Test
        public void validateForAmounts() throws Exception {

            String result;
            IncomingInvoice.Validator validator;

            // given
            validator = new IncomingInvoice.Validator();
            invoice.setNetAmount(new BigDecimal("100.00"));
            invoice.setGrossAmount(new BigDecimal("100.00"));

            // when
            result = validator.validateForAmounts(invoice).getResult();

            // then
            Assertions.assertThat(result).isEqualTo("total amount on items equal to amount on the invoice required");

            // and given
            invoice.setGrossAmount(BigDecimal.ZERO);
            invoice.setNetAmount(BigDecimal.ZERO);
            // when
            validator = new IncomingInvoice.Validator();
            result = validator.validateForAmounts(invoice).getResult();
            // then
            Assertions.assertThat(result).isNull();

            // and given
            invoice.setNetAmount(new BigDecimal("100.00"));
            invoice.setGrossAmount(new BigDecimal("100.00"));
            IncomingInvoiceItem item = new IncomingInvoiceItem() {
                @Override
                void invalidateApproval() {
                    // nothing
                }
            };
            item.setNetAmount(new BigDecimal("100.00"));
            item.setGrossAmount(new BigDecimal("100.00"));
            invoice.getItems().add(item);
            // when
            validator = new IncomingInvoice.Validator();
            result = validator.validateForAmounts(invoice).getResult();
            // then
            Assertions.assertThat(result).isNull();

        }
    }

}