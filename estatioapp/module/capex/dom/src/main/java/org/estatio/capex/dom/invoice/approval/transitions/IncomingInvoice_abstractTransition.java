package org.estatio.capex.dom.invoice.approval.transitions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.task.AbstractTransitionMixin;

public abstract class IncomingInvoice_abstractTransition extends AbstractTransitionMixin<
        IncomingInvoice,
        IncomingInvoiceApprovalStateTransition,
        IncomingInvoiceApprovalStateTransitionType,
        IncomingInvoiceApprovalState> {

    protected IncomingInvoice_abstractTransition(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransitionType transitionType) {
        super(incomingInvoice, transitionType);
    }

}
