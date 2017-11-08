package org.estatio.capex.dom.bankaccount.verification;

import org.estatio.module.capex.dom.state.State;

public enum BankAccountVerificationState implements State<BankAccountVerificationState> {
    NOT_VERIFIED,
    VERIFIED,
    AWAITING_PROOF
}
