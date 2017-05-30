package org.estatio.integtests.capex.document;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.DocumentMenu;
import org.estatio.capex.dom.documents.HasDocument_categoriseAsInvoice;
import org.estatio.capex.dom.documents.HasDocument_resetCategorisation;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.capex.TickingFixtureClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.NEW;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CLASSIFY_AS_INVOICE_OR_ORDER;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.INSTANTIATE;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.RESET;

public class IncomingDocumentCategorisationStateSubscriber_IntegTest extends EstatioIntegrationTest {

    @Inject
    FixtureScripts fixtureScripts;


    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    EventBusService eventBusService;

    @Before
    public void setupData() throws IOException {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PropertyForOxfGb());
            }
        });

        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        final String fileName = "1020100123.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(IncomingDocumentCategorisationStateSubscriber_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);
        wrap(documentMenu).upload(blob);
        transactionService.nextTransaction();


        TickingFixtureClock.replaceExisting();
    }

    @After
    public void tearDown() {
        TickingFixtureClock.reinstateExisting();
    }

    @Test
    public void scenario() throws Exception {

        // given
        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).hasSize(1);

        final Document document = incomingDocumentsBefore.get(0);

        List<IncomingDocumentCategorisationStateTransition> transitions =
                stateTransitionRepository.findByDomainObject(document);
        assertThat(transitions).hasSize(2);
        assertTransition(transitions.get(0),
                NEW, CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY, null);
        assertTransition(transitions.get(1),
                null, INSTANTIATE, NEW);

        assertState(document, NEW);

        // when
        final IncomingDocumentViewModel vm = new IncomingDocumentViewModel(document);
        wrap(mixin(HasDocument_categoriseAsInvoice.class, vm)).act(null, true);
//        final HasDocument_categoriseAbstract.DomainEvent categoriseEv =
//                new HasDocument_categoriseAbstract.DomainEvent();
//        categoriseEv.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
//        categoriseEv.setMixedIn(new IncomingDocumentViewModel(document));
//        eventBusService.post(categoriseEv);
//        transactionService.nextTransaction();

        // then
        transitions =
                stateTransitionRepository.findByDomainObject(document);
        assertThat(transitions).hasSize(3);
        assertTransition(transitions.get(0),
                CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY, CLASSIFY_AS_INVOICE_OR_ORDER, null);
        assertTransition(transitions.get(1),
                NEW, CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY, CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);
        assertTransition(transitions.get(2),
                null, INSTANTIATE, NEW);

        assertState(document, CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);

        /*
        NOT TESTING THIS HERE...

        because firing the IncomingInvoiceViewmodel_saveInvoice.DomainEvent
        causes the flow for incoming invoice approval o kick in.
        We don't have an easy way of stubbing out that subscriber (would need to mess with modules and appmanifest to
        somehow remove it/exclude it.
        In any case, the tests for incoming invoice approval do also check that the state of the upstream document
        is modified, so no need to test twice.


        // and when
        final IncomingInvoiceViewmodel_saveInvoice.DomainEvent saveEv =
                new IncomingInvoiceViewmodel_saveInvoice.DomainEvent();

        final FixedAsset fixedAsset = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        saveEv.setMixedIn(new IncomingInvoiceViewModel(document, fixedAsset));
        saveEv.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
        eventBusService.post(saveEv);

        // then
        transitions =
                stateTransitionRepository.findByDomainObject(document);
        assertThat(transitions).hasSize(3);
        assertTransition(transitions.get(0),
                CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY, CLASSIFY_AS_INVOICE_OR_ORDER, CLASSIFIED_AS_INVOICE_OR_ORDER);
        assertTransition(transitions.get(1),
                NEW, CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY, CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);
        assertTransition(transitions.get(2),
                null, INSTANTIATE, NEW);

        assertState(document, CLASSIFIED_AS_INVOICE_OR_ORDER);
         */

        // when
        final IncomingInvoiceViewModel vm2 = new IncomingInvoiceViewModel(document);
        wrap(mixin(HasDocument_resetCategorisation.class, vm2)).act(null);
        transactionService.nextTransaction();

//        final HasDocument_resetCategorisation.DomainEvent resetEv =
//                new HasDocument_resetCategorisation.DomainEvent();
//
//        resetEv.setMixedIn(new IncomingInvoiceViewModel(document));
//        resetEv.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
//        eventBusService.post(resetEv);

        // then
        transitions =
                stateTransitionRepository.findByDomainObject(document);
        assertThat(transitions).hasSize(4);
        assertTransition(transitions.get(0),
                NEW, CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY, null);
        assertTransition(transitions.get(1),
                CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY, RESET, NEW);
        assertTransition(transitions.get(2),
                NEW, CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY, CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);
        assertTransition(transitions.get(3),
                null, INSTANTIATE, NEW);


        assertState(document, NEW);
    }


    @Inject
    PropertyRepository propertyRepository;

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


    private void assertState(final Document document, final IncomingDocumentCategorisationState expectedState) {
        final IncomingDocumentCategorisationState currentState =
                stateTransitionService.currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);

        assertThat(currentState).isEqualTo(expectedState);
    }

    @Inject
    IncomingDocumentRepository repository;

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository stateTransitionRepository;

    @Inject
    DocumentMenu documentMenu;

    @Inject
    TransactionService transactionService;

}
