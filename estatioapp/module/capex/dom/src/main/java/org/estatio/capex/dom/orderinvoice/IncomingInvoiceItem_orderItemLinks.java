package org.estatio.capex.dom.orderinvoice;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;

@Mixin(method="coll")
public class IncomingInvoiceItem_orderItemLinks extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_orderItemLinks(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public List<OrderItemInvoiceItemLink> coll() {
        return orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee);
    }
}
