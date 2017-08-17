package org.estatio.capex.dom.order.approval;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.party.Party;

/**
 * This cannot be inlined (must be a mixin) because Party does not know about Orders.
 */
@Mixin
public class Party_orders {

    private final Party party;

    public Party_orders(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Order> $$() {
        return orderRepository.findBySellerParty(party);
    }

    @Inject
    OrderRepository orderRepository;
}
