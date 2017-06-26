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

    private final BankAccount bankAccount;

    public BankAccount_verify(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT);
        this.bankAccount = bankAccount;
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<BankAccount_verify> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @MemberOrder(sequence = "9")
    public BankAccount act(@Nullable final String comment) {
        return super.act(comment);
    }

    public boolean hideAct() {
        return super.hideAct();
    }

}
