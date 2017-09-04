package org.estatio.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

public abstract class IncomingInvoice_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    IncomingInvoice,
                                    IncomingInvoiceApprovalStateTransition,
                                    IncomingInvoiceApprovalStateTransitionType,
                                    IncomingInvoiceApprovalState> {

    protected IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final List<IncomingInvoiceApprovalState> fromStates,
            final IncomingInvoiceApprovalStateTransitionType requiredTransitionTypeIfAny) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, fromStates, requiredTransitionTypeIfAny
        );
    }

    protected IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransitionType requiredTransitionType) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType
        );
    }

    protected IncomingInvoice nextAfterPendingIfRequested(final boolean goToNext) {
        final IncomingInvoice nextObj = goToNext ? nextAfterPending() : null;
        return coalesce(nextObj, getDomainObject());
    }

    protected IncomingInvoice nextAfterPending() {
        return queryResultsCache.execute(
                this::doNextAfterPending, getClass(), "nextAfterPending", getDomainObject());
    }

    private IncomingInvoice doNextAfterPending() {
        final IncomingInvoiceApprovalStateTransition pendingTransition = stateTransitionService
                .pendingTransitionOf(getDomainObject(), IncomingInvoiceApprovalStateTransition.class);
        final IncomingInvoice nextInvoice = nextViaTask(pendingTransition);
        return coalesce(nextInvoice, getDomainObject());
    }

    private IncomingInvoice nextViaTask(final IncomingInvoiceApprovalStateTransition transition) {
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
        final IncomingInvoiceApprovalStateTransition nextTransition = stateTransitionRepository.findByTask(nextTask);
        if(nextTransition == null) {
            return null;
        }
        return nextTransition.getDomainObject();
    }


    protected IncomingInvoice previousBeforePending() {
        return queryResultsCache.execute(
                this::doPreviousBeforePending, getClass(), "previousBeforePending", getDomainObject());
    }

    private IncomingInvoice doPreviousBeforePending() {
        final IncomingInvoiceApprovalStateTransition pendingTransition = stateTransitionService
                .pendingTransitionOf(getDomainObject(), IncomingInvoiceApprovalStateTransition.class);
        final IncomingInvoice previousInvoice = previousViaTask(pendingTransition);
        return coalesce(previousInvoice, getDomainObject());
    }

    private IncomingInvoice previousViaTask(final IncomingInvoiceApprovalStateTransition transition) {
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
        final IncomingInvoiceApprovalStateTransition previousTransition = stateTransitionRepository.findByTask(previousTask);
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
    IncomingInvoiceApprovalStateTransition.Repository stateTransitionRepository;


}
