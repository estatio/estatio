package org.estatio.module.financial.dom.bankaccount.verification.triggers;

import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.task.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.module.financial.dom.BankAccount;

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
