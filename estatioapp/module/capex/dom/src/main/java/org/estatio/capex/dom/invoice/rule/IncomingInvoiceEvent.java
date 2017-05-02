package org.estatio.capex.dom.invoice.rule;

import java.util.Arrays;
import java.util.List;

import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoice;

import lombok.Getter;

public enum IncomingInvoiceEvent {

    CATEGORISE(
            IncomingInvoiceState.RECEIVED),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceState.CATEGORISED),
    APPROVE_AS_ASSET_MANAGER(
            IncomingInvoiceState.CATEGORISED),
    APPROVE_AS_COUNTRY_DIRECTOR(
            IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER, IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER),
    APPROVE_AS_TREASURER(
            IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR),
    PAY(
            IncomingInvoiceState.APPROVED_BY_TREASURER),
    CANCEL(
            null);

    public void apply(TaskForIncomingInvoice task) {

    }

    //public abstract void doApply(TaskForIncomingInvoice task);



    @Getter
    private List<IncomingInvoiceState> validStates;

    IncomingInvoiceEvent(final IncomingInvoiceState ... incomingInvoiceState) {
        validStates = Arrays.asList(incomingInvoiceState);
    }

    public boolean isValidState(final IncomingInvoiceState incomingInvoiceState) {
        return validStates == null || validStates.contains(incomingInvoiceState);
    }
}
