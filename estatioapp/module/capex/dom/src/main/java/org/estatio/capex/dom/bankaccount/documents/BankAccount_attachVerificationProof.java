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

@Mixin
public class BankAccount_attachVerificationProof {

    private final BankAccount bankAccount;

    public BankAccount_attachVerificationProof(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action()
    @MemberOrder(name = "documents", sequence = "1")
    public BankAccount $$(
            @Parameter(fileAccept = "application/pdf")
            final Blob document) {
        final String name = document.getName();
        final DocumentType type = DocumentTypeData.IBAN_PROOF.findUsing(documentTypeRepository);
        documentService.createAndAttachDocumentForBlob(type, bankAccount.getAtPath(), name, document, "iban proof", bankAccount);
        return bankAccount;
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentService documentService;

}
