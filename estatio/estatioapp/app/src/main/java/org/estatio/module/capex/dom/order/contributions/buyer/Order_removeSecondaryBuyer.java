package org.estatio.module.capex.dom.order.contributions.buyer;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.buyer.OrderSecondaryBuyerLinkRepository;

@Mixin
public class Order_removeSecondaryBuyer {

    private final Order order;

    public Order_removeSecondaryBuyer(final Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Order removeSecondaryBuyer() {
        orderSecondaryBuyerLinkRepository.removeLink(order);
        return order;
    }

    @Inject
    OrderSecondaryBuyerLinkRepository orderSecondaryBuyerLinkRepository;

}
