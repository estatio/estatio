package org.estatio.capex.dom.invoice.approval;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.CHECK_BANK_ACCOUNT;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceApprovalStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev) {
        final StateTransitionEvent.Phase phase = ev.getPhase();
        if (phase == StateTransitionEvent.Phase.TRANSITIONED) {

            final IncomingDocumentCategorisationStateTransitionType transitionType = ev.getTransitionType();
            switch (transitionType) {

            case INSTANTIATE:
                break;
            case CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY:
                break;

            case CLASSIFY_AS_INVOICE_OR_ORDER:
                final Document document = ev.getDomainObject();
                final Optional<IncomingInvoice> incomingInvoiceIfAny = findIncomingInvoiceFrom(document);
                incomingInvoiceIfAny.ifPresent(
                        incomingInvoice ->
                                stateTransitionService .trigger(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.INSTANTIATE, null));
                break;
            }
        }
    }

    private Optional<IncomingInvoice> findIncomingInvoiceFrom(final Document document) {
        final List<Paperclip> paperclipList = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclipList) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof IncomingInvoice) {
                return Optional.of ((IncomingInvoice) attachedTo);
            }
        }
        return Optional.empty();
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(BankAccountVerificationStateTransitionType.TransitionEvent ev) {
        final StateTransitionEvent.Phase phase = ev.getPhase();
        if (phase == StateTransitionEvent.Phase.TRANSITIONED) {
            final BankAccountVerificationStateTransitionType transitionType = ev.getTransitionType();
            final BankAccount bankAccount = ev.getDomainObject();

            switch (transitionType) {

            case INSTANTIATE:
                break;
            case VERIFY_BANK_ACCOUNT:
                final List<IncomingInvoice> incomingInvoices = findIncomingInvoicesUsing(bankAccount);
                for (IncomingInvoice incomingInvoice : incomingInvoices) {
                    stateTransitionService.trigger(incomingInvoice, CHECK_BANK_ACCOUNT, null);
                }

                break;
            case CANCEL:
                break;
            case RESET:
                break;
            }
        }
    }

    private List<IncomingInvoice> findIncomingInvoicesUsing(final BankAccount bankAccount) {
        return incomingInvoiceRepository.findByBankAccount(bankAccount);
    }


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    StateTransitionService stateTransitionService;


}
