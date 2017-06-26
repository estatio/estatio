package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approveAsProjectManager
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approveAsProjectManager> {

    protected final Task task;

    public Task_approveAsProjectManager(final Task task) {
        super(task, IncomingInvoice_approveAsProjectManager.class);
        this.task = task;
    }

}
