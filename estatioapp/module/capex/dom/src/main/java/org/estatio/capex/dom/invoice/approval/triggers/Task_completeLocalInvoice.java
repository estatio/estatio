package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_completeLocalInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_completeLocalInvoice> {

    protected final Task task;

    public Task_completeLocalInvoice(final Task task) {
        super(task, IncomingInvoice_completeLocalInvoice.class);
        this.task = task;
    }


}
