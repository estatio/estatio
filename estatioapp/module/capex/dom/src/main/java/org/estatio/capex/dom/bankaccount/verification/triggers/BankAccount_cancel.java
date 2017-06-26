package org.estatio.capex.dom.bankaccount.verification.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method = "act")
public class BankAccount_cancel extends BankAccount_triggerAbstract {

    private final BankAccount bankAccount;

    public BankAccount_cancel(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.CANCEL);
        this.bankAccount = bankAccount;
    }

}
