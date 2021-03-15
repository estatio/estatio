package org.estatio.module.capex.dom.order.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.module.task.dom.dobj.DomainObject_nextTaskTransitionTypeAbstract;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="prop")
public class Order_nextApprovalTaskTransitionType
        extends DomainObject_nextTaskTransitionTypeAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    public Order_nextApprovalTaskTransitionType(final Order order) {
        super(order, OrderApprovalStateTransition.class);
    }

    public boolean hideProp() {
        return super.hideProp();
    }

}
