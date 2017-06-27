package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approve
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approve> {

    protected final Task task;

    public Task_approve(final Task task) {
        super(task, IncomingInvoice_approve.class);
        this.task = task;
    }

}
