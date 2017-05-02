package org.estatio.capex.dom.invoice.state.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransition;

@Mixin
public class IncomingInvoice_cancel extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_cancel(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.CANCEL);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public IncomingInvoice $$() {
        return super.$$();
    }

}
