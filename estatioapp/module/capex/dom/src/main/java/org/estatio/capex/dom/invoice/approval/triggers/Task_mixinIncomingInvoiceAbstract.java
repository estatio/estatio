package org.estatio.capex.dom.invoice.approval.triggers;

import javax.inject.Inject;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;

public abstract class Task_mixinIncomingInvoiceAbstract<M>
        extends
        Task_mixinActAbstract<M, IncomingInvoice> {

    protected final Task task;

    public Task_mixinIncomingInvoiceAbstract(final Task task, final Class<M> mixinClass) {
        super(task, mixinClass);
        this.task = task;
    }


    @Override
    protected IncomingInvoice doGetDomainObjectIfAny() {
        final IncomingInvoiceApprovalStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getInvoice() : null;
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository repository;

}
