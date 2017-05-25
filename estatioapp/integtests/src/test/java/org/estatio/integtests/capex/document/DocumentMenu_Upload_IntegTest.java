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

import org.estatio.app.menus.documents.DocumentMenu;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void happyCase() throws Exception {

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
        List<Document> incomingDocumentsAfter = repository.findIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);

        final Document document = incomingDocumentsAfter.get(0);
        final Blob documentBlob = document.getBlob();

        assertThat(documentBlob.getName()).isEqualTo(blob.getName());
        assertThat(documentBlob.getMimeType().getBaseType()).isEqualTo(blob.getMimeType().getBaseType());
        assertThat(documentBlob.getBytes()).isEqualTo(blob.getBytes());
    }

    @Inject
    IncomingDocumentRepository repository;

    @Inject
    DocumentMenu documentMenu;

    @Inject
    TransactionService transactionService;

}
