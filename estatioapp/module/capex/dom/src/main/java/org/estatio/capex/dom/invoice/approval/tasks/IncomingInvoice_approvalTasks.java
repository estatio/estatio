package org.estatio.capex.dom.invoice.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_tasksAbstract;

@Mixin(method = "coll")
public class IncomingInvoice_approvalTasks
        extends DomainObject_tasksAbstract<
                IncomingInvoice,
                IncomingInvoiceApprovalStateTransition,
                IncomingInvoiceApprovalStateTransitionType,
                IncomingInvoiceApprovalState> {

    public IncomingInvoice_approvalTasks(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice,
                IncomingInvoiceApprovalStateTransition.class);
    }

}
