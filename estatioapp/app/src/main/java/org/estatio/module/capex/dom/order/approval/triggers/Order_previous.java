package org.estatio.module.capex.dom.order.approval.triggers;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass.
 */
@Mixin(method = "act")
public class Order_previous extends Order_triggerAbstract {

    private final Order order;

    public Order_previous(Order order) {
        super(order, OrderApprovalStateTransitionType.COMPLETE_WITH_APPROVAL);
        this.order = order;
    }

    public static class ActionDomainEvent extends Order_triggerAbstract.ActionDomainEvent<Order_next> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    public Object act() {
        final Order previous = previousBeforePending();
        return objectToReturn(previous);
    }

    public String disableAct() {
        return previousBeforePending() == getDomainObject()
                ? "Could not find previous order; either this order has no pending task, or there are none prior"
                : null;
    }

    protected Object objectToReturn(final Order order) {
        return order;
    }

}
