package org.estatio.capex.dom.bankaccount.verification.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method = "act")
public class BankAccount_cancel extends BankAccount_triggerAbstract {

    public BankAccount_cancel(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.CANCEL);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public Object act(
            @Nullable final String comment) {
        return triggerStateTransition(comment);
    }

    public boolean hideAct() {
        return cannotTriggerStateTransition();
    }
}
