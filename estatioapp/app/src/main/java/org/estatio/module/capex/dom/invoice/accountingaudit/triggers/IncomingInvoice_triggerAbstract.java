package org.estatio.module.capex.dom.invoice.accountingaudit.triggers;

import javax.inject.Inject;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingState;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransition;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.task.dom.triggers.DomainObject_triggerAbstract;

public abstract class IncomingInvoice_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    IncomingInvoice,
                                    IncomingInvoiceAccountingStateTransition,
                                    IncomingInvoiceAccountingStateTransitionType,
                                    IncomingInvoiceAccountingState> {

    public static abstract class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return IncomingInvoiceAccountingStateTransition.class;
        }
    }

    protected IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceAccountingStateTransitionType requiredTransitionType) {
        super(incomingInvoice, IncomingInvoiceAccountingStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType
        );
    }

    @Override
    protected IncomingInvoiceAccountingStateTransition findByTask(final Task previousTask) {
        return stateTransitionRepository.findByTask(previousTask);
    }

    @Inject
    IncomingInvoiceAccountingStateTransition.Repository stateTransitionRepository;


}
