package org.estatio.module.capex.integtests.document;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.assetfinancial.fixtures.bankaccount.personas.BankAccountAndFaFaForHelloWorldNl;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.fixtures.document.personas.IncomingPdfFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForTopModelGb;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocumentRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject IncomingDocumentRepository incomingDocumentRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new OrganisationForTopModelGb());
                executionContext.executeChild(this, new OrganisationForHelloWorldGb());
                executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
                executionContext.executeChild(this, new BankAccountAndFaFaForHelloWorldNl());

                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());

                executionContext.executeChild(this, new IncomingPdfFixture());
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
}
