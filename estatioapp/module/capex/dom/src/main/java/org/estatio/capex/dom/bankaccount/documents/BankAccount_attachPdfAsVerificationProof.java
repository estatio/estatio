package org.estatio.capex.dom.bankaccount.documents;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class BankAccount_attachPdfAsVerificationProof {

    public static final String ROLE_NAME_FOR_IBAN_PROOF = "iban proof";

    private final BankAccount bankAccount;

    public BankAccount_attachPdfAsVerificationProof(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action()
    @MemberOrder(name = "documents", sequence = "2")
    public BankAccount act(
            @Parameter(fileAccept = "application/pdf")
            final Blob document) {
        final String name = document.getName();
        final DocumentType type = DocumentTypeData.IBAN_PROOF.findUsing(documentTypeRepository);
        documentService.createAndAttachDocumentForBlob(type, bankAccount.getAtPath(), name, document, ROLE_NAME_FOR_IBAN_PROOF, bankAccount);
        return bankAccount;
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentService documentService;

}
