package org.estatio.capex.dom.invoice.rule.transitions;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;

@Mixin
public class IncomingInvoice_instantiating extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_instantiating(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.INSTANTIATING);
    }

}
