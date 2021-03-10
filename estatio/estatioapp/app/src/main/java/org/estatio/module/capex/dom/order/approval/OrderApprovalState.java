package org.estatio.module.capex.dom.order.approval;

import org.estatio.module.task.dom.state.State;

public enum OrderApprovalState implements State<OrderApprovalState> {
    NEW,
    APPROVED,
    DISCARDED
}
