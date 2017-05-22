package org.estatio.capex.dom.bankaccount.verification;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="prop")
public class BankAccount_verificationState {

    private final BankAccount bankAccount;
    public BankAccount_verificationState(final BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public BankAccountVerificationState prop() {
        return stateTransitionService.currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
    }
    public boolean hide() {
        return false;
    }

    @Inject
    StateTransitionService stateTransitionService;
}
