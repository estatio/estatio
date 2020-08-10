package org.estatio.module.capex.dom.invoice.accountingaudit;

import org.estatio.module.task.dom.state.State;

import lombok.Getter;

public enum IncomingInvoiceAccountingState implements State<IncomingInvoiceAccountingState> {
    NEW                          (false),
    AUDITABLE                    (false),
    ESCALATED                    (false),
    AUDITED                      (true),
    CHANGED                      (false);

    @Getter
    private final boolean finalState;

    private IncomingInvoiceAccountingState(
            final boolean finalState) {
        this.finalState = finalState;
    }

    public boolean isNotFinal() { return ! isFinalState(); }

}
