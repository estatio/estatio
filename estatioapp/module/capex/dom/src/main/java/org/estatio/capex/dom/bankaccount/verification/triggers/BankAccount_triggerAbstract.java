package org.estatio.capex.dom.bankaccount.verification.triggers;

import java.util.List;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.dom.financial.bankaccount.BankAccount;

abstract class BankAccount_triggerAbstract
        extends DomainObject_triggerAbstract<
                    BankAccount,
                    BankAccountVerificationStateTransition,
                    BankAccountVerificationStateTransitionType,
                    BankAccountVerificationState> {

    BankAccount_triggerAbstract(
            final BankAccount bankAccount,
            final List<BankAccountVerificationState> fromStates) {
        super(bankAccount, BankAccountVerificationStateTransition.class, fromStates);
    }
}
