package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.base.platform.applib.ReasonBuffer2;

/**
 * @see OrderItem_updateInvoiceItemLink
 */
@Mixin(method="act")
public class IncomingInvoiceItem_updateOrderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_updateOrderItemLink(final IncomingInvoiceItem mixee) {
        super(mixee);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "linkedAmount", sequence = "1")
    public IncomingInvoiceItem act(
            @Digits(integer = 13, fraction = 2)
            final BigDecimal netAmount) {

        linkIfAny().ifPresent(link -> link.setNetAmount(netAmount));

        return mixee;
    }

    public String disableAct() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();

        buf.append(() -> ! orderItemIfAny().isPresent(), "Not linked to an order item");
        buf.append(() -> mixee.getReportedDate() != null, "Invoice item has been reported");
        buf.append(() -> mixee.getReversalOf() != null, "Invoice item is a reversal");

        return buf.getReason();
    }

    public BigDecimal default0Act(){
        return linkIfAny().map(OrderItemInvoiceItemLink::getNetAmount).orElse(null);
    }

    public String validateAct(final BigDecimal proposedNetAmount) {

        final OrderItem orderItem = linkedOrderItem();

        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(orderItem, mixee);
        final BigDecimal currentNetAmount = link != null ? link.getNetAmount() : BigDecimal.ZERO; // should be there.
        return validateLinkAmount(currentNetAmount, proposedNetAmount);
    }

    private OrderItem linkedOrderItem() {
        return orderItemIfAny().orElse(null);
    }

    private Optional<OrderItem> orderItemIfAny() {
        return orderItemInvoiceItemLinkRepository.findLinkedOrderItemsByInvoiceItem(mixee);
    }

    private Optional<OrderItemInvoiceItemLink> linkIfAny() {
        return orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee);
    }



}
