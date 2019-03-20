/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.dom.invoicing.comms;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.base.spi.DeriveBlobFromDummyPdfArg1;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.spi.DocumentAttachmentAdvisor;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.paperclips.InvoiceDocAndCommService;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

/**
 * TODO: REVIEW: this mixin could in theory be inlined, but it's a lot of functionality in its own right; and maybe we want to keep invoices and documents decoupled?
 */
@Mixin
public class InvoiceForLease_attachSupportingDocument {

    private final InvoiceForLease invoiceForLease;

    public InvoiceForLease_attachSupportingDocument(final InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            commandDtoProcessor = DeriveBlobFromDummyPdfArg1.class
    )
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "paperclip")
    public Invoice $$(
            final DocumentType supportingDocumentType,
            @Parameter(fileAccept = MimeTypes.APPLICATION_PDF.asStr())
            @ParameterLayout(named = "Receipt (PDF)")
            final Blob blob,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String fileName,
            final String roleName) throws IOException {


        //
        // if appropriate, we will automatically attach the supporting doc (once created)
        // to any unsent documents for this Invoice
        // before we do anything, therefore, we get hold of those invoice documents.
        //
        DocumentTypeData supportedBy = DocumentTypeData.supportedBy(supportingDocumentType);
        final List<DocumentAbstract> unsentDocuments = findUnsentDocumentsFor(invoiceForLease, supportedBy);

        //
        // now we create the receiptDoc, and attach to the invoice
        //
        String documentName = determineName(blob, fileName);

        // unlike documents that are generated from a template (where we call documentTemplate#render), in this case
        // we have the actual bytes; so we just set up the remaining state of the document manually.


        final Document supportingDoc = documentService.createAndAttachDocumentForBlob(
                supportingDocumentType, this.invoiceForLease.getAtPath(), documentName, blob,
                PaperclipRoleNames.SUPPORTING_DOCUMENT, this.invoiceForLease);


        //
        // finally we also attach the newly created supporting doc to the unsent document(s) we picked up previously.
        //
        for (DocumentAbstract unsentDocument : unsentDocuments) {
            paperclipRepository.attach(supportingDoc, roleName, unsentDocument);
        }

        return this.invoiceForLease;
    }

    @Inject
    DocumentService documentService;

    private List<DocumentAbstract> findUnsentDocumentsFor(
            final InvoiceForLease invoice,
            final DocumentTypeData docTypeData) {

        final DocumentType documentType = docTypeData.findUsing(documentTypeRepository, queryResultsCache);

        final List<DocumentAbstract> unsentDocuments = Lists.newArrayList();

        final List<Paperclip> existingInvoicePaperclips = paperclipRepository.findByAttachedTo(invoice);
        for (Paperclip paperclip : existingInvoicePaperclips) {
            final DocumentAbstract document = paperclip.getDocument();
            if(document.getType() == documentType) {
                boolean sent = whetherSent(document);
                if(!sent) {
                    unsentDocuments.add(document);
                }
            }
        }

        return unsentDocuments;
    }

    private boolean whetherSent(final DocumentAbstract document) {
        final List<Paperclip> invDocPaperclips = paperclipRepository.findByDocument(document);
        for (final Paperclip invDocPaperclip : invDocPaperclips) {
            final Object attachedTo = invDocPaperclip.getAttachedTo();
            if(attachedTo instanceof Communication) {
                return true;
            }
        }
        return false;
    }

    private static String determineName(
            final Blob document,
            final String fileName) {
        String name = fileName != null ? fileName : document.getName();
        if(!name.toLowerCase().endsWith(".pdf")) {
            name = name + ".pdf";
        }
        return name;
    }

    public List<DocumentType> choices0$$() {
        return documentAttachmentAdvisor.documentTypeChoicesFor(null);
    }

    public DocumentType default0$$() {
        return documentAttachmentAdvisor.documentTypeDefaultFor(null);
    }

    public List<String> choices3$$() {
        return documentAttachmentAdvisor.roleNameChoicesFor(null);
    }

    public String default3$$() {
        return documentAttachmentAdvisor.roleNameDefaultFor(null);
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;

    @Inject
    ClockService clockService;

    @Inject
    FactoryService factoryService;

    @Inject
    DocumentAttachmentAdvisor documentAttachmentAdvisor;

    /**
     * Implementation of SPI of document model, returning the available {@link DocumentType}s and also roles which
     * can be used as a supporting document for the provided {@link Document}.
     *
     * <p>
     *     This service is also used by the {@link InvoiceForLease_attachSupportingDocument} mixin which will call it with a
     *     <tt>null</tt> {@link Document}.
     * </p>
     */
    @DomainService
    public static class DocumentAttachmentAdvisorForInvoiceSupportingDocuments implements DocumentAttachmentAdvisor {

        @Override
        public List<DocumentType> documentTypeChoicesFor(final Document document) {

            if(document == null) {
                // assume being called by InvoiceForLease_attachSupportingDocument
                return DocumentTypeData.supportingDocTypesUsing(documentTypeRepository, queryResultsCache);
            }

            // otherwise, look up the DocumentTypeData for the provided document's type, then return the
            // (DocumentType(s) corresponding to the) DocumentTypeData(s) that support this
            final DocumentTypeData documentType = DocumentTypeData.docTypeDataFor(document);
            if(documentType == null) {
                throw new IllegalArgumentException("Could not locate the document type of document " + document);
            }

            final List<DocumentTypeData> supports = DocumentTypeData.supports(documentType);
            return DocumentTypeData.findUsing(supports, documentTypeRepository, queryResultsCache);
        }

        @Override
        public DocumentType documentTypeDefaultFor(final Document document) {
            List<DocumentType> documentTypes = documentTypeChoicesFor(document);
            return !documentTypes.isEmpty() ? documentTypes.get(0) : null;
        }

        @Override
        public List<String> roleNameChoicesFor(final Document document) {
            return Lists.newArrayList(PaperclipRoleNames.INVOICE_DOCUMENT_SUPPORTED_BY);
        }

        @Override
        public String roleNameDefaultFor(final Document document) {
            return roleNameChoicesFor(document).get(0);
        }

        @Inject
        DocumentTypeRepository documentTypeRepository;
        @Inject
        QueryResultsCache queryResultsCache;

    }
}
