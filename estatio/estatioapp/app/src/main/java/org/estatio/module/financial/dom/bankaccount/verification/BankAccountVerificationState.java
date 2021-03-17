package org.estatio.module.financial.dom.bankaccount.verification;

import org.estatio.module.task.dom.state.State;

public enum BankAccountVerificationState implements State<BankAccountVerificationState> {
    NOT_VERIFIED,
    VERIFIED,
    DOUBLE_CHECKED, //TODO: ask business implement in workflow ..? Consider if this should be pre-condition for the bankfile export? What about the future integration with financial system?
    AWAITING_PROOF,
    DISCARDED
}
