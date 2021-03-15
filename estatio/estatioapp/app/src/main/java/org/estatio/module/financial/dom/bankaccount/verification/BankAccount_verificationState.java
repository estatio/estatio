package org.estatio.module.financial.dom.bankaccount.verification;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.task.dom.dobj.DomainObject_currentStateAbstract;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method="prop")
public class BankAccount_verificationState
        extends DomainObject_currentStateAbstract<
                            BankAccount,
                            BankAccountVerificationStateTransition,
                            BankAccountVerificationStateTransitionType,
                            BankAccountVerificationState> {

    public BankAccount_verificationState(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public BankAccountVerificationState prop() {
        return super.prop();
    }
}
