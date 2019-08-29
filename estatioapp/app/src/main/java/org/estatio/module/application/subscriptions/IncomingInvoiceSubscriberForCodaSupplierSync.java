package org.estatio.module.application.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.application.contributions.Organisation_syncToCoda;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.invoice.approval.triggers.Task_completeIncomingInvoice;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.party.dom.Organisation;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceSubscriberForCodaSupplierSync extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice_complete.ActionDomainEvent ev) {
        if (ev.getEventPhase() == AbstractDomainEvent.Phase.EXECUTED) {
            final IncomingInvoice incomingInvoice = (IncomingInvoice) ev.getMixedIn();
            syncToCodaIfNecessary(incomingInvoice);
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(Task_completeIncomingInvoice.ActionDomainEvent ev) {
        if (ev.getEventPhase() == AbstractDomainEvent.Phase.EXECUTED) {
            final Task task = (Task) ev.getMixedIn();
            // object on task has private access
            final StateTransition stateTransition = stateTransitionService.findFor(task);

            if (stateTransition != null)
                syncToCodaIfNecessary((IncomingInvoice) stateTransition.getDomainObject());
        }

    }

    private void syncToCodaIfNecessary(final IncomingInvoice incomingInvoice) {
        final Organisation organisation = (Organisation) incomingInvoice.getSeller();
        final Organisation_syncToCoda mixin = factoryService.mixin(Organisation_syncToCoda.class, organisation);

        if (!mixin.hideAct()) {
            wrapperFactory.wrap(mixin).act();
        }
    }

    @Inject
    FactoryService factoryService;

    @Inject
    WrapperFactory wrapperFactory;

    @Inject
    StateTransitionService stateTransitionService;

}
