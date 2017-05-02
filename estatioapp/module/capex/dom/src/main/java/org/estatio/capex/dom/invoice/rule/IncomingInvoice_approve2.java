package org.estatio.capex.dom.invoice.rule;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;

@Mixin
public class IncomingInvoice_approve2 {
    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approve2(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    public static final IncomingInvoiceEvent EVENT = IncomingInvoiceEvent.APPROVE_AS_PROJECT_MANAGER;
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
