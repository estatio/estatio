package org.estatio.module.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass.
 */
@Mixin(method = "act")
public class IncomingInvoice_previous extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_previous(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_previous> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    public Object act() {
        final IncomingInvoice previous = previousBeforePending();
        return objectToReturn(previous);
    }

    public String disableAct() {
        return previousBeforePending() == getDomainObject()
                ? "Could not find previous invoice; either this invoice has no pending task, or there are none prior"
                : null;
    }

    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return incomingInvoice;
    }

}
