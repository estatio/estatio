package org.estatio.capex.dom.documents;

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

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.order.Order;
import org.estatio.dom.invoice.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class LookupAttachedPdfService {

    @Programmatic
    public Optional<Document> lookupIncomingInvoicePdfFrom(final IncomingInvoice incomingInvoice) {
        return lookupPdfFrom(incomingInvoice, DocumentTypeData.INCOMING_INVOICE);
    }

    @Programmatic
    public Optional<Document> lookupOrderPdfFrom(final Order order) {
        return lookupPdfFrom(order, DocumentTypeData.INCOMING_ORDER);
    }

    @Programmatic
    public Optional<Document> lookupPdfFrom(
            final Object domainObject,
            final DocumentTypeData documentTypeData) {
        return queryResultsCache.execute(
                () -> doLookupPdfFrom(domainObject, documentTypeData),
                LookupAttachedPdfService.class,
                "lookupPdfFrom", domainObject, documentTypeData);
    }

    private Optional<Document> doLookupPdfFrom(
            final Object domainObject,
            final DocumentTypeData documentTypeData) {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(domainObject);
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
