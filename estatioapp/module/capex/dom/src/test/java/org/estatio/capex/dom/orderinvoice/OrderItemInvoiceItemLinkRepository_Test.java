package org.estatio.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;

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

}