package org.estatio.capex.dom.orderinvoice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;

@Mixin
public class OrderItem_InvoiceItems {

    private final OrderItem orderItem;
    public OrderItem_InvoiceItems(OrderItem orderItem){
        this.orderItem = orderItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoiceItem> invoiceItems() {
        List<IncomingInvoiceItem> result = new ArrayList<>();
        for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByOrderItem(orderItem)){
            result.add(link.getInvoiceItem());
        }
        return result;
    }

    @Inject
    private OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
