package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approveAsCountryDirector
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approveAsCountryDirector> {

    protected final Task task;

    public Task_approveAsCountryDirector(final Task task) {
        super(task, IncomingInvoice_approveAsCountryDirector.class);
        this.task = task;
    }

}
