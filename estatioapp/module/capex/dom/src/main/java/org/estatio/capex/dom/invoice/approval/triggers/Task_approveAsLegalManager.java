package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approveAsLegalManager
        extends
        Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approveAsLegalManager> {

    protected final Task task;

    public Task_approveAsLegalManager(final Task task) {
        super(task, IncomingInvoice_approveAsLegalManager.class);
        this.task = task;
    }


}
