package org.incode.module.document.dom.api;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.services.DocumentCreatorService;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentService {

    /**
     * @param documentName - override the name of the blob (if null, then uses the blob's name)
     */
    @Programmatic
    public Document createForBlob(
            final DocumentType documentType,
            final String documentAtPath,
            String documentName,
            final Blob blob) {
        documentName = documentName != null? documentName: blob.getName();

        final Document document = documentRepository.create(
                documentType, documentAtPath, documentName, blob.getMimeType().getBaseType());

        document.setRenderedAt(clockService.nowAsDateTime());
        document.setState(DocumentState.RENDERED);
        document.setSort(DocumentSort.BLOB);
        document.setBlobBytes(blob.getBytes());
        return document;
    }

    /**
     * @param documentName - override the name of the clob (if null, then uses the clob's name)
     */
    @Programmatic
    public Document createForClob(
            final DocumentType documentType,
            final String documentAtPath,
            String documentName,
            final Clob clob) {
        documentName = documentName != null? documentName: clob.getName();

        final Document document = documentRepository.create(
                documentType, documentAtPath, documentName, clob.getMimeType().getBaseType());

        document.setRenderedAt(clockService.nowAsDateTime());
        document.setState(DocumentState.RENDERED);
        document.setSort(DocumentSort.CLOB);
        document.setClobChars(clob.getChars().toString());
        return document;
    }

    /**
     * @param documentName - override the name of the blob (if null, then uses the blob's name)
     */
    @Programmatic
    public Document createAndAttachDocumentForBlob(
            final DocumentType documentType,
            final String documentAtPath,
            String documentName,
            final Blob blob,
            final String paperclipRoleName,
            final Object paperclipAttachTo){

        final Document document = createForBlob(documentType, documentAtPath, documentName, blob);

        paperclipRepository.attach(document, paperclipRoleName, paperclipAttachTo);

        return document;
    }


    /**
     * @param documentName - override the name of the clob (if null, then uses the clob's name)
     */
    @Programmatic
    public Document createAndAttachDocumentForClob(
            final DocumentType documentType,
            final String documentAtPath,
            String documentName,
            final Clob clob,
            final String paperclipRoleName,
            final Object paperclipAttachTo){

        final Document document = createForClob(documentType, documentAtPath, documentName, clob);

        paperclipRepository.attach(document, paperclipRoleName, paperclipAttachTo);

        return document;
    }


    @Programmatic
    public boolean canCreateDocumentAndAttachPaperclips(
            final Object domainObject,
            final DocumentTemplate template) {
        return creatorService.canCreateDocumentAndAttachPaperclips(domainObject, template);
    }

    @Programmatic
    public Document createDocumentAndAttachPaperclips(
            final Object domainObject,
            final DocumentTemplate template) {

        return creatorService.createDocumentAndAttachPaperclips(domainObject, template);
    }

    @Inject
    DocumentCreatorService creatorService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    ClockService clockService;

}
