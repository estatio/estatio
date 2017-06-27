package org.estatio.integtests.capex.document;

import org.estatio.integtests.EstatioIntegrationTest;

public class IncomingDocumentCategorisation_scenario_IntegTest extends EstatioIntegrationTest {

//    @Inject
//    FixtureScripts fixtureScripts;
//
//
//    @Inject
//    StateTransitionService stateTransitionService;
//
//    @Inject
//    EventBusService eventBusService;
//
//    @Before
//    public void setupData() throws IOException {
//        runFixtureScript(new FixtureScript() {
//            @Override
//            protected void execute(final ExecutionContext executionContext) {
//                executionContext.executeChild(this, new EstatioBaseLineFixture());
//                executionContext.executeChild(this, new PropertyForOxfGb());
//
//                executionContext.executeChild(this, new PersonForDylanClaytonGb()); // gb mailroom
//                executionContext.executeChild(this, new PersonForJonathanRiceGb());  // gb property mgr for OXF
//                executionContext.executeChild(this, new PersonForFaithConwayGb());  // gb country administrator
//                executionContext.executeChild(this, new PersonForOscarPritchardGb());  // gb country director
//                executionContext.executeChild(this, new PersonForEmmaFarmerGb());   // gb treasurer
//
//            }
//        });
//
//        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
//        assertThat(incomingDocumentsBefore).isEmpty();
//
//        final String fileName = "1020100123.pdf";
//        final byte[] pdfBytes = Resources.toByteArray(
//                Resources.getResource(IncomingDocumentCategorisation_scenario_IntegTest.class, fileName));
//        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);
//
//        sudoService.sudo("estatio-user-gb", new Runnable() {
//            @Override public void run() {
//                wrap(documentMenu).upload(blob);
//            }
//        });
//        transactionService.nextTransaction();
//
//
//        TickingFixtureClock.replaceExisting();
//    }
//
//    @Inject
//    SudoService sudoService;
//
//    @After
//    public void tearDown() {
//        TickingFixtureClock.reinstateExisting();
//    }
//
//    @Test
//    public void scenario() throws Exception {
//
//        // given
//        final Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
//        List<Document> incomingDocumentsBefore = repository.findIncomingDocuments();
//        assertThat(incomingDocumentsBefore).hasSize(1);
//
//        final Document document = incomingDocumentsBefore.get(0);
//
//        List<IncomingDocumentCategorisationStateTransition> transitions =
//                stateTransitionRepository.findByDomainObject(document);
//        assertThat(transitions).hasSize(2);
//        assertTransition(transitions.get(0),
//                NEW, CATEGORISE, null);
//        assertTransition(transitions.get(1),
//                null, INSTANTIATE, NEW);
//
//        assertState(document, NEW);
//
//        Task task = transitions.get(0).getTask();
//        assertThat(task.getAssignedTo().getKey()).isEqualTo(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR.getKey());
//        assertThat(task.getPersonAssignedTo()).isNotNull();
//        assertThat(task.getPersonAssignedTo().getReference()).isEqualTo(PersonForDylanClaytonGb.REF);
//
//        // when
//        wrap(mixin(Task_categoriseAsOtherInvoice.class, task)).act(documentTypeData, DocumentTypeData.INCOMING_INVOICE, property, null, true);
//
//        // then
//        transitions =
//                stateTransitionRepository.findByDomainObject(document);
//        assertThat(transitions).hasSize(3);
//        assertTransition(transitions.get(0),
//                CATEGORISED, PROCESS_INVOICE, null);
//        assertTransition(transitions.get(1),
//                NEW, CATEGORISE, CATEGORISED);
//        assertTransition(transitions.get(2),
//                null, INSTANTIATE, NEW);
//
//        assertState(document, CATEGORISED);
//
//        // then assigned now to Jonathan
//        task = transitions.get(0).getTask();
//        assertThat(task.getAssignedTo().getKey()).isEqualTo(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.getKey());
//        assertThat(task.getPersonAssignedTo()).isNotNull();
//        assertThat(task.getPersonAssignedTo().getReference()).isEqualTo(PersonForJonathanRiceGb.REF);
//
//
//
//        // when
//        final IncomingDocAsInvoiceViewModel vm2 = new IncomingDocAsInvoiceViewModel(document);
//        wrap(mixin(IncomingDocViewModel_resetCategorisation.class, vm2)).act(null);
//        transactionService.nextTransaction();
//
//
//        // then
//        transitions =
//                stateTransitionRepository.findByDomainObject(document);
//        assertThat(transitions).hasSize(4);
//        assertTransition(transitions.get(0),
//                NEW, CATEGORISE, null);
//        assertTransition(transitions.get(1),
//                CATEGORISED, RESET, NEW);
//        assertTransition(transitions.get(2),
//                NEW, CATEGORISE, CATEGORISED);
//        assertTransition(transitions.get(3),
//                null, INSTANTIATE, NEW);
//
//        // then back to Dylan
//        task = transitions.get(0).getTask();
//        assertThat(task.getAssignedTo().getKey()).isEqualTo(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR.getKey());
//        assertThat(task.getPersonAssignedTo()).isNotNull();
//        assertThat(task.getPersonAssignedTo().getReference()).isEqualTo(PersonForDylanClaytonGb.REF);
//
//
//        assertState(document, NEW);
//    }
//
//
//    static void assertTransition(
//            final IncomingDocumentCategorisationStateTransition transition,
//            final IncomingDocumentCategorisationState from,
//            final IncomingDocumentCategorisationStateTransitionType type,
//            final IncomingDocumentCategorisationState to) {
//
//        assertThat(transition.getTransitionType()).isEqualTo(type);
//        if(from != null) {
//            assertThat(transition.getFromState()).isEqualTo(from);
//        } else {
//            assertThat(transition.getFromState()).isNull();
//        }
//        if(to != null) {
//            assertThat(transition.getToState()).isEqualTo(to);
//        } else {
//            assertThat(transition.getToState()).isNull();
//        }
//    }
//
//
//    private void assertState(final Document document, final IncomingDocumentCategorisationState expectedState) {
//        final IncomingDocumentCategorisationState currentState =
//                stateTransitionService.currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);
//
//        assertThat(currentState).isEqualTo(expectedState);
//    }
//
//    @Inject
//    PropertyRepository propertyRepository;
//
//    @Inject
//    IncomingDocumentRepository repository;
//
//    @Inject
//    IncomingDocumentCategorisationStateTransition.Repository stateTransitionRepository;
//
//    @Inject
//    DocumentMenu documentMenu;
//
//    @Inject
//    TransactionService transactionService;

}
