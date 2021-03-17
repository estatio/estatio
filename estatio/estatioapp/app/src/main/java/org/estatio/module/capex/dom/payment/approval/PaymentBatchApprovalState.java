package org.estatio.module.capex.dom.payment.approval;

import org.estatio.module.task.dom.state.State;

public enum PaymentBatchApprovalState implements State<PaymentBatchApprovalState> {
    NEW,
    COMPLETED, // by the treasurer
    PAID,
    DISCARDED;

    public boolean isModifiable() {
        return this == NEW || this == COMPLETED;
    }
}
