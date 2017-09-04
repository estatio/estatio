package org.estatio.capex.dom.order.approval.triggers;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
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


    protected Order nextAfterPendingIfRequested(final boolean goToNext) {
        final Order nextOrder = goToNext ? nextAfterPending() : null;
        return coalesce(nextOrder, getDomainObject());
    }

    protected Order nextAfterPending() {
        return queryResultsCache.execute(
                this::doNextAfterPending, getClass(), "nextAfterPending", getDomainObject());
    }

    private Order doNextAfterPending() {
        final OrderApprovalStateTransition pendingTransition = stateTransitionService
                .pendingTransitionOf(getDomainObject(), OrderApprovalStateTransition.class);
        final Order nextOrder = nextOrderViaTask(pendingTransition);
        return coalesce(nextOrder, getDomainObject());
    }

    private Order nextOrderViaTask(final OrderApprovalStateTransition transition) {
        if(transition == null) {
            return null;
        }
        final Task task = transition.getTask();
        if (task == null) {
            return null;
        }

        final Task nextTask = taskRepository.nextTaskAfter(task);
        if(nextTask == null) {
            return null;
        }
        final OrderApprovalStateTransition nextTransition = stateTransitionRepository.findByTask(nextTask);
        if(nextTransition == null) {
            return null;
        }
        return nextTransition.getDomainObject();
    }


    protected Order previousBeforePending() {
        return queryResultsCache.execute(
                this::doPreviousBeforePending, getClass(), "previousBeforePending", getDomainObject());
    }

    private Order doPreviousBeforePending() {
        final OrderApprovalStateTransition pendingTransition = stateTransitionService
                .pendingTransitionOf(getDomainObject(), OrderApprovalStateTransition.class);
        final Order previousOrder = previousViaTask(pendingTransition);
        return coalesce(previousOrder, getDomainObject());
    }

    private Order previousViaTask(final OrderApprovalStateTransition transition) {
        if(transition == null) {
            return null;
        }
        final Task task = transition.getTask();
        if (task == null) {
            return null;
        }

        final Task previousTask = taskRepository.previousTaskBefore(task);
        if(previousTask == null) {
            return null;
        }
        final OrderApprovalStateTransition previousTransition = stateTransitionRepository.findByTask(previousTask);
        if(previousTransition == null) {
            return null;
        }
        return previousTransition.getDomainObject();
    }

    private static <T> T coalesce(final T... candidates) {
        for (T candidate : candidates) {
            if(candidate != null) {
                return candidate;
            }
        }
        return null;
    }



    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    TaskRepository taskRepository;

    @Inject
    OrderApprovalStateTransition.Repository stateTransitionRepository;


}
