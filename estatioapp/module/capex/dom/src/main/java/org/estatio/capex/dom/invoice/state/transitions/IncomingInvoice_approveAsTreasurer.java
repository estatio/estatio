package org.estatio.capex.dom.invoice.state.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransitionType;

@Mixin
public class IncomingInvoice_approveAsTreasurer extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_approveAsTreasurer(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransitionType.APPROVE_AS_TREASURER);
    }

    @Action()
    @MemberOrder(sequence = "4")
    public IncomingInvoice $$() {
        return super.$$();
    }

}
