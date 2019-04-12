package org.estatio.module.capex.dom.bankaccount.verification.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.financial.dom.BankAccount;
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
        trigger(personToAssignNextTo, reason, reason);
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
