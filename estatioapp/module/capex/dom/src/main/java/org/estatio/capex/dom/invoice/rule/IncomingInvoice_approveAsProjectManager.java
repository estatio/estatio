package org.estatio.capex.dom.invoice.rule;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;

@Mixin
public class IncomingInvoice_approveAsProjectManager extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_approveAsProjectManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.APPROVE_AS_PROJECT_MANAGER);
    }

}
