package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.base.platform.applib.ReasonBuffer2;

/**
 * @see IncomingInvoiceItem_updateOrderItemLink
 */
@Mixin(method="act")
public class OrderItem_updateInvoiceItemLink extends OrderItem_abstractMixinInvoiceItemLinks {
    public OrderItem_updateInvoiceItemLink(final OrderItem mixee) {
        super(mixee);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "invoiceItemLinks", sequence = "1")
    public OrderItem act(
            final IncomingInvoiceItem invoiceItem,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal netAmount){
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(mixee, invoiceItem);
        if(link != null) {
            link.setNetAmount(netAmount);
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

    public BigDecimal default1Act(){
        final List<OrderItemInvoiceItemLink> orderItemLinks =
                orderItemInvoiceItemLinkRepository.findByOrderItem(mixee);
        return orderItemLinks.size() == 1 ? orderItemLinks.get(0).getNetAmount(): null;
    }

    public String validate0Act(final IncomingInvoiceItem invoiceItem) {

        ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot link to this invoice item");

        buf.append(() -> invoiceItem.getCharge() != mixee.getCharge(), "charge is different");
        buf.append(() -> invoiceItem.getProject() != mixee.getProject(), "project is different");
        buf.append(() -> invoiceItem.getFixedAsset() != mixee.getProperty(), "property is different");

        return buf.getReason();
    }

    public String validateAct(final IncomingInvoiceItem invoiceItem, final BigDecimal proposedNetAmount) {
        final OrderItemInvoiceItemLink link = orderItemInvoiceItemLinkRepository.findUnique(mixee, invoiceItem);
        final BigDecimal currentNetAmount = link != null ? link.getNetAmount() : BigDecimal.ZERO; // should be there.
        return validateLinkAmount(currentNetAmount, proposedNetAmount, invoiceItem);
    }

}
