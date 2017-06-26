package org.estatio.capex.dom.payment.approval;

import org.estatio.capex.dom.state.State;

public enum PaymentApprovalState implements State<PaymentApprovalState> {
    NEW,
    APPROVED_BY_FINANCIAL_DIRECTOR
}
