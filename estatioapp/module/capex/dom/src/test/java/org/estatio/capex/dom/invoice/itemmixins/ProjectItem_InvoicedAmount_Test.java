package org.estatio.capex.dom.invoice.itemmixins;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.project.ProjectItem;

public class ProjectItem_InvoicedAmount_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceItemRepository mockIncomingInvoiceItemRepository;

    @Mock
    EventBusService mockEventBusService;

    @Test
    public void invoicedAmount_works() throws Exception {

        // given
        BigDecimal expectedTotalInvoicedNetAmountOnItems = new BigDecimal("100.00");
        BigDecimal netAmountOnItem1 = new BigDecimal("55.00");
        BigDecimal netAmountOnItem2 = new BigDecimal("45.00");
        BigDecimal netAmountOnItem3_discarded = new BigDecimal("12345.67");

        ProjectItem projectItem = new ProjectItem();
        ProjectItem_InvoicedAmount mixin = new ProjectItem_InvoicedAmount(projectItem);
        mixin.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;

        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        item1.setInvoice(newIncomingInvoice());
        item1.setNetAmount(netAmountOnItem1);

        IncomingInvoiceItem item2 = new IncomingInvoiceItem();
        item2.setInvoice(newIncomingInvoice());
        item2.setNetAmount(netAmountOnItem2);
        IncomingInvoiceItem item3_discarded = new IncomingInvoiceItem(){
            @Override
            public  boolean isDiscarded(){
                return true;
            }
        };
        item3_discarded.setInvoice(newIncomingInvoice());

        item3_discarded.setNetAmount(netAmountOnItem3_discarded);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceItemRepository).findByProjectAndCharge(null, null);
            will(returnValue(Arrays.asList(item1, item2, item3_discarded)));
        }});

        // when
        BigDecimal orderedAmount = mixin.invoicedAmount();

        // then
        Assertions.assertThat(orderedAmount).isEqualTo(expectedTotalInvoicedNetAmountOnItems);

    }

    private IncomingInvoice newIncomingInvoice() {
        final IncomingInvoice incomingInvoice = new IncomingInvoice() {
            @Override protected EventBusService getEventBusService() {
                return mockEventBusService;
            }
        };
        context.ignoring(mockEventBusService);

        return incomingInvoice;
    }

}