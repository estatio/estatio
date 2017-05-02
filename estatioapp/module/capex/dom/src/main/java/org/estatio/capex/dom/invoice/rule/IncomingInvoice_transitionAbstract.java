package org.estatio.capex.dom.invoice.rule;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;

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
        transition.apply(incomingInvoice, wrapperFactory, factoryService);
        return incomingInvoice;
    }

    public boolean hide$$() {
        return !transition.isFromState(incomingInvoice.getIncomingInvoiceState());
    }

    @Inject
    private FactoryService factoryService;
    @Inject
    private WrapperFactory wrapperFactory;

}
