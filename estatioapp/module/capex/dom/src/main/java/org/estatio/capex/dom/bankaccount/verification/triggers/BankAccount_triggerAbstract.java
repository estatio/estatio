package org.estatio.capex.dom.bankaccount.verification.triggers;

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

    public static class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return BankAccountVerificationState.class;
        }
    }

    BankAccount_triggerAbstract(
            final BankAccount bankAccount,
            final BankAccountVerificationStateTransitionType requiredTransitionType) {
        super(bankAccount, BankAccountVerificationStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType
        );
    }


    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

}
