package org.estatio.capex.dom.invoice.approval.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin
public class IncomingInvoice_cancel extends IncomingInvoice_abstractTransition {

    public IncomingInvoice_cancel(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.CANCEL);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
