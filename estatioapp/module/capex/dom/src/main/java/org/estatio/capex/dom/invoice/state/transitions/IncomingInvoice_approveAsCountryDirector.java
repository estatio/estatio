package org.estatio.capex.dom.invoice.state.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransition;

@Mixin
public class IncomingInvoice_approveAsCountryDirector extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_approveAsCountryDirector(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.APPROVE_AS_COUNTRY_DIRECTOR);
    }

    @Action()
    @MemberOrder(sequence = "3")
    public IncomingInvoice $$() {
        return super.$$();
    }

}
