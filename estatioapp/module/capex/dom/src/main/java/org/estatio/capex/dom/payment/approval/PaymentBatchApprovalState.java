package org.estatio.capex.dom.payment.approval;

import org.estatio.capex.dom.state.State;

public enum PaymentBatchApprovalState implements State<PaymentBatchApprovalState> {
    NEW,
    COMPLETED // by the treasurer
    // in the future, expect to extend this to include approval by the financial director
}
