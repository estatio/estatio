package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin(method = "act")
public class IncomingInvoice_approveAsAssetManager extends IncomingInvoice_triggerAbstract {

    public IncomingInvoice_approveAsAssetManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_ASSET_MANAGER);
    }

    @Action()
    @MemberOrder(sequence = "2.1")
    public Object act(@Nullable final String comment) {
        trigger(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

}
