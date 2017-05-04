package org.estatio.capex.dom.invoice.state;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewmodel_createInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class CreateIncomingInvoiceSubscriber extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(IncomingInvoiceViewmodel_createInvoice.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final IncomingInvoiceViewModel viewModel = (IncomingInvoiceViewModel) ev.getMixedIn();
            final IncomingInvoice incomingInvoice = viewModel.getIncomingInvoice();

            transactionService.flushTransaction();

            // an alternative design would be to just do this in IncomingInvoiceViewmodel_createInvoice#createInvoice method
            stateTransitionService.apply(incomingInvoice, IncomingInvoiceStateTransitionType.INSTANTIATE, null);
            break;
        }
    }

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    TransactionService transactionService;

}
