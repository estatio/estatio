package org.estatio.capex.dom.invoice.state.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransitionType;

@Mixin
public class IncomingInvoice_pay extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_pay(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransitionType.PAY);
    }

    @Action()
    @MemberOrder(sequence = "6")
    public IncomingInvoice $$() {
        return super.$$();
    }

}
