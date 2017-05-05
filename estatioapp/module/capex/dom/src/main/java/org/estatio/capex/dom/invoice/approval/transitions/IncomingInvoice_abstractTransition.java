package org.estatio.capex.dom.invoice.approval.transitions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionService;

public abstract class IncomingInvoice_abstractTransition {

    protected final IncomingInvoice incomingInvoice;
    protected final IncomingInvoiceApprovalStateTransitionType transitionType;

    protected IncomingInvoice_abstractTransition(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransitionType transitionType) {
        this.incomingInvoice = incomingInvoice;
        this.transitionType = transitionType;
    }

    @Action()
    public IncomingInvoice $$(
                            @Nullable
                            final String comment){
        stateTransitionService.apply(incomingInvoice, transitionType, comment);
        return incomingInvoice;
    }

    public boolean hide$$() {
        return !stateTransitionService.canApply(incomingInvoice, transitionType);
    }

    @Inject
    private ServiceRegistry2 serviceRegistry2;
    @Inject
    private StateTransitionService stateTransitionService;

}
