package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know about incoming invoices or documents.
 */
@Mixin(method="act")
public class BankAccount_detachIbanProof {

    private final BankAccount bankAccount;

    public BankAccount_detachIbanProof(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public BankAccount act(
            final DocumentAbstract document) {

        final Paperclip paperclipIfAny = paperclipRepository
                .findByDocumentAndAttachedToAndRoleName(document, bankAccount,
                        BankAccount_attachPdfAsIbanProof.ROLE_NAME_FOR_IBAN_PROOF);
        if(paperclipIfAny != null) {
            paperclipRepository.delete(paperclipIfAny);
        }
        return bankAccount;
    }

    public List<DocumentAbstract> choices0Act() {
        final List<Paperclip> paperclips = findIbanProofPaperclips();
        return paperclips.stream()
                .map(Paperclip::getDocument).collect(
                Collectors.toList());
    }

    public DocumentAbstract default0Act() {
        final List<DocumentAbstract> choices = choices0Act();
        return choices.size() == 1 ? choices.get(0) : null;
    }

    public String disableAct() {
        final List<Paperclip> ibanProofPaperclips = findIbanProofPaperclips();
        final int numProof = ibanProofPaperclips.size();
        switch (numProof) {
        case 0:
            return "No documents to detach";
        case 1:
            // otherwise get into the complication of having to move the IBAN back into unverified...
            final BankAccountVerificationState currentState =
                    stateTransitionService.currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
            if(currentState == BankAccountVerificationState.VERIFIED) {
                return "Cannot remove only iban proof - attach other proof first";
            }
        default:
            return null;
        }
    }

    private List<Paperclip> findIbanProofPaperclips() {
        return queryResultsCache.execute(
                this::doFindIbanProofPaperclips,
                getClass(),
                "findIbanProofPaperclips", bankAccount);
    }

    private List<Paperclip> doFindIbanProofPaperclips() {
        return paperclipRepository.findByAttachedToAndRoleName(bankAccount, BankAccount_attachPdfAsIbanProof.ROLE_NAME_FOR_IBAN_PROOF);
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;

}
