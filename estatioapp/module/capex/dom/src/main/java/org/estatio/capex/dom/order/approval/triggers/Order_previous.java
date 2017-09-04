package org.estatio.capex.dom.order.approval.triggers;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;

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

    @Action(semantics = SemanticsOf.SAFE)
    public Object act() {
        final Order previous = previousOrderBeforePending();
        return objectToReturn(previous);
    }

    public String disableAct() {
        return previousOrderBeforePending() == getDomainObject()
                ? "Could not find previous order; either this order has no pending task, or there are none prior"
                : null;
    }

    protected Object objectToReturn(final Order order) {
        return order;
    }

}
