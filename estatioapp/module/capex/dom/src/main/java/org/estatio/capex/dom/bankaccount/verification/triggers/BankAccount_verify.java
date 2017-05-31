package org.estatio.capex.dom.bankaccount.verification.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
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
    public Object act(@Nullable final String comment) {
        triggerStateTransition(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTriggerStateTransition();
    }

}
