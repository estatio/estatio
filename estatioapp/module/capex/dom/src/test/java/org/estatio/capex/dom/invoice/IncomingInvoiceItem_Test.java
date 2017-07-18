package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class IncomingInvoiceItem_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoice mockInvoice;

    @Mock
    RepositoryService mockRepositoryService;

    @Test
    public void removeItem() throws Exception {

        // given
        IncomingInvoiceItem item = new IncomingInvoiceItem(){
            @Override
            boolean isLinkedToOrderItem(){
                return false;
            }
        };
        item.setInvoice(mockInvoice);
        item.repositoryService = mockRepositoryService;

        // expect
        context.checking(new Expectations(){
            {
                oneOf(mockRepositoryService).removeAndFlush(item);
                oneOf(mockInvoice).recalculateAmounts();
            }
        });

        // when
        item.removeItem();

    }

    @Test
    public void subtractAmounts_works() throws Exception {

        IncomingInvoiceItem incomingInvoiceItem = new IncomingInvoiceItem();

        // given
        BigDecimal amount = new BigDecimal("100.00");
        incomingInvoiceItem.setNetAmount(amount);
        incomingInvoiceItem.setVatAmount(null);
        incomingInvoiceItem.setGrossAmount(amount);

        // when
        BigDecimal netToSubtract = new BigDecimal("50.50");
        BigDecimal vatToSubtract = new BigDecimal("10.10");
        BigDecimal grossToSubtract = null;
        incomingInvoiceItem.subtractAmounts(netToSubtract, vatToSubtract, grossToSubtract);

        // then
        Assertions.assertThat(incomingInvoiceItem.getNetAmount()).isEqualTo(new BigDecimal("49.50"));
        Assertions.assertThat(incomingInvoiceItem.getVatAmount()).isNull();
        Assertions.assertThat(incomingInvoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));

    }

    @Test
    public void addAmounts_works() throws Exception {

        IncomingInvoiceItem incomingInvoiceItem = new IncomingInvoiceItem();

        // given
        BigDecimal amount = new BigDecimal("100.00");
        incomingInvoiceItem.setNetAmount(amount);
        incomingInvoiceItem.setVatAmount(null);
        incomingInvoiceItem.setGrossAmount(amount);

        // when
        BigDecimal netToAdd = new BigDecimal("50.50");
        BigDecimal vatToAdd = new BigDecimal("10.10");
        BigDecimal grossToAdd = null;
        incomingInvoiceItem.addAmounts(netToAdd, vatToAdd, grossToAdd);

        // then
        Assertions.assertThat(incomingInvoiceItem.getNetAmount()).isEqualTo(new BigDecimal("150.50"));
        Assertions.assertThat(incomingInvoiceItem.getVatAmount()).isNull();
        Assertions.assertThat(incomingInvoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));

    }

}