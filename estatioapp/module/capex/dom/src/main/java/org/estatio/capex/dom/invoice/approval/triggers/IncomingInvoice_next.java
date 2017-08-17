package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass.
 */
@Mixin(method = "act")
public class IncomingInvoice_next extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_next(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE);
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Object act() {
        final IncomingInvoice next = nextInvoiceAfterPending();
        return objectToReturn(next);
    }

    public String disableAct() {
        return nextInvoiceAfterPending() == getDomainObject()
                ? "Could not find next invoice; either this invoice has no pending task, or there are none after"
                : null;
    }

    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return incomingInvoice;
    }

}
