package org.estatio.capex.dom.bankaccount.verification.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin
public class BankAccount_cancel extends BankAccount_abstractTransition {

    public BankAccount_cancel(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.CANCEL);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public BankAccount $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
