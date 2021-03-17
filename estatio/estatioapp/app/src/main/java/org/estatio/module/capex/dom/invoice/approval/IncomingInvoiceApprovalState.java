package org.estatio.module.capex.dom.invoice.approval;

import java.util.Arrays;
import java.util.List;

import org.estatio.module.task.dom.state.State;

import lombok.Getter;

public enum IncomingInvoiceApprovalState implements State<IncomingInvoiceApprovalState> {
    NEW                          (false, false),
    COMPLETED                    (false, false),
    PRE_MONITORED                    (false, false),
    MONITORED                    (false, false),
    DISCARDED                    (false, true ),
    APPROVED                     (true,  false),
    APPROVED_BY_MARKETING_MANAGER(true, false),
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

    public static List<IncomingInvoiceApprovalState> upstreamStates() {
        return Arrays.asList(
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.MONITORED,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.APPROVED_BY_MARKETING_MANAGER,
                IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
    }

}
