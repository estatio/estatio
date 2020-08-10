package org.estatio.module.capex.dom.invoice.accountingaudit.triggers;

import javax.inject.Inject;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransition;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.task.dom.task.Task_mixinActAbstract;

public abstract class Task_mixinIncomingInvoiceAbstract<M>
        extends
        Task_mixinActAbstract<M, IncomingInvoice> {

    public static abstract class ActionDomainEvent<MIXIN>
            extends Task_mixinActAbstract.ActionDomainEvent<MIXIN> {
        public Class<?> getStateTransitionClass() {
            return IncomingInvoiceAccountingStateTransition.class;
        }
    }

    protected final Task task;

    public Task_mixinIncomingInvoiceAbstract(final Task task, final Class<M> mixinClass) {
        super(task, mixinClass);
        this.task = task;
    }


    @Override
    protected IncomingInvoice doGetDomainObjectIfAny() {
        final IncomingInvoiceAccountingStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getInvoice() : null;
    }

    @Inject
    IncomingInvoiceAccountingStateTransition.Repository repository;

}
