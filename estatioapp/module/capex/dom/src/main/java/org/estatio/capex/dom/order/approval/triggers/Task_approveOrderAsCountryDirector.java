package org.estatio.capex.dom.order.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approveOrderAsCountryDirector
        extends Task_mixinOrderAbstract<Order_approveAsCountryDirector> {

    protected final Task task;

    public Task_approveOrderAsCountryDirector(final Task task) {
        super(task, Order_approveAsCountryDirector.class);
        this.task = task;
    }

}
