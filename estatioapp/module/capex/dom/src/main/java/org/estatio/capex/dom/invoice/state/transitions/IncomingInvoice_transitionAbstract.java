package org.estatio.capex.dom.invoice.state.transitions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransitionType;
import org.estatio.capex.dom.task.StateTransitionType;

public abstract class IncomingInvoice_transitionAbstract {

    protected final IncomingInvoice incomingInvoice;
    protected final IncomingInvoiceTransitionType transition;

    public IncomingInvoice_transitionAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceTransitionType transition) {
        this.incomingInvoice = incomingInvoice;
        this.transition = transition;
    }

    @Action()
    public IncomingInvoice $$(){
        StateTransitionType.Util.apply(incomingInvoice, transition, serviceRegistry2);
        return incomingInvoice;
    }

    public boolean hide$$() {
        return !StateTransitionType.Util.canApply(incomingInvoice, transition, serviceRegistry2);
    }

    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
