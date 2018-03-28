package org.estatio.module.capex.dom.invoice.approval;

import org.estatio.module.capex.dom.state.State;

public enum IncomingInvoiceApprovalState implements State<IncomingInvoiceApprovalState> {
    NEW(false),
    COMPLETED(false),
    DISCARDED(false),
    APPROVED(true),
    APPROVED_BY_COUNTRY_DIRECTOR(true),
    APPROVED_BY_CORPORATE_MANAGER(true),
    PENDING_BANK_ACCOUNT_CHECK(false),
    PAYABLE(false),
    PAID(false);

    private final boolean isApproval;

    private IncomingInvoiceApprovalState(final boolean isApproval) {
        this.isApproval = isApproval;
    }

    public boolean isApproval(){
        return this.isApproval;
    }
}
