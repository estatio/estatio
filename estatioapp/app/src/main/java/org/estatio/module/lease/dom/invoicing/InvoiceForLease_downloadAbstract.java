package org.estatio.module.lease.dom.invoicing;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.paperclips.InvoiceDocAndCommService;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommAbstract;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForPrelimLetter;

public abstract class InvoiceForLease_downloadAbstract<T extends DocAndCommAbstract<T>> {

    private final InvoiceForLease invoice;
    private final DocAndCommAbstract.Factory.DncProvider<T> provider;
    private final DocumentTypeData documentTypeData;

    public InvoiceForLease_downloadAbstract(
            final InvoiceForLease invoice,
            final DocAndCommAbstract.Factory.DncProvider<T> provider,
            final DocumentTypeData documentTypeData) {
        this.invoice = invoice;
        this.provider = provider;
        this.documentTypeData = documentTypeData;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<T> act() {
        return findDocAndComms();
    }

    public String disableAct() {
        final List<T> docAndComms = findDocAndComms();
        for (T docAndComm : docAndComms) {
            final Document document = invoiceDocAndCommService.findDocument(docAndComm.getInvoice(), getDocumentType());
            if(document != null && document.getState() == DocumentState.RENDERED) {
                return null;
            }
        }
        return "No documents have been prepared & rendered";
    }

    DocumentType getDocumentType() {
        return documentTypeData.findUsing(documentTypeRepository, queryResultsCache);
    }


    private List<T> findDocAndComms() {
        return docAndCommFactory.documentsAndCommunicationsFor(invoice, provider);
    }

    @Inject
    DocAndCommForPrelimLetter.Factory docAndCommFactory;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;

}
