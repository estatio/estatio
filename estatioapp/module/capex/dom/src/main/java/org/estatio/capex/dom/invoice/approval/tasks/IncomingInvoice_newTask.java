package org.estatio.capex.dom.invoice.approval.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionChart;
import org.estatio.capex.dom.task.NewTaskMixin;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class IncomingInvoice_newTask
        implements NewTaskMixin<IncomingInvoice, IncomingInvoiceApprovalStateTransition, IncomingInvoiceApprovalStateTransitionChart, IncomingInvoiceApprovalState> {

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
            final IncomingInvoiceApprovalStateTransitionChart transitionType,
            @Nullable
            final String description) {
        final IncomingInvoiceApprovalState currState = supportService.currentStateOf(incomingInvoice);

        final String taskDescription = Enums.getFriendlyNameOf(transitionType);
        final IncomingInvoiceApprovalStateTransition stateTransition =
                repository.create(incomingInvoice, transitionType, currState, assignTo, taskDescription);
        return stateTransition.getTask();
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository repository;
    @Inject
    IncomingInvoiceApprovalStateTransitionChart.SupportService supportService;

}
