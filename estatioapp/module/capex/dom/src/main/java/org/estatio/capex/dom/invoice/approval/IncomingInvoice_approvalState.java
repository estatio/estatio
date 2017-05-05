package org.estatio.capex.dom.invoice.approval;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionService;

@Mixin(method="prop")
public class IncomingInvoice_approvalState {

    private final IncomingInvoice incomingInvoice;
    public IncomingInvoice_approvalState(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public IncomingInvoiceApprovalState prop() {
        return stateTransitionService.currentStateOf(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
    }
    public boolean hide() {
        return false;
    }

    @Inject
    StateTransitionService stateTransitionService;
}
