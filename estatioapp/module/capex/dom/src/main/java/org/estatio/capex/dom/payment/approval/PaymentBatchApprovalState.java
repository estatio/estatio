package org.estatio.capex.dom.payment.approval;

import org.estatio.capex.dom.state.State;

public enum PaymentBatchApprovalState implements State<PaymentBatchApprovalState> {
    NEW,
    COMPLETED, // by the treasurer
    APPROVED,
    DISCARDED;

    public boolean isModifiable() {
        return this == NEW || this == COMPLETED;
    }
}
