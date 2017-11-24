package org.estatio.module.capex.dom.bankaccount.verification;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.financial.dom.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class BankAccountVerificationChecker {

    @Programmatic
    public boolean isBankAccountVerifiedFor(final IncomingInvoice incomingInvoice) {

        final BankAccount bankAccount = incomingInvoice.getBankAccount();

        BankAccountVerificationState state = stateTransitionService
                .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);

        return state == BankAccountVerificationState.VERIFIED;
    }

    @Inject
    StateTransitionService stateTransitionService;

}
