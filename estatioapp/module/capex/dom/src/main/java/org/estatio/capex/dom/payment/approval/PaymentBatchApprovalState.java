package org.estatio.capex.dom.payment.approval;

import org.estatio.capex.dom.state.State;

public enum PaymentBatchApprovalState implements State<PaymentBatchApprovalState> {
    NEW,
    APPROVED_BY_FINANCIAL_DIRECTOR
}
