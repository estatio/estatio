package org.estatio.capex.dom.orderinvoice;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;

@Mixin(method="act")
public class IncomingInvoiceItem_removeOrderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_removeOrderItemLink(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(name = "orderItemLinks", sequence = "3")
    public IncomingInvoiceItem act(
            @Nullable
            final OrderItem orderItem){
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(orderItem, mixee);
        if(link != null) {
            link.remove();
        }
        return mixee;
    }
    public String disableAct() {
        return choices0Act().isEmpty()? "No order items" : null;
    }

    public OrderItem default0Act() {
        final List<OrderItem> orderItems = choices0Act();
        return orderItems.size() == 1 ? orderItems.get(0): null;
    }

    public List<OrderItem> choices0Act() {
        return orderItemInvoiceItemLinkRepository.findLinkedOrderItemsByInvoiceItem(mixee);
    }
}
