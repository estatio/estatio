package org.estatio.capex.dom.invoice.approval.triggers;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

abstract class IncomingInvoice_triggerAbstract
        extends DomainObject_triggerAbstract<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState> {

    IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransitionType transitionType) {
        super(incomingInvoice, transitionType);
    }

}
