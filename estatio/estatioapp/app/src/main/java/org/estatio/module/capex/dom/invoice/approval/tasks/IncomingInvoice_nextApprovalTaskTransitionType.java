package org.estatio.module.capex.dom.invoice.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.task.dom.dobj.DomainObject_nextTaskTransitionTypeAbstract;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="prop")
public class IncomingInvoice_nextApprovalTaskTransitionType
        extends DomainObject_nextTaskTransitionTypeAbstract<
                        IncomingInvoice,
                        IncomingInvoiceApprovalStateTransition,
                        IncomingInvoiceApprovalStateTransitionType,
                        IncomingInvoiceApprovalState> {

    public IncomingInvoice_nextApprovalTaskTransitionType(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
    }

    public boolean hideProp() {
        return super.hideProp();
    }

}
