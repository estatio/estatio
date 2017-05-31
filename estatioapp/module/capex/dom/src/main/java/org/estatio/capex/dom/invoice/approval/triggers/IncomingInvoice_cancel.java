package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin(method = "act")
public class IncomingInvoice_cancel extends IncomingInvoice_triggerAbstract {

    public IncomingInvoice_cancel(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.CANCEL);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public Object act(@Nullable final String comment) {
        triggerStateTransition(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTriggerStateTransition();
    }

}
