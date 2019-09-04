package org.estatio.module.capex.dom.order.contributions.buyer;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.buyer.OrderSecondaryBuyerLinkRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;

@Mixin
public class Order_addSecondaryBuyer {

    private final Order order;

    public Order_addSecondaryBuyer(final Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Order addSecondaryBuyer(final Party buyer) {
        orderSecondaryBuyerLinkRepository.createLink(order, buyer);
        return order;
    }

    public List<Party> choices0AddSecondaryBuyer() {
        return partyRepository.findByRoleTypeAndAtPath(IncomingInvoiceRoleTypeEnum.ECP, "/ITA");
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

    @Inject
    PartyRepository partyRepository;

}
