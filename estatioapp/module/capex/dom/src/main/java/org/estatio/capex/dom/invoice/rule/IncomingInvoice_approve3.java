package org.estatio.capex.dom.invoice.rule;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;

@Mixin
public class IncomingInvoice_approve3 {
    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approve3(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    public static final IncomingInvoiceEvent EVENT = IncomingInvoiceEvent.APPROVE_AS_TREASURER;
    @Action()
    public IncomingInvoice $$(){
        service.on(EVENT, incomingInvoice);
        return incomingInvoice;
    }

    public boolean hide$$() {
        return !EVENT.isValidState(incomingInvoice.getIncomingInvoiceState());
    }

    @Inject
    IncomingInvoiceEventService service;

}
