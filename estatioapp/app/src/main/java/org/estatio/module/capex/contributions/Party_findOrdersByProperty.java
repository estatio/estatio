package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.party.dom.Party;

/**
 * This cannot be inlined (must be a mixin) because Party does not know about Orders.
 */
@Mixin
public class Party_findOrdersByProperty {

    private final Party party;

    public Party_findOrdersByProperty(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<Order> $$(final Property property) {
        return orderRepository
                .findBySellerParty(party)
                .stream()
                .filter(x->(x.getProperty()!=null && x.getProperty().equals(property)) || orderItemsContainProperty(x, property))
                .collect(Collectors.toList());
    }

    private boolean orderItemsContainProperty(final Order order, final Property property){
        for (OrderItem orderItem : order.getItems()){
            if (orderItem.getProperty() == property){
                return true;
            }
        }
        return false;
    }

    @Inject
    OrderRepository orderRepository;
}
