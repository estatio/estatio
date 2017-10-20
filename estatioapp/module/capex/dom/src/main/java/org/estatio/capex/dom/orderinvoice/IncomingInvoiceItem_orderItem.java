package org.estatio.capex.dom.orderinvoice;

import java.util.Optional;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;

/**
 * @see OrderItem_invoiceItemLinks (one-to-many)
 */
@Mixin(method="prop")
public class IncomingInvoiceItem_orderItem extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_orderItem(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public OrderItem prop() {
        final Optional<OrderItem> items = orderItemInvoiceItemLinkRepository.findLinkedOrderItemsByInvoiceItem(mixee);
        return items.orElse(null);
    }
}
