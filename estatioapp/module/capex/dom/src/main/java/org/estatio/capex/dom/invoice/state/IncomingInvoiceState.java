package org.estatio.capex.dom.invoice.state;

import org.estatio.capex.dom.state.State;

public enum IncomingInvoiceState
        implements State<IncomingInvoiceState> {
    NEW,
    APPROVED_BY_ASSET_MANAGER,
    APPROVED_BY_PROJECT_MANAGER,
    APPROVED_BY_COUNTRY_DIRECTOR,
    APPROVED_BY_TREASURER,
    PAID,
    CANCELLED
}
