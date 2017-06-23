package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approveAsAssetManager
        extends
        Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approveAsAssetManager> {

    protected final Task task;

    public Task_approveAsAssetManager(final Task task) {
        super(task, IncomingInvoice_approveAsAssetManager.class);
        this.task = task;
    }


}
