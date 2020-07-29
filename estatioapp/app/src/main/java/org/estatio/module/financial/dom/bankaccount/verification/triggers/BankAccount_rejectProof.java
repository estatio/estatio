package org.estatio.module.financial.dom.bankaccount.verification.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method = "act")
public class BankAccount_rejectProof extends BankAccount_triggerAbstract {

    private final BankAccount bankAccount;

    public BankAccount_rejectProof(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.REJECT_PROOF);
        this.bankAccount = bankAccount;
    }

    public static class ActionDomainEvent extends BankAccount_triggerAbstract.ActionDomainEvent<BankAccount_rejectProof> {}

    @Action(
        domainEvent = ActionDomainEvent.class,
        semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "9")
    public BankAccount act(
            @Nullable
            final IPartyRoleType role,
            @Nullable final Person personToAssignNextTo,
            final String reason) {
        final BankAccountVerificationStateTransition transition = trigger(role, personToAssignNextTo, reason, reason);
        // The returned transition is the last completed transition and therefore has no task.
        // If you have a next transition that is not completed, set the priority of that task
        final BankAccountVerificationStateTransition transitionNotCompleted = getPendingTransition();
        if (transitionNotCompleted != null) {
            if (transitionNotCompleted.getTask() != null) {
                transitionNotCompleted.getTask().setToHighestPriority();
                transitionNotCompleted.getTask().setPriority(1);
            }
        }
        return bankAccount;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public IPartyRoleType default0Act() {
        return choices0Act().stream().findFirst().orElse(null);
    }

    public List<? extends IPartyRoleType> choices0Act() {
        return enumPartyRoleType();
    }

    public Person default1Act(final IPartyRoleType roleType) {
        return defaultPersonToAssignNextTo(roleType);
    }

    public List<Person> choices1Act(final IPartyRoleType roleType) {
        return choicesPersonToAssignNextTo(roleType);
    }


}
