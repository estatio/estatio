package org.estatio.capex.dom.bankaccount.verification.transitions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

public abstract class BankAccount_abstractTransition {

    protected final BankAccount bankAccount;
    protected final BankAccountVerificationStateTransitionType transitionType;

    protected BankAccount_abstractTransition(
            final BankAccount bankAccount,
            final BankAccountVerificationStateTransitionType transitionType) {
        this.bankAccount = bankAccount;
        this.transitionType = transitionType;
    }

    @Action()
    public BankAccount $$(
                            @Nullable
                            final String comment){
        stateTransitionService.apply(bankAccount, transitionType, comment);
        return bankAccount;
    }

    public boolean hide$$() {
        return !stateTransitionService.canApply(bankAccount, transitionType);
    }

    @Inject
    private ServiceRegistry2 serviceRegistry2;
    @Inject
    private StateTransitionService stateTransitionService;

}
