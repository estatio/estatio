package org.estatio.capex.dom.invoice.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceState;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class IncomingInvoice_newTask
        implements NewTaskMixin<IncomingInvoice, IncomingInvoiceState, IncomingInvoiceTransition> {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_newTask(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action()
    @MemberOrder(name = "tasks", sequence = "1")
    @Override
    public Task<?> newTask(
            final EstatioRole assignTo,
            final String description,
            final IncomingInvoiceTransition taskTransition) {
        return repository.create(incomingInvoice, taskTransition, assignTo, description);
    }

    @Inject
    TaskForIncomingInvoiceRepository repository;

}
