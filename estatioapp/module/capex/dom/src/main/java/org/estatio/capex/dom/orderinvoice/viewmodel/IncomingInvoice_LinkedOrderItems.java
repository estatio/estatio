package org.estatio.capex.dom.orderinvoice.viewmodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

@Mixin
public class IncomingInvoice_LinkedOrderItems {

    private IncomingInvoice incomingInvoice;
    public IncomingInvoice_LinkedOrderItems(IncomingInvoice incomingInvoice){
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<LinkedOrderItemViewModel> linkedOrderItems() {
        List<LinkedOrderItemViewModel> result = new ArrayList<>();
        for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByInvoice(incomingInvoice)){
            OrderItem item = link.getOrderItem();
            BigDecimal netAmount = item.getNetAmount();
            BigDecimal netAmountInvoiced = orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedToOrderItem(item);
            result.add(new LinkedOrderItemViewModel(item, netAmount, netAmountInvoiced));
        }
        return  result;
    }

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
