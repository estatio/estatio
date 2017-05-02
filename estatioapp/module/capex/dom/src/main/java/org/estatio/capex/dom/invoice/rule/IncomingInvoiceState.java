package org.estatio.capex.dom.invoice.rule;

import org.estatio.capex.dom.invoice.IncomingInvoice;

public enum IncomingInvoiceState
        implements TaskState<IncomingInvoice, IncomingInvoiceState> {
    NEW,
    APPROVED_BY_ASSET_MANAGER,
    APPROVED_BY_PROJECT_MANAGER,
    APPROVED_BY_COUNTRY_DIRECTOR,
    APPROVED_BY_TREASURER,
    PAID,
    CANCELLED;
}
