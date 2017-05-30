package org.estatio.capex.dom.documents.invoice;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Task_classifyAsInvoice extends Task_classifyAsAbstract {

    protected final Task task;

    public Task_classifyAsInvoice(final Task task) {
        super(task, DocumentTypeData.INCOMING_INVOICE);
        this.task = task;
    }

    protected IncomingInvoiceViewModel doCreate() {
        return new IncomingInvoiceViewModel(getDocument());
    }

}
