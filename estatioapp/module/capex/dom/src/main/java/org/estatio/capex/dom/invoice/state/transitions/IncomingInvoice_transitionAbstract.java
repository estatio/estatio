package org.estatio.capex.dom.invoice.state.transitions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransition;
import org.estatio.capex.dom.task.TaskTransition;

public abstract class IncomingInvoice_transitionAbstract {

    protected final IncomingInvoice incomingInvoice;
    protected final IncomingInvoiceTransition transition;

    public IncomingInvoice_transitionAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceTransition transition) {
        this.incomingInvoice = incomingInvoice;
        this.transition = transition;
    }

    @Action()
    public IncomingInvoice $$(){
        TaskTransition.Util.apply(incomingInvoice, transition, serviceRegistry2);
        return incomingInvoice;
    }

    public boolean hide$$() {
        return !TaskTransition.Util.canApply(incomingInvoice, transition, serviceRegistry2);
    }

    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
