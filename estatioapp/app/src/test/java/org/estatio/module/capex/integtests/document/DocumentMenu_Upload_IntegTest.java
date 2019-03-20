package org.estatio.module.capex.integtests.document;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDOHelper;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.base.dom.MimeTypes;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.app.DocumentMenu;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.NEW;
import static org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CATEGORISE;
import static org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.INSTANTIATE;

public class DocumentMenu_Upload_IntegTest extends CapexModuleIntegTestAbstract {

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
    public void happy_case() throws Exception {

        // given
        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        // when
        final String fileName = "3020100123.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(DocumentMenu_Upload_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, MimeTypes.APPLICATION_PDF, pdfBytes);
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        final Document uploadedDocument1 = sudoService
                .sudo(Person_enum.DanielOfficeAdministratorFr.getRef().toLowerCase(), () ->
                        wrap(documentMenu).upload(blob));
        transactionService.nextTransaction();

        // then
        List<Document> incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);
        final Document document = incomingDocumentsAfter.get(0);
        assertThat(document).isSameAs(uploadedDocument1);

        final Blob documentBlob = uploadedDocument1.getBlob();

        assertThat(ApplicationTenancy_enum.FrBe.getPath()).isEqualTo("/FRA;/BEL");
        assertThat(uploadedDocument1.getAtPath()).isEqualTo("/FRA");
        assertThat(documentBlob.getName()).isEqualTo(blob.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
        assertThat(JDOHelper.getVersion(uploadedDocument1)).isEqualTo(1L);
        assertThat(paperclipRepository.findByDocument(uploadedDocument1).size()).isEqualTo(0);

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
                Resources.getResource(DocumentMenu_Upload_IntegTest.class, fileName2));
        final Blob similarNamedBlob = new Blob(fileName, MimeTypes.APPLICATION_PDF, pdfBytes2);
        final Document uploadedDocument2 = wrap(documentMenu).upload(similarNamedBlob);
        transactionService.nextTransaction();


        // then
        assertThat(uploadedDocument1).isNotSameAs(uploadedDocument2);

        incomingDocumentsAfter = repository.findAllIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(2);
        assertThat(incomingDocumentsAfter).contains(uploadedDocument1, uploadedDocument2);

        assertThat(JDOHelper.getVersion(uploadedDocument2)).isEqualTo(1L);
        assertThat(uploadedDocument2.getName()).isEqualTo(fileName);
        assertThat(uploadedDocument2.getBlobBytes()).isEqualTo(similarNamedBlob.getBytes());

        final List<Paperclip> paperclipByAttachedTo = paperclipRepository.findByAttachedTo(uploadedDocument2);
        assertThat(paperclipByAttachedTo.size()).isEqualTo(1);
        final List<Paperclip> paperclipByDocument = paperclipRepository.findByDocument(uploadedDocument1);
        assertThat(paperclipByDocument.size()).isEqualTo(1);
        assertThat(paperclipByDocument.get(0)).isSameAs(paperclipByAttachedTo.get(0));
        assertThat(paperclipRepository.findByAttachedTo(uploadedDocument1)).isEmpty();

        assertThat(JDOHelper.getVersion(uploadedDocument1)).isEqualTo(2L);
        assertThat(uploadedDocument1.getName()).startsWith("arch");
        assertThat(uploadedDocument1.getName()).endsWith(fileName);
        assertThat(uploadedDocument1.getBlobBytes()).isEqualTo(documentBlob.getBytes());

        // and when since ECP-676 atPath derived from filename for France and Belgium
        final String belgianBarCode = "6010012345.pdf";
        final Blob belgianBlob = new Blob(belgianBarCode, MimeTypes.APPLICATION_PDF, pdfBytes2);
        Document belgianDocument = wrap(documentMenu).upload(belgianBlob);

        // then
        assertThat(belgianDocument.getAtPath()).isEqualTo("/BEL");

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
    DocumentMenu documentMenu;

    @Inject
    TransactionService transactionService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

}
