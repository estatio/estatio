package org.estatio.capex.dom.invoice.state.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionType;

@Mixin
public class IncomingInvoice_approveAsProjectManager extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_approveAsProjectManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceStateTransitionType.APPROVE_AS_PROJECT_MANAGER);
    }

    @Action()
    @MemberOrder(sequence = "2.2")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
