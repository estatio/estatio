package org.estatio.capex.dom.bankaccount.verification.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_viewTransitionsAbstract;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method = "act")
public class BankAccount_viewVerificationTransitions
        extends DomainObject_viewTransitionsAbstract<
                                BankAccount,
                                BankAccountVerificationStateTransition,
                                BankAccountVerificationStateTransitionType,
                                BankAccountVerificationState> {

    public BankAccount_viewVerificationTransitions(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<BankAccountVerificationStateTransition> act() {
        return super.act();
    }
}
