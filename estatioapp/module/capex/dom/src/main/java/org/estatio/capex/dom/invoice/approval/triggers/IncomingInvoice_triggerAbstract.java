package org.estatio.capex.dom.invoice.approval.triggers;

import javax.inject.Inject;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.triggers.DomainObject_triggerAbstract;

public abstract class IncomingInvoice_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    IncomingInvoice,
                                    IncomingInvoiceApprovalStateTransition,
                                    IncomingInvoiceApprovalStateTransitionType,
                                    IncomingInvoiceApprovalState> {

    public static abstract class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return IncomingInvoiceApprovalStateTransition.class;
        }
    }

    protected IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransitionType requiredTransitionType) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType
        );
    }

    @Override
    protected IncomingInvoiceApprovalStateTransition findByTask(final Task previousTask) {
        return stateTransitionRepository.findByTask(previousTask);
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository stateTransitionRepository;


}
