package org.estatio.capex.dom.bankaccount.verification.transitions;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.task.AbstractTransitionMixin;
import org.estatio.dom.financial.bankaccount.BankAccount;

public abstract class BankAccount_abstractTransition extends AbstractTransitionMixin<
        BankAccount,
        BankAccountVerificationStateTransition,
        BankAccountVerificationStateTransitionType,
        BankAccountVerificationState> {

    protected BankAccount_abstractTransition(
            final BankAccount bankAccount,
            final BankAccountVerificationStateTransitionType transitionType) {
        super(bankAccount, transitionType);
    }
}
