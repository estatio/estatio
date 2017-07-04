package org.estatio.capex.dom.bankaccount.verification.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_pendingTaskAbstract;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="prop")
public class BankAccount_pendingVerificationTask
        extends DomainObject_pendingTaskAbstract<
                    BankAccount,
                    BankAccountVerificationStateTransition,
                    BankAccountVerificationStateTransitionType,
                    BankAccountVerificationState> {

    public BankAccount_pendingVerificationTask(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }


}
