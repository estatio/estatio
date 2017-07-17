package org.estatio.capex.dom.invoice;

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

}