package org.estatio.module.capex.integtests.document;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.Document_delete;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.app.DocumentMenu;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocumentPresentationSubscriber_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() throws IOException {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
            }
        });

        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        // given
        final String fileName = "3020100123.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(IncomingDocumentPresentationSubscriber_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, MimeTypeData.APPLICATION_PDF.asStr(), pdfBytes);
        wrap(documentMenu).upload(blob);
        transactionService.nextTransaction();

    }

    public static class DeleteDisabledIfIncomingAndCategorised
            extends IncomingDocumentPresentationSubscriber_IntegTest {

        @Test
        public void can_delete_if_not_yet_categorised() throws Exception {

            // given
            List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
            assertThat(incomingDocumentsBefore).isNotEmpty();
            Document document = incomingDocumentsBefore.get(0);
            assertThat(DocumentTypeData.docTypeDataFor(document)).isEqualTo(DocumentTypeData.INCOMING);

            // when
            wrap(mixin(Document_delete.class, document)).$$();
            transactionService.nextTransaction();

            // then
            List<Document> incomingDocumentsAfter = repository.findIncomingDocuments();
            assertThat(incomingDocumentsAfter.size()).isEqualTo(incomingDocumentsBefore.size() - 1);

        }

        @Test
        public void cannot_delete_if_categorised_as_incoming_invoice() throws Exception {
            cannot_delete_if_categorised_as(
                    DocumentTypeData.INCOMING_INVOICE, "Document has already been categorised (as Incoming Invoice)");
        }

        @Test
        public void cannot_delete_if_categorised_as_incoming_order() throws Exception {
            cannot_delete_if_categorised_as(
                    DocumentTypeData.INCOMING_ORDER, "Document has already been categorised (as Incoming Order)");
        }

        private void cannot_delete_if_categorised_as(
                final DocumentTypeData documentTypeData,
                final String expectedMessage) {
            // given
            List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
            assertThat(incomingDocumentsBefore).isNotEmpty();
            Document document = incomingDocumentsBefore.get(0);

            document.setType(documentTypeData.findUsing(documentTypeRepository));

            // expect
            expectedExceptions.expectMessage(expectedMessage);

            // when
            wrap(mixin(Document_delete.class, document)).$$();
        }
    }


    @Inject
    IncomingDocumentRepository repository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentMenu documentMenu;

    @Inject
    TransactionService transactionService;

}
