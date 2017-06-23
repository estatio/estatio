package org.estatio.capex.dom.invoice;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.invoice.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoicePdfService {

    @Programmatic
    public Optional<Document> lookupIncomingInvoicePdfFrom(
            final IncomingInvoice incomingInvoice) {
        return lookupPdfFrom(incomingInvoice, DocumentTypeData.INCOMING_INVOICE);
    }

    @Programmatic
    public Optional<Document> lookupPdfFrom(
            final IncomingInvoice incomingInvoice,
            final DocumentTypeData documentTypeData) {
        return queryResultsCache.execute(
                () -> doLookupPdfFrom(incomingInvoice, documentTypeData),
                IncomingInvoicePdfService.class,
                "lookupPdfFrom", incomingInvoice, documentTypeData);
    }

    private Optional<Document> doLookupPdfFrom(
            final IncomingInvoice incomingInvoice,
            final DocumentTypeData documentTypeData) {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(incomingInvoice);
        return paperclips.stream()
                .map(Paperclip::getDocument)
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .filter(documentTypeData::isDocTypeFor)
                .filter(document -> Objects.equals(document.getMimeType(), "application/pdf"))
                .findFirst();
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaperclipRepository paperclipRepository;

}
