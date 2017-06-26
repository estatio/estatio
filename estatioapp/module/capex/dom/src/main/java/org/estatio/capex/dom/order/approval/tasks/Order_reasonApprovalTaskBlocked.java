package org.estatio.capex.dom.order.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_reasonGuardNotSatisfiedAbstract;

@Mixin(method="prop")
public class Order_reasonApprovalTaskBlocked
        extends DomainObject_reasonGuardNotSatisfiedAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    public Order_reasonApprovalTaskBlocked(final Order order) {
        super(order, OrderApprovalStateTransition.class);
    }

}
