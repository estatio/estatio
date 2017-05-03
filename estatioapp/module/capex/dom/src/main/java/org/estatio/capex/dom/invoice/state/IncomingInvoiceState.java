package org.estatio.capex.dom.invoice.state;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.task.State;

public enum IncomingInvoiceState
        implements State<IncomingInvoice, IncomingInvoiceState> {
    NEW,
    APPROVED_BY_ASSET_MANAGER,
    APPROVED_BY_PROJECT_MANAGER,
    APPROVED_BY_COUNTRY_DIRECTOR,
    APPROVED_BY_TREASURER,
    PAID,
    CANCELLED;
}
