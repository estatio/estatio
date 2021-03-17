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
public class Order_next extends Order_triggerAbstract {

    private final Order order;

    public Order_next(Order order) {
        super(order, OrderApprovalStateTransitionType.COMPLETE_WITH_APPROVAL);
        this.order = order;
    }

    public static class ActionDomainEvent extends Order_triggerAbstract.ActionDomainEvent<Order_next> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    public Object act() {
        final Order next = nextAfterPending();
        return objectToReturn(next);
    }

    public String disableAct() {
        return nextAfterPending() == getDomainObject()
                ? "Could not find next order; either this order has no pending task, or there are none after"
                : null;
    }

    protected Object objectToReturn(final Order order) {
        return order;
    }

}
