package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.invoice.dom.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know about incoming invoices or documents.
 */
@Mixin(method = "act")
public class BankAccount_attachPdfAsIbanProof {

    public static final String ROLE_NAME_FOR_IBAN_PROOF = "iban proof";

    private final BankAccount bankAccount;

    public BankAccount_attachPdfAsIbanProof(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public BankAccount act(
            @Parameter(fileAccept = "application/pdf")
            final Blob document) {

        final DocumentType ibanProofDocType = DocumentTypeData.IBAN_PROOF.findUsing(documentTypeRepository);

        final List<Paperclip> ibanProofPaperclips =
                paperclipRepository.findByAttachedToAndRoleName(bankAccount, ROLE_NAME_FOR_IBAN_PROOF);

        // delete all existing paperclips for this role whose type is also not IBAN_PROOF
        // (ie any incoming invoices that were automatically attached as candidate iban proofs)
        final Predicate<Paperclip> hasIbanProofDocType =
                paperclip -> Objects.equals(ibanProofDocType, paperclip.getDocument().getType());
        final Predicate<Paperclip> doesNotHaveIbanProofDocType = hasIbanProofDocType.negate();

        ibanProofPaperclips.stream()
                .filter(doesNotHaveIbanProofDocType).forEach(
                    paperclip -> paperclipRepository.delete(paperclip)
                );

        final String name = document.getName();
        documentService.createAndAttachDocumentForBlob(
                ibanProofDocType, bankAccount.getAtPath(), name, document, ROLE_NAME_FOR_IBAN_PROOF, bankAccount);
        return bankAccount;
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentService documentService;

}
