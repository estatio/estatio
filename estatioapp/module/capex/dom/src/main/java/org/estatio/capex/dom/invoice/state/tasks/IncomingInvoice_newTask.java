package org.estatio.capex.dom.invoice.state.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceState;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionRepository;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionType;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionTypeSupportService;
import org.estatio.capex.dom.task.NewTaskMixin;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class IncomingInvoice_newTask
        implements NewTaskMixin<IncomingInvoice, IncomingInvoiceStateTransition, IncomingInvoiceStateTransitionType, IncomingInvoiceState> {

    public static class ActionDomainEvent extends EstatioCapexDomModule.ActionDomainEvent<IncomingInvoice_newTask> { }

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_newTask(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(
            domainEvent = ActionDomainEvent.class
    )
    @MemberOrder(name = "tasks", sequence = "1")
    @Override
    public Task newTask(
            final EstatioRole assignTo,
            final IncomingInvoiceStateTransitionType transitionType,
            @Nullable
            final String description) {
        final IncomingInvoiceState currState = supportService.currentStateOf(incomingInvoice);

        final String taskDescription = Enums.getFriendlyNameOf(transitionType);
        final IncomingInvoiceStateTransition stateTransition =
                repository.create(incomingInvoice, transitionType, currState, assignTo, taskDescription);
        return stateTransition.getTask();
    }

    @Inject
    IncomingInvoiceStateTransitionRepository repository;
    @Inject
    IncomingInvoiceStateTransitionTypeSupportService supportService;

}
