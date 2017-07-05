package org.estatio.capex.dom.bankaccount.documents;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="act")
public class BankAccount_detachVerificationProof {

    private final BankAccount bankAccount;

    public BankAccount_detachVerificationProof(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(name = "documents", sequence = "3")
    public BankAccount act(
            final DocumentAbstract document) {

        final Paperclip paperclipIfAny = paperclipRepository
                .findByDocumentAndAttachedToAndRoleName(document, bankAccount,
                        BankAccount_attachPdfAsVerificationProof.ROLE_NAME_FOR_IBAN_PROOF);
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

    public String disableAct() {
        final List<Paperclip> ibanProofPaperclips = findIbanProofPaperclips();
        final int numProof = ibanProofPaperclips.size();
        switch (numProof) {
        case 0:
            return "No documents to detach";
        case 1:
            // otherwise get into the complication of having to move the IBAN back into unverified...
            return "Cannot remove only bank verification proof - attach other proof first";
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
        return paperclipRepository.findByAttachedToAndRoleName(bankAccount, BankAccount_attachPdfAsVerificationProof.ROLE_NAME_FOR_IBAN_PROOF);
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    QueryResultsCache queryResultsCache;

}
