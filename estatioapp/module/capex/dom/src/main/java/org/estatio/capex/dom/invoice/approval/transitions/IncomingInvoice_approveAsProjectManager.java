package org.estatio.capex.dom.invoice.approval.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin
public class IncomingInvoice_approveAsProjectManager extends IncomingInvoice_abstractTransition {

    public IncomingInvoice_approveAsProjectManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_PROJECT_MANAGER);
    }

    @Action()
    @MemberOrder(sequence = "2.2")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
