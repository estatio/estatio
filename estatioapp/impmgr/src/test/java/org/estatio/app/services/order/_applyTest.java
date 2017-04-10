package org.estatio.app.services.order;

import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.order.OrderRepository;

public class _applyTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private OrderRepository orderRepository;

    @Test
    @Ignore
    public void act() throws Exception {

        // given
        OrderInvoiceLine line = new OrderInvoiceLine();
        line.setOrderDate(new LocalDate(2017,01,31));

        // when
        OrderInvoiceLine._apply lineWithMixin = new OrderInvoiceLine._apply(line);
        lineWithMixin.orderRepository = orderRepository;
        OrderInvoiceLine result = lineWithMixin.act();
    }

}