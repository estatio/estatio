package org.estatio.capex.dom.order.approval;

import org.estatio.capex.dom.state.State;

public enum OrderApprovalState implements State<OrderApprovalState> {
    NEW,
    APPROVED_BY_COUNTRY_DIRECTOR,
    CANCELLED
}
