package org.estatio.capex.dom.order.approval.triggers;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.Person;

@Mixin(method="act")
public class Order_completeWithApproval extends
        Order_triggerAbstract {

    private final Order order;

    public Order_completeWithApproval(Order order) {
        super(order, OrderApprovalStateTransitionType.COMPLETE_WITH_APPROVAL);
        this.order = order;
    }

    @Action()
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public Order act(
            Person approvedBy,
            LocalDate approvedOn,
            @Nullable final String comment) {
        order.setApprovedBy(approvedBy.getReference());
        order.setApprovedOn(approvedOn);
        trigger(comment);
        return getDomainObject();
    }

    public List<Person> autoComplete0Act(@MinLength(3) final String searchPhrase) {
        return partyRepository.autoComplete(searchPhrase)
                .stream()
                .filter(Person.class::isInstance)
                .map(Person.class::cast).collect(
                Collectors.toList());
    }

    public String validate1Act(LocalDate approvedOn) {
        if(approvedOn == null) {
            return null;
        }
        if(clockService.now().isBefore(approvedOn)) {
            return "Cannot approve in the future";
        }
        return null;
    }


    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    @Inject
    PartyRepository partyRepository;
    @Inject
    ClockService clockService;

}
