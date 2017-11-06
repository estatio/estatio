package org.estatio.capex.dom.order.approval;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.asset.Property;
import org.estatio.module.party.dom.Party;

/**
 * This cannot be inlined (must be a mixin) because Party does not know about Orders.
 */
@Mixin
public class Party_findByProperty {

    private final Party party;

    public Party_findByProperty(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<Order> $$(final Property property) {
        return orderRepository
                .findBySellerParty(party)
                .stream()
                .filter(x->x.getProperty()!=null && x.getProperty().equals(property))
                .collect(Collectors.toList());
    }

    @Inject
    OrderRepository orderRepository;
}
