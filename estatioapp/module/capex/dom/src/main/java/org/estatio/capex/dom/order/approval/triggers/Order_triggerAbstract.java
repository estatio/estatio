package org.estatio.capex.dom.order.approval.triggers;

import javax.inject.Inject;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

public abstract class Order_triggerAbstract
        extends DomainObject_triggerAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    public static abstract  class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return OrderApprovalStateTransition.class;
        }
    }

    Order_triggerAbstract(
            final Order order,
            final OrderApprovalStateTransitionType requiredTransitionType) {
        super(order, OrderApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType);
    }

    @Override
    protected OrderApprovalStateTransition findByTask(final Task nextTask) {
        return stateTransitionRepository.findByTask(nextTask);
    }

    @Inject
    OrderApprovalStateTransition.Repository stateTransitionRepository;


}
