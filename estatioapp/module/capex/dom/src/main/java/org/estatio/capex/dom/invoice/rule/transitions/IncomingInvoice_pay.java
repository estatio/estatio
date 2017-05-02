package org.estatio.capex.dom.invoice.rule.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;

@Mixin
public class IncomingInvoice_pay extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_pay(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.PAY);
    }

    @Action()
    @MemberOrder(sequence = "6")
    public IncomingInvoice $$() {
        return super.$$();
    }

}
