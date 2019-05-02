package org.estatio.module.capex.dom.order.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="act")
public class Order_amend extends
        Order_triggerAbstract {

    private final Order order;

    public Order_amend(Order order) {
        super(order, OrderApprovalStateTransitionType.AMEND);
        this.order = order;
    }

    public static class ActionDomainEvent extends Order_triggerAbstract.ActionDomainEvent<Order_amend> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-thumbs-o-down", cssClass = "btn-warning")
    public Order act(
            final IPartyRoleType role,
            @Nullable final Person personToAssignNextTo,
            final String reason) {
        trigger(role, personToAssignNextTo, reason, reason);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public IPartyRoleType default0Act() {
        return choices0Act().stream().findFirst().orElse(null);
    }

    public List<? extends IPartyRoleType> choices0Act() {
        return enumPartyRoleType();
    }

    public Person default1Act(final IPartyRoleType roleType) {
        return defaultPersonToAssignNextTo(roleType);
    }

    public List<Person> choices1Act(final IPartyRoleType roleType) {
        return choicesPersonToAssignNextTo(roleType);
    }

}
