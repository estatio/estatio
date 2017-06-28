package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;

public abstract class Task_mixinIncomingInvoiceAbstract<M extends IncomingInvoice_triggerAbstract>
        extends
        Task_mixinActAbstract<M, IncomingInvoice> {

    protected final Task task;

    public Task_mixinIncomingInvoiceAbstract(final Task task, final Class<M> mixinClass) {
        super(task, mixinClass);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(comment);
        return toReturnElse(goToNext, mixinResult);
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    @Override
    protected IncomingInvoice doGetDomainObjectIfAny() {
        final IncomingInvoiceApprovalStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getInvoice() : null;
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository repository;

}
