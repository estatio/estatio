package org.estatio.capex.dom.invoice.approval.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionChart;

@Mixin
public class IncomingInvoice_approveAsTreasurer extends IncomingInvoice_abstractTransition {

    public IncomingInvoice_approveAsTreasurer(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionChart.APPROVE_AS_TREASURER);
    }

    @Action()
    @MemberOrder(sequence = "4")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
