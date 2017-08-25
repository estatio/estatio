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
import org.estatio.dom.party.Party;

@Mixin(method="act")
public class OrderItem_createInvoiceItemLink extends OrderItem_abstractMixinInvoiceItemLinks {

    public OrderItem_createInvoiceItemLink(final OrderItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "invoiceItemLinks", sequence = "1")
    public OrderItem act(
            final IncomingInvoiceItem invoiceItem,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal netAmount){
        orderItemInvoiceItemLinkRepository.createLink(mixee, invoiceItem, netAmount);
        return mixee;
    }

    public IncomingInvoiceItem default0Act(){
        final List<OrderItemInvoiceItemLink> links = orderItemInvoiceItemLinkRepository.findByOrderItem(mixee);
        return links.size() > 0 ? links.get(0).getInvoiceItem() : null;
    }

    public List<IncomingInvoiceItem> choices0Act(){

        // the disable guard ensures this is non-null
        final Party seller = mixee.getOrdr().getSeller();

        // candidates
        final List<IncomingInvoiceItem> invoiceItems = invoiceItemRepository.findBySeller(seller);

        // exclude any invoice items already linked to this order
        invoiceItems.removeAll(orderItemInvoiceItemLinkRepository.findLinkedInvoiceItemsByOrderItem(mixee));



        return invoiceItems;
    }

    public String disableAct(){
        if(mixee.getOrdr().getSeller() == null) {
            return "Order's seller is required before items can be linked";
        }
        return null;
    }

    public String validate0Act(final IncomingInvoiceItem invoiceItem) {

        if(orderItemInvoiceItemLinkRepository.calculateNetAmountNotLinkedFromInvoiceItem(invoiceItem).compareTo(BigDecimal.ZERO) <= 0) {
            return "The net amount for this invoice item has already been linked to other order items";
        }

        return linkValidationService.validateOrderItem(mixee, invoiceItem);
    }

    public String validateAct(final IncomingInvoiceItem incomingInvoiceItem, final BigDecimal netAmount) {
        return validateLinkAmount(BigDecimal.ZERO, netAmount, incomingInvoiceItem);
    }

}
