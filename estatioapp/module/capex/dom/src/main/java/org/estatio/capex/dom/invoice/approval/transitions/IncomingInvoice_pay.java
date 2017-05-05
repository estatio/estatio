package org.estatio.capex.dom.invoice.approval.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin
public class IncomingInvoice_pay extends IncomingInvoice_abstractTransition {

    public IncomingInvoice_pay(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.PAY);
    }

    @Action()
    @MemberOrder(sequence = "6")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
