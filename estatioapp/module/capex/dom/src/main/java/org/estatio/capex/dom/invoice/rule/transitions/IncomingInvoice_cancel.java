package org.estatio.capex.dom.invoice.rule.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;

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
