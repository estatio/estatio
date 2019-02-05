package org.estatio.module.capex.integtests.document;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.Document_categorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsOtherInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.restapi.DocumentServiceRestApi;
import org.estatio.module.capex.seed.DocumentTypeFSForIncoming;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocumentRepository_UpsertAndArchive_IntegTest extends CapexModuleIntegTestAbstract {


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new DocumentTypeFSForIncoming());
                executionContext.executeChild(this, IncomingInvoiceNoDocument_enum.invoiceForItaNoOrder);
                executionContext.executeChild(this, IncomingInvoiceNoDocument_enum.invoiceForItaRecoverable);
            }
        });
    }


    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    Blob blob;
    Blob blobDifferentContent;
    Blob blobDifferentContentAgain;
    Blob otherBlob;

    DocumentType documentType;
    IncomingInvoice incomingInvoice;
    IncomingInvoice incomingInvoice2;

    @Before
    public void setUp() throws Exception {
        documentType = DocumentTypeData.INCOMING.findUsing(documentTypeRepository);
        incomingInvoice = IncomingInvoiceNoDocument_enum.invoiceForItaNoOrder.findUsing(serviceRegistry);
        incomingInvoice2 = IncomingInvoiceNoDocument_enum.invoiceForItaRecoverable.findUsing(serviceRegistry);

        this.blob                      = load("3020100123.pdf");
        this.blobDifferentContent      = load("3020100123.pdf", "3020100123-altered.pdf");       // use the same name
        this.blobDifferentContentAgain = load("3020100123.pdf", "3020100123-altered-again.pdf"); // use the same name
        this.otherBlob = load("3020100124.pdf");
    }

    @Test
    public void when_new_document() throws IOException {

        // given
        final List<Document> allIncomingDocuments = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocuments).isEmpty();
        final List<Paperclip> paperclips = paperclipRepository.listAll();
        assertThat(paperclips).isEmpty();

        transactionService.nextTransaction();

        // when
        final Document document1 = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blob);
        transactionService.nextTransaction();

        // then no new paperclips, but document persisted
        final List<Document> allIncomingDocumentsAfter = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocumentsAfter).hasSize(1);
        final Document document = allIncomingDocumentsAfter.get(0);

        assertThat(document).isSameAs(document1);

        assertThat(document.getName()).isEqualTo(blob.getName());
        assertThat(document.getBlobBytes()).isEqualTo(blob.getBytes());
        assertThat(document.getMimeType()).isEqualTo(blob.getMimeType().toString());

        final List<Paperclip> paperclipsAfter = paperclipRepository.listAll();
        assertThat(paperclipsAfter).isEmpty();
    }

    @Test
    public void when_unchanged_document() throws IOException {

        // given
        final List<Document> allIncomingDocuments = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocuments).isEmpty();
        final List<Paperclip> paperclips = paperclipRepository.listAll();
        assertThat(paperclips).isEmpty();
        transactionService.nextTransaction();

        // when
        final Document document1 = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blob);
        transactionService.nextTransaction();

        // and when
        final Document document2 = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blob);
        transactionService.nextTransaction();

        // then the second identical blob is effectively ignored
        assertThat(document1).isSameAs(document2);

        final List<Document> allIncomingDocumentsAfter = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocumentsAfter).hasSize(1);
        final Document document = allIncomingDocumentsAfter.get(0);

        assertThat(document).isSameAs(document1);

        final List<Paperclip> paperclipsAfter = paperclipRepository.listAll();
        assertThat(paperclipsAfter).isEmpty();
    }

    @Test
    public void when_updated_document_not_attached() throws IOException {

        // given
        final List<Paperclip> paperclips = paperclipRepository.listAll();
        assertThat(paperclips).isEmpty();
        final List<Document> allIncomingDocuments = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocuments).isEmpty();

        // when
        final Document documentOrig = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blob);
        transactionService.nextTransaction();

        // and when
        final Document documentReplacement = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blobDifferentContent);
        transactionService.nextTransaction();

        // then there are two documents
        assertThat(documentOrig).isNotSameAs(documentReplacement);

        final List<Document> allIncomingDocumentsAfter = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocumentsAfter).hasSize(2);

        // ... and the new one links to the old.
        final List<Paperclip> paperclipsForReplacement = paperclipRepository.findByAttachedTo(documentReplacement);
        assertThat(paperclipsForReplacement).hasSize(1);

        final Paperclip paperclip = paperclipsForReplacement.get(0);

        assertThat(paperclip.getAttachedTo()).isSameAs(documentReplacement);
        assertThat(paperclip.getRoleName()).isEqualTo("replaces");
        assertThat(paperclip.getDocument()).isSameAs(documentOrig);

        // and there is just once paperclip in all
        final List<Paperclip> paperclipsAfter = paperclipRepository.listAll();
        assertThat(paperclipsAfter).hasSize(1);
    }

    @Inject IncomingDocumentCategorisationStateTransition.Repository repository;

    @Inject DocumentServiceRestApi api;

    @Test
    public void when_updated_document_already_attached() throws IOException {

        // given
        final Document documentOrig = wrap(api).uploadGeneric(blob, "INCOMING", "/FRA");
        transactionService.nextTransaction();

        List<IncomingDocumentCategorisationStateTransition> transitionsForOrigDoc = repository.findByDomainObject(documentOrig);
        assertThat(transitionsForOrigDoc).hasSize(2);
        assertThat(transitionsForOrigDoc.get(0).isCompleted()).isFalse();

        wrap(mixin(Document_categoriseAsOtherInvoice.class, documentOrig)).act(IncomingInvoiceType.CORPORATE_EXPENSES, null);
        assertThat(transitionsForOrigDoc.get(0).isCompleted()).isTrue();
        assertThat(transitionsForOrigDoc.get(0).getTask().isCompleted()).isTrue();
        assertThat(mixin(Document_categorisationState.class, documentOrig).prop()).isEqualTo(IncomingDocumentCategorisationState.CATEGORISED);

        final List<Paperclip> paperclipsBefore = paperclipRepository.listAll();
        assertThat(paperclipsBefore).hasSize(1);
        final Paperclip paperclipForInvoiceBefore = paperclipsBefore.get(0);
        incomingInvoice = (IncomingInvoice) paperclipForInvoiceBefore.getAttachedTo();

        assertThat(paperclipForInvoiceBefore.getRoleName()).isNull();
        assertThat(paperclipForInvoiceBefore.getDocument()).isSameAs(documentOrig);

        // when
        final Document documentReplacement = wrap(api).uploadGeneric(blobDifferentContent, "INCOMING", "/FRA");
        transactionService.nextTransaction();

        // then there are two documents
        assertThat(documentOrig).isNotSameAs(documentReplacement);
        assertThat(documentOrig.getType()).isEqualTo(documentReplacement.getType());
        assertThat(documentReplacement.getType().getName()).isEqualTo("Incoming Invoice");

        assertThat(repository.findByDomainObject(documentReplacement)).isEmpty();
        assertThat(mixin(Document_categorisationState.class, documentReplacement).prop()).isNull();

        // ... and the new one links to the old
        final List<Document> allIncomingDocumentsAfter = incomingDocumentRepository.findAllIncomingDocuments();
        assertThat(allIncomingDocumentsAfter).hasSize(2);

        final List<Paperclip> paperclipsForReplacement = paperclipRepository.findByAttachedTo(documentReplacement);
        assertThat(paperclipsForReplacement).hasSize(1);

        final Paperclip paperclipForDoc = paperclipsForReplacement.get(0);

        assertThat(paperclipForDoc.getAttachedTo()).isSameAs(documentReplacement);
        assertThat(paperclipForDoc.getRoleName()).isEqualTo("replaces");
        assertThat(paperclipForDoc.getDocument()).isSameAs(documentOrig);

        // ... and the invoice's paperclip is adjusted
        final List<Paperclip> paperclipsForInvoiceAfter = paperclipRepository.findByAttachedTo(incomingInvoice);
        assertThat(paperclipsForInvoiceAfter).hasSize(1);
        final Paperclip paperclipForInvoiceAfter = paperclipsForInvoiceAfter.get(0);

        assertThat(paperclipForInvoiceAfter).isSameAs(paperclipForInvoiceBefore);

        assertThat(paperclipForInvoiceAfter.getAttachedTo()).isSameAs(incomingInvoice);
        assertThat(paperclipForInvoiceAfter.getRoleName()).isNull();
        assertThat(paperclipForInvoiceAfter.getDocument()).isSameAs(documentReplacement);

        // and there are two paperclips in all
        final List<Paperclip> paperclipsAfter = paperclipRepository.listAll();
        assertThat(paperclipsAfter).hasSize(2);


        // and when we update but with the same content
        final Document documentReplacement2 = wrap(api).uploadGeneric(blobDifferentContent, "INCOMING", "/FRA");
        transactionService.nextTransaction();

        // then there is no change
        assertThat(documentReplacement2).isSameAs(documentReplacement);

        final List<Paperclip> paperclipsAfter2 = paperclipRepository.listAll();
        assertThat(paperclipsAfter2).hasSize(2);


        // and when we update one further time but with different content
        final Document documentReplacement3 = wrap(api).uploadGeneric(blobDifferentContentAgain, "INCOMING", "/FRA");
        transactionService.nextTransaction();

        // then this time there is a change
        assertThat(documentReplacement3).isNotSameAs(documentReplacement);
        assertThat(documentReplacement3.getType().getName()).isEqualTo("Incoming Invoice");
        assertThat(mixin(Document_categorisationState.class, documentReplacement3).prop()).isNull();
        
        // ... the second replacement replaces the first
        final List<Paperclip> paperclipsForReplacement3 = paperclipRepository.findByAttachedTo(documentReplacement3);
        assertThat(paperclipsForReplacement3).hasSize(1);
        final Paperclip paperclipForReplacement3 = paperclipsForReplacement3.get(0);

        assertThat(paperclipForReplacement3.getAttachedTo()).isSameAs(documentReplacement3);
        assertThat(paperclipForReplacement3.getRoleName()).isEqualTo("replaces");
        assertThat(paperclipForReplacement3.getDocument()).isSameAs(documentReplacement);

        // ... the invoice's paperclip is updated once more.
        final List<Paperclip> paperclipsForInvoiceAfter3 = paperclipRepository.findByAttachedTo(incomingInvoice);
        assertThat(paperclipsForInvoiceAfter3).hasSize(1);
        final Paperclip paperclipForInvoiceAfter3 = paperclipsForInvoiceAfter.get(0);

        assertThat(paperclipForInvoiceAfter3).isSameAs(paperclipForInvoiceBefore);

        assertThat(paperclipForInvoiceAfter3.getAttachedTo()).isSameAs(incomingInvoice);
        assertThat(paperclipForInvoiceAfter3.getRoleName()).isNull();
        assertThat(paperclipForInvoiceAfter3.getDocument()).isSameAs(documentReplacement3);

        // ... the paperclip between the first replacement and the original is unchanged
        final List<Paperclip> paperclipsForDocReplacement = paperclipRepository.findByAttachedTo(documentReplacement);
        assertThat(paperclipsForDocReplacement).hasSize(1);
        final Paperclip paperclipForDocReplacement = paperclipsForDocReplacement.get(0);

        assertThat(paperclipForDocReplacement).isSameAs(paperclipForDoc);

        assertThat(paperclipForDocReplacement.getAttachedTo()).isSameAs(documentReplacement);
        assertThat(paperclipForDocReplacement.getRoleName()).isEqualTo("replaces");
        assertThat(paperclipForDocReplacement.getDocument()).isSameAs(documentOrig);

        // ... and there is one further paperclip altogether
        final List<Paperclip> paperclipsAfter3 = paperclipRepository.listAll();
        assertThat(paperclipsAfter3).hasSize(3);

    }

    @Test
    public void when_updated_document_already_attached_twice() throws IOException {

        // given
        final Document document1 = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blob);
        final Document document2 = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", otherBlob.getName(), otherBlob);
        paperclipRepository.attach(document1, null, incomingInvoice);
        paperclipRepository.attach(document1, "A", incomingInvoice2);
        paperclipRepository.attach(document2, "B", incomingInvoice2);

        final List<Paperclip> paperclipsBefore = paperclipRepository.listAll();
        assertThat(paperclipsBefore).hasSize(3);

        // when
        final Document documentReplacement = incomingDocumentRepository
                .upsertAndArchive(documentType, "/ITA", blob.getName(), blobDifferentContent);
        transactionService.nextTransaction();

        // then invoice1's paperclip is updated
        final List<Paperclip> paperclipsIncomingInvoice1 = paperclipRepository.findByAttachedTo(incomingInvoice);
        assertThat(paperclipsIncomingInvoice1).hasSize(1);
        final Paperclip paperclipIncomingInvoice1 = paperclipsIncomingInvoice1.get(0);

        assertThat(paperclipIncomingInvoice1.getAttachedTo()).isSameAs(incomingInvoice);
        assertThat(paperclipIncomingInvoice1.getRoleName()).isNull();
        assertThat(paperclipIncomingInvoice1.getDocument()).isSameAs(documentReplacement);

        // .. and one of invoice2's paperclip is updated
        final List<Paperclip> paperclipsIncomingInvoice2RoleA = paperclipRepository.findByAttachedToAndRoleName(incomingInvoice2, "A");
        assertThat(paperclipsIncomingInvoice2RoleA).hasSize(1);
        final Paperclip paperclipIncomingInvoice2RoleA = paperclipsIncomingInvoice2RoleA.get(0);

        assertThat(paperclipIncomingInvoice2RoleA.getAttachedTo()).isSameAs(incomingInvoice2);
        assertThat(paperclipIncomingInvoice2RoleA.getRoleName()).isEqualTo("A");
        assertThat(paperclipIncomingInvoice2RoleA.getDocument()).isSameAs(documentReplacement);

        // .. but the other is unchanged
        final List<Paperclip> paperclipsIncomingInvoice2RoleB = paperclipRepository.findByAttachedToAndRoleName(incomingInvoice2, "B");
        assertThat(paperclipsIncomingInvoice2RoleB).hasSize(1);
        final Paperclip paperclipIncomingInvoice2RoleB = paperclipsIncomingInvoice2RoleB.get(0);

        assertThat(paperclipIncomingInvoice2RoleB.getAttachedTo()).isSameAs(incomingInvoice2);
        assertThat(paperclipIncomingInvoice2RoleB.getRoleName()).isEqualTo("B");
        assertThat(paperclipIncomingInvoice2RoleB.getDocument()).isSameAs(document2);

        // .. and ther is one additional paperclip for replacementDocument -> document1
        final List<Paperclip> paperclipsAfter = paperclipRepository.listAll();
        assertThat(paperclipsAfter).hasSize(4);

        paperclipsAfter.removeAll(paperclipsBefore);
        assertThat(paperclipsAfter).hasSize(1);

        final Paperclip newPaperclip = paperclipsAfter.get(0);

        assertThat(newPaperclip.getAttachedTo()).isSameAs(documentReplacement);
        assertThat(newPaperclip.getRoleName()).isEqualTo("replaces");
        assertThat(newPaperclip.getDocument()).isSameAs(document1);

    }


    private Blob load(final String fileName) throws IOException {
        return load(fileName, fileName);
    }
    private Blob load(final String name, final String fileName) throws IOException {
        final byte[] pdfBytes = Resources.toByteArray(Resources.getResource(getClass(), fileName));
        return new Blob(name, "application/pdf", pdfBytes);
    }


}
