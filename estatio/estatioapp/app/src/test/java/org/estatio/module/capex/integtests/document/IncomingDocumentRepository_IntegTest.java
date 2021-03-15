package org.estatio.module.capex.integtests.document;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.assetfinancial.fixtures.enums.BankAccountFaFa_enum;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocumentRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject IncomingDocumentRepository incomingDocumentRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
                executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldGb.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, BankAccount_enum.HelloWorldNl.builder());
                executionContext.executeChild(this, BankAccountFaFa_enum.HelloWorldNl.builder());

                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());

                executionContext.executeChild(this, IncomingPdf_enum.FakeOrder1.builder());
                executionContext.executeChild(this, IncomingPdf_enum.FakeInvoice1.builder());
            }
        });
    }

    public static class MatchIncomingDocumentByName extends IncomingDocumentRepository_IntegTest {
        @Test
        public void when_document_name_is_equal_to_search_phrase_return_documents() {
            // given
            String searchPhrase = "fakeOrder1.pdf";

            // when
            List<Document> matchedDocuments = incomingDocumentRepository.matchAllIncomingDocumentsByName(searchPhrase);

            // then
            assertThat(matchedDocuments).isNotEmpty();
        }

        @Test
        public void when_document_name_contains_case_insensitive_search_phrase_return_documents() {
            // given
            String searchPhrase = "keorde";

            // when
            List<Document> matchedDocuments = incomingDocumentRepository.matchAllIncomingDocumentsByName(searchPhrase);

            // then
            assertThat(matchedDocuments).isNotEmpty();
        }

        @Test
        public void when_document_name_does_not_contain_search_phrase_return_no_documents() {
            // given
            String searchPhrase = "snakeOrder1.jpg";

            // when
            List<Document> matchedDocuments = incomingDocumentRepository.matchAllIncomingDocumentsByName(searchPhrase);

            // then
            assertThat(matchedDocuments).isEmpty();
        }
    }

    public static class UpsertAndArrchive extends IncomingDocumentRepository_IntegTest {

    }
}
