package org.estatio.capex.dom.invoice.state.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionType;

@Mixin
public class IncomingInvoice_instantiating extends IncomingInvoice_abstractTransition {

    public IncomingInvoice_instantiating(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceStateTransitionType.INSTANTIATING);
    }

    @Action()
    @MemberOrder(sequence = "1")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
