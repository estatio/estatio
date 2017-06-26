package org.estatio.capex.dom.order.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_tasksAbstract;

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
