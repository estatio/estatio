package org.estatio.module.capex.dom.bankaccount.verification;

import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.contributions.BankAccount_attachPdfAsIbanProof;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.task.dom.state.StateTransitionEvent;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
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

            case COMPLETE: // do so as early as possible so that verification can run in parallel with approval
            case CHECK_BANK_ACCOUNT: // belt-n-braces, do late as well

                if(bankAccountVerificationChecker.isBankAccountVerifiedFor(incomingInvoice) ||
                        Arrays.asList(PaymentMethod.MANUAL_PROCESS, PaymentMethod.CREDIT_CARD, PaymentMethod.REFUND_BY_SUPPLIER).contains(incomingInvoice.getPaymentMethod())
                        ) {
                    return;
                }

                triggerBankVerificationState(incomingInvoice);
                attachDocumentAsPossibleIbanProofIfNone(incomingInvoice);

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

        // create the required pending transition, if none already.
        stateTransitionService
                .triggerPending(bankAccount, BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT);
    }

    private void attachDocumentAsPossibleIbanProofIfNone(final IncomingInvoice incomingInvoice) {

        final BankAccount bankAccount = incomingInvoice.getBankAccount();
        if(bankAccount == null) {
            return;
        }

        // if already have some proof, then no need to attach any other
        final Optional<Document> currentProofIfAny = lookupAttachedPdfService.lookupIbanProofPdfFrom(bankAccount);
        if(currentProofIfAny.isPresent()) {
            return;
        }

        // else, attach this invoice as possible iban proof.
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
