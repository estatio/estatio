package org.estatio.module.capex.contributions;

import java.util.Optional;

import javax.inject.Inject;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.bankaccount.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know about incoming invoices or documents.
 */
@Mixin(method = "prop")
public class BankAccount_ibanProof {

    private final BankAccount bankAccount;

    public BankAccount_ibanProof(final BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public static class DomainEvent extends ActionDomainEvent<Document> {
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_FIT, initialHeight = 1200)
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = BankAccount_ibanProof.DomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIbanProofPdfFrom(bankAccount);
        return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;
}
