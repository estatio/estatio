package org.estatio.capex.dom.invoice.approval;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewmodel_saveInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceApprovalInitiatorSubscribingToViewModel extends AbstractSubscriber {

    //
    // TODO: should get rid of this when the upstream document state is implemented.
    //
    // (think it's not a harm to have both, behaviour of STS is kinda idempotent).
    //

    @com.google.common.eventbus.Subscribe
    public void on(IncomingInvoiceViewmodel_saveInvoice.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final IncomingInvoiceViewModel viewModel = (IncomingInvoiceViewModel) ev.getMixedIn();
            final IncomingInvoice incomingInvoice = viewModel.getIncomingInvoice();

            transactionService.flushTransaction();

            // an alternative design would be to just do this in IncomingInvoiceViewmodel_saveInvoice#saveInvoice method
            stateTransitionService.trigger(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.INSTANTIATE, null);
            break;
        }
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    TransactionService transactionService;

}
