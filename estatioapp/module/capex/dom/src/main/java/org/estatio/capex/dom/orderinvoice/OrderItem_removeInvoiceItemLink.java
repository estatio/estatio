package org.estatio.capex.dom.orderinvoice;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;

/**
 * @see IncomingInvoiceItem_removeOrderItemLink
 */
@Mixin(method="act")
public class OrderItem_removeInvoiceItemLink extends OrderItem_abstractMixinInvoiceItemLinks {
    public OrderItem_removeInvoiceItemLink(final OrderItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(name = "invoiceItemLinks", sequence = "3")
    public OrderItem act(
            @Nullable
            final IncomingInvoiceItem invoiceItem){
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(mixee, invoiceItem);
        if(link != null) {
            link.remove();
        }
        return mixee;
    }
    public String disableAct() {
        return choices0Act().isEmpty()? "No invoice items" : null;
    }

    public IncomingInvoiceItem default0Act() {
        final List<IncomingInvoiceItem> invoiceItems = choices0Act();
        return invoiceItems.size() == 1 ? invoiceItems.get(0): null;
    }

    public List<IncomingInvoiceItem> choices0Act() {
        return orderItemInvoiceItemLinkRepository.findLinkedInvoiceItemsByOrderItemAsStream(mixee)
                .filter(x -> x.getReportedDate() == null) // ignore items those that have been reported
                .filter(x -> x.getReversalOf() == null) // ignore reversals
                .collect(Collectors.toList());
    }
}
