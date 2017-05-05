package org.estatio.capex.dom.invoice.approval;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceApprovalInitiatorSubscribingToUpstreamDocumentState extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(StateTransitionEvent ev) {
        final StateTransitionEvent.Phase phase = ev.getPhase();
        switch (phase) {
        case TRANSITIONING:
            break;
        case TRANSITIONED:
            if(ev.getTransitionType() instanceof IncomingDocumentCategorisationStateTransitionType) {
                doOn(ev);
            }
            break;
        }
    }

    protected void doOn(
            final StateTransitionEvent<
                    Document,
                    IncomingDocumentCategorisationStateTransition,
                    IncomingDocumentCategorisationStateTransitionType,
                    IncomingDocumentCategorisationState> ev) {
        final IncomingDocumentCategorisationStateTransitionType transitionType = ev.getTransitionType();
        switch (transitionType) {

        case INSTANTIATING:
            break;
        case CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY:
            break;
        case ASSOCIATE_WITH_DOMAIN_ENTITY:
            final Document document = ev.getDomainObject();
            final IncomingInvoice incomingInvoice = findIncomingInvoiceFrom(document);
            if(incomingInvoice == null) {
                stateTransitionService.apply(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.INSTANTIATE, null);
            }
            break;
        }
    }

    private IncomingInvoice findIncomingInvoiceFrom(final Document document) {
        final List<Paperclip> paperclipList = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclipList) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof IncomingInvoice) {
                return (IncomingInvoice) attachedTo;
            }
        }
        return null;
    }


    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    TransactionService transactionService;

}
