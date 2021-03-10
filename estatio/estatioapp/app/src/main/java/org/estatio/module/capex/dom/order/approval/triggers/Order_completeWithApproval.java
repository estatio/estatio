package org.estatio.module.capex.dom.order.approval.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.module.party.dom.Person;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="act")
public class Order_completeWithApproval extends
        Order_triggerAbstract {

    private final Order order;

    public Order_completeWithApproval(Order order) {
        super(order, OrderApprovalStateTransitionType.COMPLETE_WITH_APPROVAL);
        this.order = order;
    }

    public static class ActionDomainEvent extends Order_triggerAbstract.ActionDomainEvent<Order_completeWithApproval> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public Order act(
            Person approvedBy,
            LocalDate approvedOn,
            @Nullable final String comment) {
        order.setApprovedBy(approvedBy.getReference());
        order.setApprovedOn(approvedOn);
        trigger(comment, comment);
        return getDomainObject();
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
    ClockService clockService;

}
