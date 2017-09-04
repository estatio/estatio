package org.estatio.capex.dom.orderinvoice;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.OrderItem;

/**
 * @see IncomingInvoiceItem_orderItemLinks
 */
@Mixin(method="coll")
public class OrderItem_invoiceItemLinks extends OrderItem_abstractMixinInvoiceItemLinks {
    public OrderItem_invoiceItemLinks(final OrderItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<OrderItemInvoiceItemLink> coll() {
        return orderItemInvoiceItemLinkRepository.findByOrderItem(mixee);
    }
}
