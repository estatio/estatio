package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_completePropertyInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_completePropertyInvoice> {

    protected final Task task;

    public Task_completePropertyInvoice(final Task task) {
        super(task, IncomingInvoice_completePropertyInvoice.class);
        this.task = task;
    }


}
