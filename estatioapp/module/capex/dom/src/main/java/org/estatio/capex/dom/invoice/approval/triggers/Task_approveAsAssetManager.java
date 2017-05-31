package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinAbstract;

@Mixin(method = "act")
public class Task_approveAsAssetManager
        extends
        Task_mixinAbstract<IncomingInvoice_approveAsAssetManager, IncomingInvoice> {

    protected final Task task;

    public Task_approveAsAssetManager(final Task task) {
        super(task, IncomingInvoice_approveAsAssetManager.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Task act(@Nullable final String comment, final boolean goToNext) {
        mixin().act(comment);
        return taskToReturn(goToNext, task);
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
