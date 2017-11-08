package org.estatio.module.capex.dom.bankaccount.verification.triggers;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.module.bankaccount.dom.BankAccount;

abstract class BankAccount_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    BankAccount,
                                    BankAccountVerificationStateTransition,
                                    BankAccountVerificationStateTransitionType,
                                    BankAccountVerificationState> {

    public static class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return BankAccountVerificationStateTransition.class;
        }
    }

    BankAccount_triggerAbstract(
            final BankAccount bankAccount,
            final BankAccountVerificationStateTransitionType requiredTransitionType) {
        super(bankAccount, BankAccountVerificationStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType
        );
    }

}
