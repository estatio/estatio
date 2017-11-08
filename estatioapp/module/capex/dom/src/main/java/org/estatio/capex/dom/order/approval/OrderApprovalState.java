package org.estatio.capex.dom.order.approval;

import org.estatio.module.capex.dom.state.State;

public enum OrderApprovalState implements State<OrderApprovalState> {
    NEW,
    APPROVED,
    DISCARDED
}
