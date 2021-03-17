package org.estatio.module.capex.dom.bankaccount.verification.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.task.dom.dobj.DomainObject_transitionsAbstract;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
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
