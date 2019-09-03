package org.estatio.module.capex.dom.order.contributions.buyer;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.buyer.OrderSecondaryBuyerLink;
import org.estatio.module.capex.dom.order.buyer.OrderSecondaryBuyerLinkRepository;
import org.estatio.module.party.dom.Party;

@Mixin
public class Order_secondaryBuyer {

    private final Order order;

    public Order_secondaryBuyer(final Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Party secondaryBuyer() {
        final OrderSecondaryBuyerLink link = orderSecondaryBuyerLinkRepository.findByOrder(order);
        return link != null ? link.getSecondaryBuyer() : null;
    }

    public boolean hideSecondaryBuyer() {
        return !order.getAtPath().startsWith("/ITA") || secondaryBuyer() == null;
    }

    @Inject OrderSecondaryBuyerLinkRepository orderSecondaryBuyerLinkRepository;

}
