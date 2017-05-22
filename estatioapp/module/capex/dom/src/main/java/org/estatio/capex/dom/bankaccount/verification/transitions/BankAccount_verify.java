package org.estatio.capex.dom.bankaccount.verification.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="act")
public class BankAccount_verify extends BankAccount_abstractTransition {

    public BankAccount_verify(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public BankAccount act(@Nullable final String comment) {
        return super.act(comment);
    }


}
