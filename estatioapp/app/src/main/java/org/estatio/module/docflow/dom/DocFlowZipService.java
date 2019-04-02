package org.estatio.module.docflow.dom;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocFlowZipService {

    public static final String PAPERCLIP_ROLE_NAME_GENERATED = "generated from xml";
    public static final String PAPERCLIP_ROLE_NAME_SUPPLIERS = "supplier's";

    public DocFlowZip handle(
            final long sdId,
            final Clob xmlFileMetadatiIfAny,
            final Clob xmlFatturaElettronica,
            final Blob pdfFatturaElettronica,
            final Blob p7mFatturaElettronicaIfAny,
            final Blob pdfSupplierIfAny,
            final String atPath,
            final String sha256) {


        final DocFlowZip docFlowZip = docFlowZipFactory.createTransient(sdId, atPath, sha256);

        // we disallow updates, fail-fast
        final DocFlowZip existingZipIfAny = docFlowZipRepository.findBySdiId(sdId);
        if(existingZipIfAny != null) {
            if (docFlowZip.isSameAs(existingZipIfAny)) {
                // nothing to do
                return existingZipIfAny;
            } else {
                throw new IllegalStateException("Attempt to update DocFlow sdiId='" + sdId + "' rejected");
            }
        }


        //
        // persist the DocFlowZip, and attach its constituent parts as documents
        //
        docFlowZipRepository.persist(docFlowZip);

        if(xmlFileMetadatiIfAny != null) {
            final DocumentType docTypeDocflowMetadata =
                    DocumentTypeData.DOCFLOW_METADATA.findUsing(documentTypeRepository);
            documentService.createAndAttachDocumentForClob(
                    docTypeDocflowMetadata, atPath,
                    xmlFileMetadatiIfAny.getName(), xmlFileMetadatiIfAny,
                    null, docFlowZip);
        }

        final DocumentType docTypeDocflowFaturraXml =
                DocumentTypeData.DOCFLOW_FATURRA_XML.findUsing(documentTypeRepository);
        documentService.createAndAttachDocumentForClob(
                docTypeDocflowFaturraXml, atPath,
                xmlFatturaElettronica.getName(), xmlFatturaElettronica,
                null, docFlowZip);


        // TODO: what should the name be of this?
        //  how do we match to it from CodaDocHead/IncomingInvoice
        final DocumentType docTypeIncomingInvoice =
                DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);
        documentService.createAndAttachDocumentForBlob(
                docTypeIncomingInvoice, atPath,
                pdfFatturaElettronica.getName(), pdfFatturaElettronica,
                PAPERCLIP_ROLE_NAME_GENERATED, docFlowZip);

        if(p7mFatturaElettronicaIfAny != null) {
            final DocumentType docTypeDocflowFaturraP7m =
                    DocumentTypeData.DOCFLOW_FATURRA_P7M.findUsing(documentTypeRepository);
            documentService.createAndAttachDocumentForBlob(
                    docTypeDocflowFaturraP7m, atPath,
                    p7mFatturaElettronicaIfAny.getName(), p7mFatturaElettronicaIfAny,
                    null, docFlowZip);
        }

        if(pdfSupplierIfAny != null) {
            documentService.createAndAttachDocumentForBlob(
                    docTypeIncomingInvoice, atPath,
                    pdfSupplierIfAny.getName(), pdfSupplierIfAny,
                    PAPERCLIP_ROLE_NAME_SUPPLIERS, docFlowZip);
        }

        return docFlowZip;
    }

    @Inject
    DocFlowZipFactory docFlowZipFactory;

    @Inject
    DocFlowZipRepository docFlowZipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentService documentService;

}
