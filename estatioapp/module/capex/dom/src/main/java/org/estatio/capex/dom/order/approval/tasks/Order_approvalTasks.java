package org.estatio.capex.dom.order.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_tasksAbstract;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "coll")
public class Order_approvalTasks extends
        DomainObject_tasksAbstract<
                Order,
                OrderApprovalStateTransition,
                OrderApprovalStateTransitionType,
                OrderApprovalState> {

    public Order_approvalTasks(final Order order) {
        super(order, OrderApprovalStateTransition.class);
    }

}
