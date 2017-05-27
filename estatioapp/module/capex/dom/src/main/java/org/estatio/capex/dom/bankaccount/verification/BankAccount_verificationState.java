package org.estatio.capex.dom.bankaccount.verification;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.state.DomainObject_currentStateAbstract;
import org.estatio.dom.financial.bankaccount.BankAccount;

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
