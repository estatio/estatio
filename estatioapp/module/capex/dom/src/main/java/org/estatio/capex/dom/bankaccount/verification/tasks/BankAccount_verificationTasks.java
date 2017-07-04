package org.estatio.capex.dom.bankaccount.verification.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_tasksAbstract;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method = "coll")
public class BankAccount_verificationTasks extends
        DomainObject_tasksAbstract<
                BankAccount,
                BankAccountVerificationStateTransition,
                BankAccountVerificationStateTransitionType,
                BankAccountVerificationState> {

    public BankAccount_verificationTasks(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }

}
