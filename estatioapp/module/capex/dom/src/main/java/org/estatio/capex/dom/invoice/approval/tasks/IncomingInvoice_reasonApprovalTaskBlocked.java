package org.estatio.capex.dom.invoice.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_reasonGuardNotSatisfiedAbstract;

@Mixin(method="prop")
public class IncomingInvoice_reasonApprovalTaskBlocked
        extends DomainObject_reasonGuardNotSatisfiedAbstract<
                                IncomingInvoice,
                                IncomingInvoiceApprovalStateTransition,
                                IncomingInvoiceApprovalStateTransitionType,
                                IncomingInvoiceApprovalState> {

    public IncomingInvoice_reasonApprovalTaskBlocked(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
    }

}
