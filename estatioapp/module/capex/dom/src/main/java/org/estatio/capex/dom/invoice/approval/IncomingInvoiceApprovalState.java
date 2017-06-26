package org.estatio.capex.dom.invoice.approval;

import org.estatio.capex.dom.state.State;

public enum IncomingInvoiceApprovalState implements State<IncomingInvoiceApprovalState> {
    NEW,
    COMPLETED,
    APPROVED,
    APPROVED_BY_COUNTRY_DIRECTOR,
    PENDING_BACK_ACCOUNT_CHECK,
    PAYABLE,
    PAID
}
