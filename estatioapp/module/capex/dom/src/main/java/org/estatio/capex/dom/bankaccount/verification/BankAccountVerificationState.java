package org.estatio.capex.dom.bankaccount.verification;

import org.estatio.capex.dom.state.State;

public enum BankAccountVerificationState implements State<BankAccountVerificationState> {
    PENDING,
    VERIFIED,
    CANCELLED
}
