package org.estatio.capex.dom.invoice.rule;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;

@Mixin
public class IncomingInvoice_transition extends TaskStateOwner_transitionAbstract<IncomingInvoiceTransition, IncomingInvoiceState, IncomingInvoice> {

    public IncomingInvoice_transition(
            final IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.class);
    }

}
