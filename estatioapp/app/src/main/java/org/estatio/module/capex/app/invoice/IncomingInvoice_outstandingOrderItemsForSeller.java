package org.estatio.module.capex.app.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

@Mixin
public class IncomingInvoice_outstandingOrderItemsForSeller {

    private final IncomingInvoice invoice;

    public IncomingInvoice_outstandingOrderItemsForSeller(IncomingInvoice invoice) {
        this.invoice = invoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<OrderItemPresentationViewmodel> $$() {
        List<OrderItemPresentationViewmodel> result = new ArrayList<>();
        if (invoice.getSeller() == null && invoice.getProperty() == null){
            return result;
        }
        List<OrderItem> itemsFound = orderItemRepository.findBySeller(invoice.getSeller())
                .stream()
                .filter(linkedToInvoiceOrOutstanding)
                .collect(Collectors.toList());
        for (OrderItem item : itemsFound){
            result.add(new OrderItemPresentationViewmodel(item));
        }
        return result;
    }

    Predicate<OrderItem> linkedToInvoiceOrOutstanding = x->itemsLinked().contains(x) || !x.isInvoiced();

    private List<OrderItem> itemsLinked() {
        return orderItemInvoiceItemLinkRepository.findByInvoice(invoice)
                .stream()
                .map(x -> x.getOrderItem())
                .collect(Collectors.toList());
    }


    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
