package org.estatio.capex.dom.invoice.approval;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.CONFIRM_BANK_ACCOUNT_VERIFIED;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceApprovalStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice.ObjectPersistedEvent ev) {
        // nb: note that the incoming invoice at this stage has no items attached to it, so there is a limit as to what we can safely do.
        // however, it *is* ok to just create the state chart for the invoice.
        final IncomingInvoice incomingInvoice = ev.getSource();
        stateTransitionService .trigger(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.INSTANTIATE, null);
    }

    /*
    because we now have NEW->COMPLETED, means that we can integrate at a "deeper" level via the object lifecycle event;
    so this code not needed.
    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev) {
        final StateTransitionEvent.Phase phase = ev.getPhase();
        if (phase == StateTransitionEvent.Phase.TRANSITIONED) {

            final IncomingDocumentCategorisationStateTransitionType transitionType = ev.getTransitionType();
            IncomingDocumentCategorisationState toState = transitionType.getToState();
            switch (toState) {

            case PROCESSED:
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


    */

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
                    stateTransitionService.trigger(incomingInvoice, CONFIRM_BANK_ACCOUNT_VERIFIED, null);
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
