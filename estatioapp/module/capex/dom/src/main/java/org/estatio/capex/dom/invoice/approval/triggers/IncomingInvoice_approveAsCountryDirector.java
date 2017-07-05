package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin(method = "act")
public class IncomingInvoice_approveAsCountryDirector extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approveAsCountryDirector(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR);
        this.incomingInvoice = incomingInvoice;
    }

    @Action()
    @ActionLayout(cssClassFa = "fa-thumbs-up")
    public Object act(
            @Nullable final String comment) {
        trigger(comment);
        return objectToReturn();
    }

    protected Object objectToReturn() {
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }


}
