package org.estatio.module.capex.dom.bankaccount.verification.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.capex.dom.dobj.DomainObject_viewTransitionsAbstract;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
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
