package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderItem_Test {

    @Test
    public void isInvoiced_works() throws Exception {

        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("100.00"));
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();

        // when
        item1.setNetAmount(new BigDecimal("50.00"));
        item2.setNetAmount(new BigDecimal("49.99"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();

        // and when
        item1.setNetAmount(new BigDecimal("50.00"));
        item2.setNetAmount(new BigDecimal("50.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

        // and when
        item1.setNetAmount(new BigDecimal("50.00"));
        item2.setNetAmount(new BigDecimal("50.01"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

    }

    @Test
    public void isInvoiced_works_when_no_netamount_on_orderItem() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(null); // explicit for this test
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();

        // when
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();
    }

    @Test
    public void isInvoiced_works_when_no_netamount_on_invoiceItem() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("100.00"));
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();

        // when
        item1.setNetAmount(null); // explicit for this test
        item2.setNetAmount(new BigDecimal("50.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();
    }

    private OrderItemInvoiceItemLinkRepository setupOrderItemInvoiceItemLinkRepository(final IncomingInvoiceItem item1, final IncomingInvoiceItem item2){
        OrderItemInvoiceItemLink link1 = new OrderItemInvoiceItemLink();
        OrderItemInvoiceItemLink link2 = new OrderItemInvoiceItemLink();

        link1.setInvoiceItem(item1);
        link2.setInvoiceItem(item2);

        OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository = new OrderItemInvoiceItemLinkRepository(){
            @Override
            public List<OrderItemInvoiceItemLink> findByOrderItem(
                    final OrderItem orderItem) {
                return Arrays.asList(link1, link2);
            }
        };

        return orderItemInvoiceItemLinkRepository;
    }

}