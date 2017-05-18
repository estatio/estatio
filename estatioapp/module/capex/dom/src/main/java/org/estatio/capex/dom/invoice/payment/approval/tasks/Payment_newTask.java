package org.estatio.capex.dom.invoice.payment.approval.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.NewTaskMixin;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class Payment_newTask
        implements NewTaskMixin<Payment, PaymentApprovalStateTransition, PaymentApprovalStateTransitionType, PaymentApprovalState> {

    public static class ActionDomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Payment_newTask> { }

    private final Payment payment;

    public Payment_newTask(Payment payment) {
        this.payment = payment;
    }

    @Action(
            domainEvent = ActionDomainEvent.class
    )
    @MemberOrder(name = "tasks", sequence = "1")
    @Override
    public Task newTask(
            final EstatioRole assignTo,
            final PaymentApprovalStateTransitionType transitionType,
            @Nullable
            final String description) {
        final PaymentApprovalState currState = supportService.currentStateOf(payment);

        final String taskDescription = Enums.getFriendlyNameOf(transitionType);
        final PaymentApprovalStateTransition stateTransition =
                repository.create(payment, transitionType, currState, assignTo, taskDescription);
        return stateTransition.getTask();
    }

    @Inject
    PaymentApprovalStateTransition.Repository repository;
    @Inject
    PaymentApprovalStateTransitionType.SupportService supportService;

}
