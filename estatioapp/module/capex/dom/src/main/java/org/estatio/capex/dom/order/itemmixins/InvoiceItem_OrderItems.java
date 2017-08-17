package org.estatio.capex.dom.order.itemmixins;

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
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

/**
 * TODO: inline this mixin
 */
@Mixin
public class InvoiceItem_OrderItems {

    private final IncomingInvoiceItem invoiceItem;
    public InvoiceItem_OrderItems(IncomingInvoiceItem invoiceItem){
        this.invoiceItem = invoiceItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<OrderItem> orderItems() {
        List<OrderItem> result = new ArrayList<>();
        for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByInvoiceItem(invoiceItem)){
            result.add(link.getOrderItem());
        }
        return result;
    }

    @Inject
    private OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
