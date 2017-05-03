package org.estatio.capex.dom.documents.invoice;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransitionType;
import org.estatio.capex.dom.task.StateTransitionType;

@DomainService(nature = NatureOfService.DOMAIN)
public class CreateIncomingInvoiceSubscriber extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(IncomingInvoiceViewmodel_createInvoice.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            ev.getReturnValue();
            final IncomingInvoiceViewModel viewModel = (IncomingInvoiceViewModel) ev.getMixedIn();
            final IncomingInvoice incomingInvoice = viewModel.getIncomingInvoice();

            transactionService.flushTransaction();

            // an alternative design would be to just do this in IncomingInvoiceViewmodel_createInvoice#createInvoice method
            StateTransitionType.Util.apply(incomingInvoice, IncomingInvoiceTransitionType.INSTANTIATING, serviceRegistry2);
            break;
        }
    }

    @Inject
    TransactionService transactionService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
