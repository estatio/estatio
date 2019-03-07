package org.estatio.module.capex.dom.invoice.approval;

import org.estatio.module.capex.dom.state.State;

import lombok.Getter;

public enum IncomingInvoiceApprovalState implements State<IncomingInvoiceApprovalState> {
    NEW                          (false, false),
    COMPLETED                    (false, false),
    DISCARDED                    (false, true ),
    APPROVED                     (true,  false),
    APPROVED_BY_COUNTRY_DIRECTOR (true,  false),
    APPROVED_BY_CORPORATE_MANAGER(true,  false),
    PENDING_BANK_ACCOUNT_CHECK   (false, false),
    PAYABLE                      (false, false),
    PAYABLE_BYPASSING_APPROVAL   (false, false),
    PAID                         (false, true ),
    PENDING_CODA_BOOKS_CHECK     (false, false),
    APPROVED_BY_CENTER_MANAGER   (true,  false),
    PENDING_ADVISE               (false, false),
    ADVISE_POSITIVE              (false,  false),
    SUSPENDED                    (false,  false) ;

    @Getter
    private final boolean approval;
    @Getter
    private final boolean finalState;

    private IncomingInvoiceApprovalState(
            final boolean approval,
            final boolean finalState) {
        this.approval = approval;
        this.finalState = finalState;
    }

    public boolean isNotFinal() { return ! isFinalState(); }

}
