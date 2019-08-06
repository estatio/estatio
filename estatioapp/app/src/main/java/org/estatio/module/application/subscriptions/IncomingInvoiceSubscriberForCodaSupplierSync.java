package org.estatio.module.application.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.application.contributions.Organisation_syncToCoda;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.party.dom.Organisation;

public class IncomingInvoiceSubscriberForCodaSupplierSync {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice_complete.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
            case HIDE:
            case DISABLE:
            case VALIDATE:
            case EXECUTING:
                break;
            case EXECUTED:
                final IncomingInvoice incomingInvoice = (IncomingInvoice) ev.getSubject();
                final Organisation organisation = (Organisation) incomingInvoice.getSeller();

                wrapperFactory.wrap(factoryService.mixin(Organisation_syncToCoda.class, organisation)).act();
                break;
        }

    }

    @Inject
    FactoryService factoryService;

    @Inject
    WrapperFactory wrapperFactory;

}
