package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.party.dom.Party;
import org.estatio.dom.utils.ReasonBuffer2;

/**
 * @see OrderItem_createInvoiceItemLink
 */
@Mixin(method="act")
public class IncomingInvoiceItem_createOrderItemLink extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_createOrderItemLink(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "orderItem", sequence = "1")
    public IncomingInvoiceItem act(
            final OrderItem orderItem,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal netAmount){
        orderItemInvoiceItemLinkRepository.createLink(orderItem, mixee, netAmount);
        mixee.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();
        return mixee;
    }

    public OrderItem default0Act(){
        final List<OrderItem> orderItems = choices0Act();
        return orderItems.size() == 1 ? orderItems.get(0) : null;
    }

    public List<OrderItem> choices0Act(){

        // the disable guard ensures this is non-null
        final Party seller = mixee.getInvoice().getSeller();

        final Property property = (Property) mixee.getFixedAsset();

        // candidates
        final List<OrderItem> orderItems;
        if (property==null) {
            orderItems = orderItemRepository.findBySeller(seller);
        } else {
            orderItems = orderItemRepository.findBySellerAndProperty(seller, property);
        }

        return orderItems;
    }
    public String disableAct(){
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();

        buf.append(() -> orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee).isPresent(),
                "Already linked to an order item");
        buf.append(() -> mixee.getInvoice().getSeller() == null,
                "Invoice's seller is required before items can be linked");

        return buf.getReason();
    }

    public String validate0Act(final OrderItem orderItem) {

        ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot link to this order item");

        buf.append(() -> mixee.getCharge() != orderItem.getCharge(), "charge is different");
        buf.append(() -> mixee.getProject() != orderItem.getProject(), "project is different");
        buf.append(() -> mixee.getFixedAsset() != orderItem.getProperty(), "property is different");

        return buf.getReason();
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
