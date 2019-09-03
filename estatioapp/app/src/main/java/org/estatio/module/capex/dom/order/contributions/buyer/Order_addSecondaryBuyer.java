package org.estatio.module.capex.dom.order.contributions.buyer;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.buyer.OrderSecondaryBuyerLinkRepository;
import org.estatio.module.party.dom.Party;

@Mixin
public class Order_addSecondaryBuyer {

    private final Order order;

    public Order_addSecondaryBuyer(final Order order) {
        this.order = order;
    }

    public Order addSecondaryBuyer(final Party buyer) {
        orderSecondaryBuyerLinkRepository.createLink(order, buyer);
        return order;
    }

    public boolean hideAddSecondaryBuyer() {
        return !order.getAtPath().startsWith("/ITA");
    }

    public String disableAddSecondaryBuyer() {
        return orderSecondaryBuyerLinkRepository.findByOrder(order) != null ?
                "A secondary buyer already exists for this order" :
                null;
    }

    @Inject
    OrderSecondaryBuyerLinkRepository orderSecondaryBuyerLinkRepository;

}
