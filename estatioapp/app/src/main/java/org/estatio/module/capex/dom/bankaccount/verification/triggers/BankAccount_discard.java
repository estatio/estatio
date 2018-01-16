package org.estatio.module.capex.dom.bankaccount.verification.triggers;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method = "act")
public class BankAccount_discard extends BankAccount_triggerAbstract {

    private final BankAccount bankAccount;

    public BankAccount_discard(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.DISCARD);
        this.bankAccount = bankAccount;
    }

    public static class ActionDomainEvent extends BankAccount_triggerAbstract.ActionDomainEvent<BankAccount_discard> {}

    @Action(
        domainEvent = ActionDomainEvent.class,
        semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "9")
    public BankAccount act(
            final String reason) {
        trigger(reason, null);
        return bankAccount;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

}
