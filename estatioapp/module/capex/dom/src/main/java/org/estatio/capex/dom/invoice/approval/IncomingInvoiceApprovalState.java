package org.estatio.capex.dom.invoice.approval;

import org.estatio.capex.dom.state.State;

public enum IncomingInvoiceApprovalState implements State<IncomingInvoiceApprovalState> {
    NEW,
    CLASSIFIED,
    APPROVED_BY_ASSET_MANAGER,
    APPROVED_BY_PROJECT_MANAGER,
    APPROVED_BY_COUNTRY_DIRECTOR,
    PAYABLE,
    CANCELLED
}
