package org.estatio.capex.dom.order.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

public abstract class Order_triggerAbstract
        extends DomainObject_triggerAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    Order_triggerAbstract(
            final Order order,
            final List<OrderApprovalState> fromStates,
            final OrderApprovalStateTransitionType requiredTransitionTypeIfAny) {
        super(order, OrderApprovalStateTransition.class, fromStates, requiredTransitionTypeIfAny);
    }

    Order_triggerAbstract(
            final Order order,
            final OrderApprovalStateTransitionType requiredTransitionType) {
        super(order, OrderApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType);
    }


}
