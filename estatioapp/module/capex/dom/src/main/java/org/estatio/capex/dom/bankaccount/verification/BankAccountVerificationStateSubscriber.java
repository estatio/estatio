package org.estatio.capex.dom.bankaccount.verification;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.bankaccount.documents.BankAccount_attachPdfAsIbanProof;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class BankAccountVerificationStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toInstantiateWhen(BankAccount.PersistedLifecycleEvent ev) {
        stateTransitionService.trigger(ev.getSource(), BankAccountVerificationStateTransitionType.INSTANTIATE, null, null);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toDeleteWhen(BankAccount.RemovingLifecycleEvent ev) {
        repository.deleteFor(ev.getSource());
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toResetWhen(BankAccount.ChangeEvent ev) {
        if(ev.getEventPhase() == AbstractDomainEvent.Phase.EXECUTED) {
            stateTransitionService.trigger(ev.getSource(), BankAccountVerificationStateTransitionType.RESET, null, null);
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toCheckIbanProof(final IncomingInvoiceApprovalStateTransitionType.TransitionEvent ev) {
        final StateTransitionEvent.Phase phase = ev.getPhase();
        if(phase == StateTransitionEvent.Phase.TRANSITIONED) {
            final IncomingInvoiceApprovalStateTransitionType transitionType = ev.getTransitionType();
            final IncomingInvoice incomingInvoice = ev.getDomainObject();


            switch (transitionType) {

            case CHECK_BANK_ACCOUNT_FOR_CORPORATE:
            case CHECK_BANK_ACCOUNT:

                if(bankAccountVerificationChecker.isBankAccountVerifiedFor(incomingInvoice)) {
                    return;
                }

                triggerBankVerificationState(incomingInvoice);
                attachDocumentAsPossibleIbanProof(incomingInvoice);

                break;
            default:
                break;
            }

        }
    }

    private void triggerBankVerificationState(final IncomingInvoice incomingInvoice) {

        final BankAccount bankAccount = incomingInvoice.getBankAccount();
        if(bankAccount == null) {
            return;
        }

        if(stateTransitionService.currentStateOf(bankAccount, BankAccountVerificationStateTransition.class) == null) {
            stateTransitionService
                    .trigger(bankAccount, BankAccountVerificationStateTransitionType.INSTANTIATE, null, null);
        }

        // (re-evaluate the state machine, and create pending transition if required)
        stateTransitionService
                .trigger(bankAccount, BankAccountVerificationStateTransition.class, null, null, null);
        final BankAccountVerificationState state =
                stateTransitionService.currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
        if(state == BankAccountVerificationState.NOT_VERIFIED) {
            stateTransitionService.createPendingTransition(
                    bankAccount, state,
                    BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT, null, null);
        }
    }

    private void attachDocumentAsPossibleIbanProof(final IncomingInvoice incomingInvoice) {

        final BankAccount bankAccount = incomingInvoice.getBankAccount();
        if(bankAccount == null) {
            return;
        }
        final Optional<Document> documentIfAny =
                lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        if (documentIfAny.isPresent()) {
            final Document document = documentIfAny.get();
            paperclipRepository.attach(document, BankAccount_attachPdfAsIbanProof.ROLE_NAME_FOR_IBAN_PROOF,
                    bankAccount);
        }
    }




    @Inject
    BankAccountVerificationChecker bankAccountVerificationChecker;
    @Inject
    StateTransitionService stateTransitionService;
    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;
    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    BankAccountVerificationStateTransition.Repository repository;

}
