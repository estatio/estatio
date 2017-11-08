package org.estatio.module.capex.dom.bankaccount.verification.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.order.approval.triggers.Task_mixinOrderAbstract;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.Task_mixinActAbstract;
import org.estatio.module.bankaccount.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because Task does not know about the domain object it refers to.
 */
@Mixin(method = "act")
public class Task_verifyBankAccount
        extends
        Task_mixinActAbstract<BankAccount_verify, BankAccount> {

    protected final Task task;

    public Task_verifyBankAccount(final Task task) {
        super(task, BankAccount_verify.class);
        this.task = task;
    }

    public static class ActionDomainEvent
            extends Task_mixinOrderAbstract.ActionDomainEvent<Task_verifyBankAccount> {
        public Class<?> getStateTransitionClass() {
            return BankAccountVerificationStateTransition.class;
        }
    }

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(comment);
        return coalesce(nextTaskIfAny, mixinResult);
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    public String disableAct() {
        if(doGetDomainObjectIfAny() == null) {
            return null;
        }
        return mixin().disableAct();
    }

    public boolean default1Act() {
        return true;
    }

    @Override
    protected BankAccount doGetDomainObjectIfAny() {
        final BankAccountVerificationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getBankAccount() : null;
    }

    @Inject
    BankAccountVerificationStateTransition.Repository repository;

}
