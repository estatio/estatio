package org.estatio.capex.dom.invoice.payment.approval;

import org.estatio.capex.dom.state.State;

public enum PaymentApprovalState implements State<PaymentApprovalState> {
    NEW,
    APPROVED_BY_TREASURER,
    CANCELLED
}
