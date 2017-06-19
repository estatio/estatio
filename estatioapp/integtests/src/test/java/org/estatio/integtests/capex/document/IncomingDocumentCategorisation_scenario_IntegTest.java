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
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.DocumentMenu;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.documents.categorisation.document.IncomingDocViewModel_resetCategorisation;
import org.estatio.capex.dom.documents.categorisation.invoice.IncomingDocAsInvoiceViewModel;
import org.estatio.capex.dom.documents.categorisation.tasks.Task_categoriseAsInvoice;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.party.PersonForDylanClaytonGb;
import org.estatio.fixture.party.PersonForEmmaFarmerGb;
import org.estatio.fixture.party.PersonForFaithConwayGb;
import org.estatio.fixture.party.PersonForJonathanRiceGb;
import org.estatio.fixture.party.PersonForOscarPritchardGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.capex.TickingFixtureClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState.NEW;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CLASSIFY_AS_INVOICE_OR_ORDER;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.INSTANTIATE;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.RESET;

public class IncomingDocumentCategorisation_scenario_IntegTest extends EstatioIntegrationTest {

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

                executionContext.executeChild(this, new PersonForDylanClaytonGb()); // gb mailroom
                executionContext.executeChild(this, new PersonForJonathanRiceGb());  // gb property mgr for OXF
                executionContext.executeChild(this, new PersonForFaithConwayGb());  // gb country administrator
                executionContext.executeChild(this, new PersonForOscarPritchardGb());  // gb country director
                executionContext.executeChild(this, new PersonForEmmaFarmerGb());   // gb treasurer

            }
        });

        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).isEmpty();

        final String fileName = "1020100123.pdf";
        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(IncomingDocumentCategorisation_scenario_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);

        sudoService.sudo("estatio-user-gb", new Runnable() {
            @Override public void run() {
                wrap(documentMenu).upload(blob);
            }
        });
        transactionService.nextTransaction();


        TickingFixtureClock.replaceExisting();
    }

    @Inject
    SudoService sudoService;

    @After
    public void tearDown() {
        TickingFixtureClock.reinstateExisting();
    }

    @Test
    public void scenario() throws Exception {

        // given
        final Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
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

        Task task = transitions.get(0).getTask();
        assertThat(task.getAssignedTo().getKey()).isEqualTo(PartyRoleTypeEnum.MAIL_ROOM.getKey());
        assertThat(task.getPersonAssignedTo()).isNotNull();
        assertThat(task.getPersonAssignedTo().getReference()).isEqualTo(PersonForDylanClaytonGb.REF);

        // when
        wrap(mixin(Task_categoriseAsInvoice.class, task)).act(property, null, true);

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

        // then assigned now to Jonathan
        task = transitions.get(0).getTask();
        assertThat(task.getAssignedTo().getKey()).isEqualTo(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.getKey());
        assertThat(task.getPersonAssignedTo()).isNotNull();
        assertThat(task.getPersonAssignedTo().getReference()).isEqualTo(PersonForJonathanRiceGb.REF);

        /*
        NOT TESTING THIS HERE...

        because firing the IncomingDocAsInvoiceViewmodel_saveInvoice.DomainEvent
        causes the flow for incoming invoice approval o kick in.
        We don't have an easy way of stubbing out that subscriber (would need to mess with modules and appmanifest to
        somehow remove it/exclude it.
        In any case, the tests for incoming invoice approval do also check that the state of the upstream document
        is modified, so no need to test twice.


        // and when
        final IncomingDocAsInvoiceViewmodel_saveInvoice.DomainEvent saveEv =
                new IncomingDocAsInvoiceViewmodel_saveInvoice.DomainEvent();

        final FixedAsset fixedAsset = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        saveEv.setMixedIn(new IncomingDocAsInvoiceViewModel(document, fixedAsset));
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
        final IncomingDocAsInvoiceViewModel vm2 = new IncomingDocAsInvoiceViewModel(document);
        wrap(mixin(IncomingDocViewModel_resetCategorisation.class, vm2)).act(null);
        transactionService.nextTransaction();

//        final IncomingDocViewModel_resetCategorisation.DomainEvent resetEv =
//                new IncomingDocViewModel_resetCategorisation.DomainEvent();
//
//        resetEv.setMixedIn(new IncomingDocAsInvoiceViewModel(document));
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

        // then back to Dylan
        task = transitions.get(0).getTask();
        assertThat(task.getAssignedTo().getKey()).isEqualTo(PartyRoleTypeEnum.MAIL_ROOM.getKey());
        assertThat(task.getPersonAssignedTo()).isNotNull();
        assertThat(task.getPersonAssignedTo().getReference()).isEqualTo(PersonForDylanClaytonGb.REF);


        assertState(document, NEW);
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


    private void assertState(final Document document, final IncomingDocumentCategorisationState expectedState) {
        final IncomingDocumentCategorisationState currentState =
                stateTransitionService.currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);

        assertThat(currentState).isEqualTo(expectedState);
    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    IncomingDocumentRepository repository;

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository stateTransitionRepository;

    @Inject
    DocumentMenu documentMenu;

    @Inject
    TransactionService transactionService;

}
