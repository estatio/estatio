package org.estatio.module.capex.integtests.document;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDOHelper;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.restapi.DocumentServiceRestApi;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.NEW;
import static org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CATEGORISE;
import static org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.INSTANTIATE;

public class DocumentServiceRestApi_uploadGeneric_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                executionContext.executeChild(this, Person_enum.DanielOfficeAdministratorFr);
            }
        });
    }

    @Test
    public void when_incoming_document() throws Exception {

        // given
        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        // when
        final String fileName = "3020100123.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(DocumentServiceRestApi_uploadGeneric_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache

        wrap(documentService).uploadGeneric(blob, "INCOMING", "/FRA");
        transactionService.nextTransaction();

        // then
        List<Document> incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);

        final Document document = incomingDocumentsAfter.get(0);
        final Blob documentBlob = document.getBlob();

        assertThat(document.getAtPath()).isEqualTo("/FRA");
        assertThat(documentBlob.getName()).isEqualTo(blob.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
        assertThat(JDOHelper.getVersion(document)).isEqualTo(1L);
        assertThat(paperclipRepository.findByDocument(document).size()).isEqualTo(0);

        // and then also
        final List<IncomingDocumentCategorisationStateTransition> transitions =
                stateTransitionRepository.findByDomainObject(document);

        assertThat(transitions).hasSize(2);
        assertTransition(transitions.get(0),
                NEW, CATEGORISE, null);
        assertTransition(transitions.get(1),
                null, INSTANTIATE, NEW);

        // and when
        final String fileName2 = "3020100123-altered.pdf";
        final byte[] pdfBytes2 = Resources.toByteArray(
                Resources.getResource(DocumentServiceRestApi_uploadGeneric_IntegTest.class, fileName2));
        final Blob similarNamedBlob = new Blob(fileName, "application/pdf", pdfBytes2);

        wrap(documentService).uploadGeneric(similarNamedBlob, "INCOMING", "/FRA");

        transactionService.nextTransaction();

        // then
        incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(2);
        assertThat(incomingDocumentsAfter.get(0).getName()).isEqualTo(fileName);
        assertThat(incomingDocumentsAfter.get(0).getBlobBytes()).isEqualTo(similarNamedBlob.getBytes());
        assertThat(JDOHelper.getVersion(incomingDocumentsAfter.get(0))).isEqualTo(2L);
        assertThat(paperclipRepository.findByDocument(incomingDocumentsAfter.get(0)).size()).isEqualTo(1);
        assertThat(incomingDocumentsAfter.get(1).getName()).contains(fileName); // has prefix arch-[date time indication]-
        assertThat(incomingDocumentsAfter.get(1).getBlobBytes()).isEqualTo(documentBlob.getBytes());
        assertThat(JDOHelper.getVersion(incomingDocumentsAfter.get(1))).isEqualTo(1L);
        assertThat(paperclipRepository.findByDocument(incomingDocumentsAfter.get(1)).size()).isEqualTo(0);

        final String belgianBarCode = "6010012345.pdf";
        final Blob belgianBlob = new Blob(belgianBarCode, "application/pdf", pdfBytes2);
        Document belgianDocument = wrap(documentService).uploadGeneric(belgianBlob, "INCOMING", "/FRA");

        // then
        assertThat(belgianDocument.getAtPath()).isEqualTo("/BEL");

    }

    @Test
    public void when_incoming_italian_document() throws Exception {

        // given
        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        // when
        final String fileName = "2010101234.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(DocumentServiceRestApi_uploadGeneric_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);
        wrap(documentService).uploadGeneric(blob, "INCOMING", "/ITA");
        transactionService.nextTransaction();

        // then
        List<Document> incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);

        Document document = incomingDocumentsAfter.get(0);
        Blob documentBlob = document.getBlob();

        assertThat(document.getAtPath()).isEqualTo("/ITA");
        assertThat(documentBlob.getName()).isEqualTo(blob.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
        assertThat(JDOHelper.getVersion(document)).isEqualTo(1L);
        assertThat(document.getType()).isEqualTo(DocumentTypeData.INCOMING.findUsing(documentTypeRepository));

        // and then also
        assertThat(stateTransitionRepository.findByDomainObject(document)).hasSize(0);
        // TODO: when implemented assert link between doc and inc inv

        // and when again uploading doc with the same name (but different bytes)
        final byte[] pdfBytes2 = Resources.toByteArray(
                Resources.getResource(DocumentServiceRestApi_uploadGeneric_IntegTest.class, "2010101234-altered.pdf"));
        final Blob similarNamedBlobWithDifferentBytes = new Blob(fileName, "application/pdf", pdfBytes2);
        wrap(documentService).uploadGeneric(similarNamedBlobWithDifferentBytes, "INCOMING", "/ITA");
        transactionService.nextTransaction();

        // then
        incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(2);

        document = incomingDocumentsAfter.get(0);
        documentBlob = document.getBlob();

        assertThat(document.getAtPath()).isEqualTo("/ITA");

        assertThat(documentBlob.getName()).isEqualTo(similarNamedBlobWithDifferentBytes.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(similarNamedBlobWithDifferentBytes.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(similarNamedBlobWithDifferentBytes.getBytes());

        document = incomingDocumentsAfter.get(1);
        documentBlob = document.getBlob();

        assertThat(document.getAtPath()).isEqualTo("/ITA");
        assertThat(documentBlob.getName()).contains(blob.getName());

        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
        assertThat(JDOHelper.getVersion(document)).isEqualTo(1L);
        assertThat(document.getType()).isEqualTo(DocumentTypeData.INCOMING.findUsing(documentTypeRepository));
    }

    @Test
    public void when_italian_tax_receipt() throws Exception {

        // given
        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        // when
        final String fileName = "some_tax_receipt.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(DocumentServiceRestApi_uploadGeneric_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);

        wrap(documentService).uploadGeneric(blob, "TAX_REGISTER", "/ITA");

        transactionService.nextTransaction();

        // then
        List<Document> incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);

        Document document = incomingDocumentsAfter.get(0);
        Blob documentBlob = document.getBlob();

        assertThat(document.getAtPath()).isEqualTo("/ITA");
        assertThat(documentBlob.getName()).isEqualTo(blob.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
        assertThat(JDOHelper.getVersion(document)).isEqualTo(1L);
        assertThat(document.getType()).isEqualTo(DocumentTypeData.TAX_REGISTER.findUsing(documentTypeRepository));

    }

    static void assertTransition(
            final IncomingDocumentCategorisationStateTransition transition,
            final IncomingDocumentCategorisationState from,
            final IncomingDocumentCategorisationStateTransitionType type,
            final IncomingDocumentCategorisationState to) {

        assertThat(transition.getTransitionType()).isEqualTo(type);

        if (from != null) {
            assertThat(transition.getFromState()).isEqualTo(from);
        } else {
            assertThat(transition.getFromState()).isNull();
        }

        if (to != null) {
            assertThat(transition.getToState()).isEqualTo(to);
        } else {
            assertThat(transition.getToState()).isNull();
        }
    }

    @Inject
    IncomingDocumentRepository repository;

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository stateTransitionRepository;

    @Inject
    DocumentServiceRestApi documentService;

    @Inject
    TransactionService transactionService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject DocumentTypeRepository documentTypeRepository;

}
