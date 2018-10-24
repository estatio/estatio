package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.isisaddons.module.security.app.user.MeService;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

@Mixin
public class Order_changeCharge {

    private final Order order;

    public Order_changeCharge(Order order) {
        this.order = order;
    }

    @Action()
    public Order $$(final Charge newCharge) {
        order.getItems().forEach(item -> item.setCharge(newCharge));
        return order;
    }

    public List<Charge> choices0$$() {
        return order.getItems().first().getProject().getItems()
                .stream()
                .map(ProjectItem::getCharge)
                .collect(Collectors.toList());
    }

    public String disable$$() {
        return order.getItems().isEmpty() ? "There are no items on the order to update the charge on" : null;
    }

    public boolean hide$$() {
        if (!meService.me().getAtPath().startsWith("/ITA"))
            return true;

        return !personRepository.me().hasPartyRoleType(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER.findUsing(partyRoleTypeRepository));
    }

    @Inject
    private MeService meService;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PartyRoleTypeRepository partyRoleTypeRepository;

}
