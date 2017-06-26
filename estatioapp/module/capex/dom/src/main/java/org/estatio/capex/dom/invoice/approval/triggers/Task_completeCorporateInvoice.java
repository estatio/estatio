package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_completeCorporateInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_completeCorporateInvoice> {

    protected final Task task;

    public Task_completeCorporateInvoice(final Task task) {
        super(task, IncomingInvoice_completeCorporateInvoice.class);
        this.task = task;
    }


}
