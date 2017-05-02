package org.estatio.capex.dom.invoice.rule.transitions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;
import org.estatio.capex.dom.invoice.rule.TaskTransition;

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
        TaskTransition.Util.apply(transition, incomingInvoice, wrapperFactory, factoryService);
        return incomingInvoice;
    }

    public boolean hide$$() {
        return !TaskTransition.Util.isFromState(transition, incomingInvoice.getIncomingInvoiceState());
    }

    @Inject
    private FactoryService factoryService;
    @Inject
    private WrapperFactory wrapperFactory;

}
