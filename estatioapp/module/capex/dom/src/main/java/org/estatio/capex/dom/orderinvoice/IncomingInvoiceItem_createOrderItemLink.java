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
public class IncomingInvoiceItem_createOrderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_createOrderItemLink(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "orderItemLinks", sequence = "1")
    public IncomingInvoiceItem act(
            final OrderItem orderItem,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal netAmount){
        orderItemInvoiceItemLinkRepository.createLink(orderItem, mixee, netAmount);
        return mixee;
    }

    public OrderItem default0Act(){
        final List<OrderItemInvoiceItemLink> links = orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee);
        return links.size() > 0 ? links.get(0).getOrderItem() : null;
    }

    public List<OrderItem> choices0Act(){

        // the disable guard ensures this is non-null
        final Party seller = mixee.getInvoice().getSeller();

        final List<OrderItem> orderItems = orderItemRepository.findBySeller(seller);
        orderItems.removeAll(orderItemInvoiceItemLinkRepository.findLinkedOrderItemsByInvoiceItem(mixee));
        return orderItems;
    }
    public String disableAct(){
        if(mixee.getInvoice().getSeller() == null) {
            return "Invoice's seller is required before items can be linked";
        }
        if(orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedFromInvoiceItem(mixee).compareTo(mixee.getNetAmount()) >= 0) {
            return "The net amount for this invoice item has already been linked to other order items";
        }
        return null;
    }

    public String validate0Act(final OrderItem orderItem) {
        return linkValidationService.validateOrderItem(orderItem, mixee);
    }

    public BigDecimal default1Act(){
        final BigDecimal netAmountNotLinked =
                orderItemInvoiceItemLinkRepository.calculateNetAmountNotLinkedFromInvoiceItem(mixee);
        return netAmountNotLinked;
    }

    public String validate1Act(final BigDecimal netAmount) {
        return validateLinkAmount(BigDecimal.ZERO, netAmount);
    }

}
