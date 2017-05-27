package org.estatio.capex.dom.bankaccount.verification.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.state.DomainObject_transitionsAbstract;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method = "coll")
public class BankAccount_verificationTransitions
        extends DomainObject_transitionsAbstract<
                        BankAccount,
                        BankAccountVerificationStateTransition,
                        BankAccountVerificationStateTransitionType,
                        BankAccountVerificationState> {

    public BankAccount_verificationTransitions(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<BankAccountVerificationStateTransition> coll() {
        return super.coll();
    }
}
