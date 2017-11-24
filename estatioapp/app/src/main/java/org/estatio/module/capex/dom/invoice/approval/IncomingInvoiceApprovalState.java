package org.estatio.module.capex.dom.invoice.approval;

import org.estatio.module.capex.dom.state.State;

public enum IncomingInvoiceApprovalState implements State<IncomingInvoiceApprovalState> {
    NEW,
    COMPLETED,
    DISCARDED,
    APPROVED,
    APPROVED_BY_COUNTRY_DIRECTOR,
    APPROVED_BY_CORPORATE_MANAGER,
    PENDING_BANK_ACCOUNT_CHECK,
    PAYABLE,
    PAID
}
