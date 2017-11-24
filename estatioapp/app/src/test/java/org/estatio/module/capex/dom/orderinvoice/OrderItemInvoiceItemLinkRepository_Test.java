package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class OrderItemInvoiceItemLinkRepository_Test {


    @Test
    public void sum_works(){

        // given
        BigDecimal expectedTotalNetAmountOnItems = new BigDecimal("100.00");

        OrderItemInvoiceItemLink link1 = createLink(new BigDecimal("55.00"));
        OrderItemInvoiceItemLink link2 = createLink(new BigDecimal("45.00"));
        OrderItemInvoiceItemLink link3 = createLink(null);

        final List<OrderItemInvoiceItemLink> result = Arrays.asList(link1, link2, link3);

        // when
        OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository = new OrderItemInvoiceItemLinkRepository();
        final BigDecimal netAmountInvoiced = orderItemInvoiceItemLinkRepository.sum(result);

        // then
        Assertions.assertThat(netAmountInvoiced).isEqualTo(expectedTotalNetAmountOnItems);
    }

    private static OrderItemInvoiceItemLink createLink(final BigDecimal netAmount2) {
        OrderItemInvoiceItemLink link = new OrderItemInvoiceItemLink();
        link.setInvoiceItem(new IncomingInvoiceItem());
        link.setNetAmount(netAmount2);
        return link;
    }

    @Test
    public void calculateNetAmountNotLinkedFromInvoiceItem_works_when_netAmount_is_null() {

        // given
        OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository = new OrderItemInvoiceItemLinkRepository(){
            @Override
            public BigDecimal calculateNetAmountLinkedFromInvoiceItem(final IncomingInvoiceItem invoiceItem) {
                return new BigDecimal("123.45");
            }
        };
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
        // when
        invoiceItem.setNetAmount(null);
        // then
        assertThat(orderItemInvoiceItemLinkRepository.calculateNetAmountNotLinkedFromInvoiceItem(invoiceItem)).isEqualTo(BigDecimal.ZERO);

    }

}