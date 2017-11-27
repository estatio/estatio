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
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.party.dom.Party;

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
        return orderRepository
                .findBySellerParty(party)
                .stream()
                .filter(x->x.getApprovalState()==null || x.getApprovalState()!= OrderApprovalState.DISCARDED)
                .collect(Collectors.toList());
    }

    @Inject
    OrderRepository orderRepository;
}
