package org.estatio.capex.dom.bankaccount.verification.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="act")
public class BankAccount_verify extends BankAccount_triggerAbstract {

    public BankAccount_verify(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT);
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<BankAccount_triggerAbstract> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @MemberOrder(sequence = "9")
    public BankAccount act(@Nullable final String comment) {
        return super.act(comment);
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }


    @Mixin(method="act")
    public static class Task_verifyBankAccount
            extends
            Task._mixinAbstract<BankAccount_verify, BankAccount> {

        protected final Task task;
        public Task_verifyBankAccount(final Task task) {
            super(task, BankAccount_verify.class);
            this.task = task;
        }

        public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<BankAccount_verify.Task_verifyBankAccount> {}

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = BankAccount_verify.Task_verifyBankAccount.DomainEvent.class)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public Task act(@Nullable final String comment, final boolean goToNext) {
            mixin().act(comment);
            return taskToReturn(goToNext, task);
        }

        public boolean hideAct() {
            return super.hideAct() || mixin().hideAct();
        }

        @Override
        protected BankAccount doGetDomainObjectIfAny() {
            final BankAccountVerificationStateTransition transition = repository.findByTask(this.task);
            return transition != null ? transition.getBankAccount() : null;
        }

        @Inject
        BankAccountVerificationStateTransition.Repository repository;

    }


}
