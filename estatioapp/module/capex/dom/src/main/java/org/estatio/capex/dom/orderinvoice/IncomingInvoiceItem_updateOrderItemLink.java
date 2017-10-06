package org.estatio.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.dom.utils.ReasonBuffer2;

/**
 * @see OrderItem_updateInvoiceItemLink
 */
@Mixin(method="act")
public class IncomingInvoiceItem_updateOrderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_updateOrderItemLink(final IncomingInvoiceItem mixee) {
        super(mixee);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "orderItemLinks", sequence = "1")
    public IncomingInvoiceItem act(
            final OrderItem orderItem,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal netAmount){
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(orderItem, mixee);
        if(link != null) {
            link.setNetAmount(netAmount);
        }
        return mixee;
    }

    public String disableAct() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();

        buf.append(choices0Act().isEmpty(), "There are no links to order items");
        buf.append(mixee.getReportedDate() != null, "Invoice item has been reported");
        buf.append(mixee.getReversalOf() != null, "Invoice item is a reversal");

        return buf.getReason();
    }

    public OrderItem default0Act() {
        final List<OrderItem> orderItems = choices0Act();
        return orderItems.size() == 1 ? orderItems.get(0): null;
    }

    public List<OrderItem> choices0Act() {
        return orderItemInvoiceItemLinkRepository.findLinkedOrderItemsByInvoiceItem(mixee);
    }

    public BigDecimal default1Act(){
        final List<OrderItemInvoiceItemLink> orderItemLinks =
                orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee);
        return orderItemLinks.size() == 1 ? orderItemLinks.get(0).getNetAmount(): null;
    }

    public String validate0Act(final OrderItem orderItem) {
        return linkValidationService.validateOrderItem(orderItem, mixee);
    }

    public String validateAct(final OrderItem orderItem, final BigDecimal proposedNetAmount) {
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(orderItem, mixee);
        final BigDecimal currentNetAmount = link != null ? link.getNetAmount() : BigDecimal.ZERO; // should be there.
        return validateLinkAmount(currentNetAmount, proposedNetAmount);
    }

}
