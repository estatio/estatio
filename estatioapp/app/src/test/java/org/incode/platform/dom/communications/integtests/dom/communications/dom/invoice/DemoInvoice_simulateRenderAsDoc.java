package org.incode.platform.dom.communications.integtests.dom.communications.dom.invoice;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.Document_attachSupportingPdf;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoice;
import org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.doctypes.DocumentType_and_DocumentTemplates_createSome;

/**
 * Being lazy here... don't want to set up all the ref data etc to actually render this DemoInvoice as some sort of
 * Document, so instead manually create a Document from an already-existing Blob.
 *
 * This is similar to the {@link Document_attachSupportingPdf} that the document module provides, however the "attachPdf"
 * functionality only allows PDFs to be attached to existing {@link Document}s, whereas here we want to attach a
 * {@link Document} to our demo invoice.
 */
@Mixin
public class DemoInvoice_simulateRenderAsDoc {

    private static final String AT_PATH = "/";
    private static final String ROLE_NAME = null;

    private final DemoInvoice invoice;

    public DemoInvoice_simulateRenderAsDoc(final DemoInvoice invoice) {
        this.invoice = invoice;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(
            name = "documents",
            sequence = "1"
    )
    public Document $$(
            @Parameter(fileAccept = MimeTypes.APPLICATION_PDF.asStr())
            final Blob document,
            @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "File name")
            final String fileName
        ) throws IOException {

        String name = determineName(document, fileName);

        final DocumentType documentType = findDocumentType(DocumentType_and_DocumentTemplates_createSome.DOC_TYPE_REF_INVOICE);

        final Document receiptDoc = documentRepository
                .create(documentType, AT_PATH,
                        name,
                        document.getMimeType().getBaseType());

        // unlike documents that are generated from a template (where we call documentTemplate#render), in this case
        // we have the actual bytes; so we just set up the remaining state of the document manually.
        receiptDoc.setRenderedAt(clockService.nowAsDateTime());
        receiptDoc.setState(DocumentState.RENDERED);
        receiptDoc.setSort(DocumentSort.BLOB);
        receiptDoc.setBlobBytes(document.getBytes());

        paperclipRepository.attach(receiptDoc, ROLE_NAME, invoice);

        return receiptDoc;
    }


    private String determineName(
            final Blob document,
            final String fileName) {
        String name = fileName != null ? fileName : document.getName();
        if(!name.endsWith(".pdf")) {
            name = name + ".pdf";
        }
        return name;
    }

    private DocumentType findDocumentType(final String ref) {
        return documentTypeRepository.findByReference(ref);
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    ClockService clockService;

}
