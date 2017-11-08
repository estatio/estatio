package org.estatio.module.capex.dom.bankaccount.verification.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.capex.dom.dobj.DomainObject_checkStateAbstract;
import org.estatio.module.bankaccount.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method="act")
public class BankAccount_checkVerificationState
        extends DomainObject_checkStateAbstract<
                            BankAccount,
                            BankAccountVerificationStateTransition,
                            BankAccountVerificationStateTransitionType,
                            BankAccountVerificationState> {

    public BankAccount_checkVerificationState(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }


}
