package org.estatio.integtests.capex.document;

import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.DocumentMenu;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.NEW;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CATEGORISE;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.INSTANTIATE;

public class DocumentMenu_Upload_IntegTest extends EstatioIntegrationTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
            }
        });
    }

    @Test
    public void happy_case() throws Exception {

        // given
        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        // when
        final String fileName = "1020100123.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(DocumentMenu_Upload_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);
        wrap(documentMenu).upload(blob);
        transactionService.nextTransaction();

        // then
        List<Document> incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);

        final Document document = incomingDocumentsAfter.get(0);
        final Blob documentBlob = document.getBlob();

        assertThat(documentBlob.getName()).isEqualTo(blob.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
        assertThat(document.dnGetVersion()).isEqualTo(1L);
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
        final String fileName2 = "1020100123-altered.pdf";
        final byte[] pdfBytes2 = Resources.toByteArray(
                Resources.getResource(DocumentMenu_Upload_IntegTest.class, fileName2));
        final Blob similarNamedBlob = new Blob(fileName, "application/pdf", pdfBytes2);
        wrap(documentMenu).upload(similarNamedBlob);
        transactionService.nextTransaction();

        // then
        incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(2);
        assertThat(incomingDocumentsAfter.get(0).getName()).isEqualTo(fileName);
        assertThat(incomingDocumentsAfter.get(0).getBlobBytes()).isEqualTo(similarNamedBlob.getBytes());
        assertThat(incomingDocumentsAfter.get(0).dnGetVersion()).isEqualTo(2L);
        assertThat(paperclipRepository.findByDocument(incomingDocumentsAfter.get(0)).size()).isEqualTo(1);
        assertThat(incomingDocumentsAfter.get(1).getName()).contains(fileName); // has prefix arch-[date time indication]-
        assertThat(incomingDocumentsAfter.get(1).getBlobBytes()).isEqualTo(documentBlob.getBytes());
        assertThat(incomingDocumentsAfter.get(1).dnGetVersion()).isEqualTo(1L);
        assertThat(paperclipRepository.findByDocument(incomingDocumentsAfter.get(1)).size()).isEqualTo(0);
    }


    static void assertTransition(
            final IncomingDocumentCategorisationStateTransition transition,
            final IncomingDocumentCategorisationState from,
            final IncomingDocumentCategorisationStateTransitionType type,
            final IncomingDocumentCategorisationState to) {

        assertThat(transition.getTransitionType()).isEqualTo(type);
        if(from != null) {
            assertThat(transition.getFromState()).isEqualTo(from);
        } else {
            assertThat(transition.getFromState()).isNull();
        }
        if(to != null) {
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
    DocumentMenu documentMenu;

    @Inject
    TransactionService transactionService;

    @Inject
    PaperclipRepository paperclipRepository;

}
