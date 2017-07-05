package org.estatio.capex.dom.bankaccount.documents;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="act")
public class BankAccount_attachInvoiceAsVerificationProof {

    public static final String ROLE_NAME_FOR_IBAN_PROOF = BankAccount_attachPdfAsVerificationProof.ROLE_NAME_FOR_IBAN_PROOF;

    private final BankAccount bankAccount;

    public BankAccount_attachInvoiceAsVerificationProof(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action()
    @MemberOrder(name = "documents", sequence = "1")
    public BankAccount act(final Document document) {
        paperclipRepository.attach(document, ROLE_NAME_FOR_IBAN_PROOF, bankAccount);
        return bankAccount;
    }

    public List<Document> choices0Act() {
        final List<Document> documents = Lists.newArrayList();
        final List<IncomingInvoice> invoices = incomingInvoiceRepository.findByBankAccount(bankAccount);
        for (IncomingInvoice invoice : invoices) {
            final List<Document> attachedDocuments =
                    lookupAttachedPdfService.lookupIncomingInvoicePdfsFrom(invoice);
            documents.addAll(attachedDocuments);
        }
        return documents;
    }

    public String disableAct() {
        return choices0Act().isEmpty() ? "No invoices found for this bank account" : null;
    }


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    PaperclipRepository paperclipRepository;

}
