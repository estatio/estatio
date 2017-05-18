package org.estatio.capex.dom.bankaccount.verification.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.task.NewTaskMixin;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class BankAccount_newTask
        implements NewTaskMixin<BankAccount, BankAccountVerificationStateTransition, BankAccountVerificationStateTransitionType, BankAccountVerificationState> {

    public static class ActionDomainEvent extends EstatioCapexDomModule.ActionDomainEvent<BankAccount_newTask> { }

    private final BankAccount bankAccount;

    public BankAccount_newTask(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(
            domainEvent = ActionDomainEvent.class
    )
    @MemberOrder(name = "tasks", sequence = "1")
    @Override
    public Task newTask(
            final EstatioRole assignTo,
            final BankAccountVerificationStateTransitionType transitionType,
            @Nullable
            final String description) {
        final BankAccountVerificationState currState = supportService.currentStateOf(bankAccount);

        final String taskDescription = Enums.getFriendlyNameOf(transitionType);
        final BankAccountVerificationStateTransition stateTransition =
                repository.create(bankAccount, transitionType, currState, assignTo, taskDescription);
        return stateTransition.getTask();
    }

    @Inject
    BankAccountVerificationStateTransition.Repository repository;
    @Inject
    BankAccountVerificationStateTransitionType.SupportService supportService;

}
