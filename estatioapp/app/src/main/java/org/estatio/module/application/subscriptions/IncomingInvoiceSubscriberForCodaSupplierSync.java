package org.estatio.module.application.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.application.contributions.Organisation_syncToCoda;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.party.dom.Organisation;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceSubscriberForCodaSupplierSync extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice_complete.ActionDomainEvent ev) {
        final IncomingInvoice incomingInvoice = (IncomingInvoice) ev.getMixedIn();
        final Organisation organisation = (Organisation) incomingInvoice.getSeller();

        switch (ev.getEventPhase()) {
            case EXECUTED:
                final Organisation_syncToCoda mixin = factoryService.mixin(Organisation_syncToCoda.class, organisation);
                if (!mixin.hideAct()) {
                    wrapperFactory.wrap(mixin).act();
                }
                break;
            default:
                break;
        }

    }

    @Inject
    FactoryService factoryService;

    @Inject
    WrapperFactory wrapperFactory;

}
