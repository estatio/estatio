package org.estatio.capex.dom.invoice.state.transitions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransition;

@Mixin
public class IncomingInvoice_approveAsAssetManager extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_approveAsAssetManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.APPROVE_AS_ASSET_MANAGER);
    }

    @Action()
    @MemberOrder(sequence = "2.1")
    public IncomingInvoice $$() {
        return super.$$();
    }
}
