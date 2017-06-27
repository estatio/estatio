package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_completeIncomingInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_complete> {

    protected final Task task;

    public Task_completeIncomingInvoice(final Task task) {
        super(task, IncomingInvoice_complete.class);
        this.task = task;
    }


}
