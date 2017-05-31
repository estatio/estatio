package org.estatio.capex.dom.documents.categorisation.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.documents.categorisation.order.IncomingOrderViewModel;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Task_classifyAsOrder extends Task_classifyAsAbstract {

    protected final Task task;

    public Task_classifyAsOrder(final Task task) {
        super(task, DocumentTypeData.INCOMING_ORDER);
        this.task = task;
    }

    protected IncomingOrderViewModel doCreate() {
        return new IncomingOrderViewModel(getDocument());
    }

}
