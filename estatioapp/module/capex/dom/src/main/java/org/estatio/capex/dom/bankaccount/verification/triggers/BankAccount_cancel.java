package org.estatio.capex.dom.bankaccount.verification.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method = "act")
public class BankAccount_cancel extends BankAccount_triggerAbstract {

    private final BankAccount bankAccount;

    public BankAccount_cancel(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.CANCEL);
        this.bankAccount = bankAccount;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "9")
    @Override public BankAccount act(@Nullable final String comment) {
        return super.act(comment);
    }

    @Override public boolean hideAct() {
        return super.hideAct();
    }

    @Override public String disableAct() {
        return super.disableAct();
    }
}
