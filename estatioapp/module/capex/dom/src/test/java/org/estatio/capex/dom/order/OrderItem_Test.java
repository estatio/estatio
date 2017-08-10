package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
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
    public void isInvoiced_works_with_negative_amounts() throws Exception {

        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("-100.00"));
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();

        // when
        item1.setNetAmount(new BigDecimal("-50.00"));
        item2.setNetAmount(new BigDecimal("-49.99"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();

        // and when
        item1.setNetAmount(new BigDecimal("-50.00"));
        item2.setNetAmount(new BigDecimal("-50.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

        // and when
        item1.setNetAmount(new BigDecimal("-50.00"));
        item2.setNetAmount(new BigDecimal("-50.01"));
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

    @Test
    public void isInvoiced_works_with_discarded_invoice() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("100.00"));
        IncomingInvoice discardedInvoice = new IncomingInvoice();
        discardedInvoice.setApprovalState(IncomingInvoiceApprovalState.DISCARDED);
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        item1.setInvoice(discardedInvoice);
        IncomingInvoice invoice = new IncomingInvoice();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();
        item2.setInvoice(invoice);

        // when
        item1.setNetAmount(new BigDecimal("45.00")); // should be discarded
        item2.setNetAmount(new BigDecimal("55.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();
        assertThat(orderItem.getNetAmountInvoiced()).isEqualTo(new BigDecimal("55.00"));
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

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

    @Test
    public void getNetAmountInvoiced_works(){

        // given
        OrderItem orderItem = new OrderItem();
        orderItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        BigDecimal expectedTotalNetAmountOnItems = new BigDecimal("100.00");
        BigDecimal netAmountOnItem1 = new BigDecimal("55.00");
        BigDecimal netAmountOnItem2 = new BigDecimal("45.00");
        BigDecimal netAmountOnItem3 = null;
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        item1.setNetAmount(netAmountOnItem1);
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();
        item2.setNetAmount(netAmountOnItem2);
        IncomingInvoiceItem item3 = new IncomingInvoiceItem();
        item3.setNetAmount(netAmountOnItem3);
        OrderItemInvoiceItemLink link1 = new OrderItemInvoiceItemLink();
        link1.setInvoiceItem(item1);
        OrderItemInvoiceItemLink link2 = new OrderItemInvoiceItemLink();
        link2.setInvoiceItem(item2);
        OrderItemInvoiceItemLink link3 = new OrderItemInvoiceItemLink();
        link3.setInvoiceItem(item3);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByOrderItem(orderItem);
            will(returnValue(Arrays.asList(link1, link2, link3)));
        }});

        // when
        BigDecimal netAmountInvoiced = orderItem.getNetAmountInvoiced();

        // then
        Assertions.assertThat(netAmountInvoiced).isEqualTo(expectedTotalNetAmountOnItems);
    }

}