package org.estatio.capex.dom.invoice.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_pendingTaskAbstract;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="prop")
public class IncomingInvoice_pendingApprovalTask
        extends DomainObject_pendingTaskAbstract<
        IncomingInvoice,
        IncomingInvoiceApprovalStateTransition,
        IncomingInvoiceApprovalStateTransitionType,
        IncomingInvoiceApprovalState> {

    public IncomingInvoice_pendingApprovalTask(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
    }


}
