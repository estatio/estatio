package org.estatio.module.capex.dom.orderinvoice;

import java.util.Optional;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

/**
 * @see OrderItem_invoiceItemLinks (one-to-many)
 */
@Mixin(method="prop")
public class IncomingInvoiceItem_orderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_orderItemLink(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public OrderItemInvoiceItemLink prop() {
        final Optional<OrderItemInvoiceItemLink> items = orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee);
        return items.orElse(null);
    }
}
