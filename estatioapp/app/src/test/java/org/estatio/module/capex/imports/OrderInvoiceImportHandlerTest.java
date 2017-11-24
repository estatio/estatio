package org.estatio.module.capex.imports;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderInvoiceImportHandlerTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private OrderRepository orderRepository;

    @Test
    public void determine_ordernumber_works() throws Exception {

        // given
        OrderInvoiceImportHandler handler = new OrderInvoiceImportHandler();
        handler.setOrderDate(new LocalDate(2017,01,31));
        handler.orderRepository = orderRepository;

        // expect
        context.checking(new Expectations() {
            {
                oneOf(orderRepository).findByOrderNumber(with("20170131-001"));
                will(returnValue(new Order()));
                oneOf(orderRepository).findByOrderNumber(with("20170131-002"));
                will(returnValue(null));
            }

        });

        // when
        String orderNumber = handler.determineOrderNumber();

        // then
        assertThat(orderNumber).isEqualTo("20170131-002");

    }

}