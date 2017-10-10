package org.estatio.capex.dom.order.itemmixins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.order.viewmodel.OrderItemPresentationViewmodel;
import org.estatio.dom.party.Organisation;

/**
 * TODO: inline this mixin
 */
@Mixin
public class IncomingDocAsInvoiceViewModel_candidateOrderItems {

    private final IncomingDocAsInvoiceViewModel vm;

    public IncomingDocAsInvoiceViewModel_candidateOrderItems(IncomingDocAsInvoiceViewModel vm) {
        this.vm = vm;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<OrderItemPresentationViewmodel> $$() {
        List<OrderItemPresentationViewmodel> result = new ArrayList<>();
        if (vm.getSeller() == null && vm.getProperty() == null){
            return result;
        }
        if (vm.getSeller()!=null){
            List<Order> ordersFromSeller = orderRepository.findBySeller((Organisation) vm.getSeller());
            for (Order order : ordersFromSeller){
                for (OrderItem orderItem : order.getItems()){
                    if (!orderItem.isInvoiced()){
                        result.add(new OrderItemPresentationViewmodel(orderItem));
                    }
                }
            }
        }
        if (vm.getProperty()!=null && vm.getSeller()==null){
            for (OrderItem orderItem : orderItemRepository.findByProperty(vm.getProperty())){
                if (!orderItem.isInvoiced()){
                    result.add(new OrderItemPresentationViewmodel(orderItem));
                }
            }
        }
        if (vm.getProject()!=null){
            result = result.stream().filter(x->x.getOrderItem().getProject()==vm.getProject()).collect(Collectors.toList());
        }
        if (vm.getCharge()!=null){
            result = result.stream().filter(x->x.getOrderItem().getCharge()==vm.getCharge()).collect(Collectors.toList());
        }
        return result;
    }

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderItemRepository orderItemRepository;

}
