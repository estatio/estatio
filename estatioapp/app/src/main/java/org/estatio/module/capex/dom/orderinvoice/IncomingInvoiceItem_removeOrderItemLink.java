package org.estatio.module.capex.dom.orderinvoice;

import java.util.Optional;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.base.platform.applib.ReasonBuffer2;

/**
 * @see OrderItem_removeInvoiceItemLink
 */
@Mixin(method="act")
public class IncomingInvoiceItem_removeOrderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_removeOrderItemLink(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @MemberOrder(name = "orderItem", sequence = "3")
    public IncomingInvoiceItem act(){
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(linkedOrderItem(), mixee);
        if(link != null) {
            link.remove();
        }
        return mixee;
    }
    public String disableAct() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();

        buf.append(() -> ! orderItemIfAny().isPresent(), "Not linked to an order item");
        buf.append(() -> mixee.getReportedDate() != null, "Invoice item has been reported");
        buf.append(() -> mixee.getReversalOf() != null, "Invoice item is a reversal");

        return buf.getReason();
    }

    private OrderItem linkedOrderItem() {
        return orderItemIfAny().orElse(null);
    }

    private Optional<OrderItem> orderItemIfAny() {
        return orderItemInvoiceItemLinkRepository
                .findLinkedOrderItemsByInvoiceItem(mixee);
    }

}
