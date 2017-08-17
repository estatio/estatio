package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingInvoice_approveAsCorporateManager extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approveAsCorporateManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CORPORATE_MANAGER);
        this.incomingInvoice = incomingInvoice;
    }

    @Action()
    @ActionLayout(cssClassFa = "fa-thumbs-o-up")
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        final IncomingInvoice next =  nextInvoiceAfterPendingIfRequested(goToNext);
        trigger(comment, null);
        return objectToReturn(next);
    }

    public boolean default1Act() {
        return true;
    }

    protected Object objectToReturn(final IncomingInvoice domainObject) {
        return domainObject;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }


}
