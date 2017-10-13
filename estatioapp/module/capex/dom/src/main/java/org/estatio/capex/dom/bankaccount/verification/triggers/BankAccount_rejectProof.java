package org.estatio.capex.dom.bankaccount.verification.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Person;

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
            final String role,
            @Nullable final Person personToAssignNextTo,
            final String reason) {
        trigger(personToAssignNextTo, reason, reason);
        return bankAccount;
    }

    @Override public boolean hideAct() {
        return super.hideAct();
    }

    @Override public String disableAct() {
        return super.disableAct();
    }

    public String default0Act() {
        return enumPartyRoleTypeName();
    }

    public Person default1Act() {
        return defaultPersonToAssignNextTo();
    }

    public List<Person> choices1Act() {
        return choicesPersonToAssignNextTo();
    }


}
