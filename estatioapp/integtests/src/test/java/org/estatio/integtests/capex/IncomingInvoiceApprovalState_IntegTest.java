package org.estatio.integtests.capex;

import org.estatio.integtests.EstatioIntegrationTest;

public class IncomingInvoiceApprovalState_IntegTest extends EstatioIntegrationTest {

//    Property propertyForOxf;
//    Party buyer;
//    Party seller;
//
//    Country greatBritain;
//    Charge charge_for_works;
//
//    IncomingInvoice incomingInvoice;
//    Document invoiceDoc;
//
//    BankAccount bankAccount;
//    Project project;
//
//    @Before
//    public void setupData() {
//
//        runFixtureScript(new FixtureScript() {
//            @Override
//            protected void execute(final FixtureScript.ExecutionContext executionContext) {
//                executionContext.executeChild(this, new EstatioBaseLineFixture());
//                executionContext.executeChild(this, new PropertyForOxfGb());
//                executionContext.executeChild(this, new BankAccountForTopModelGb());
//                executionContext.executeChild(this, new IncomingPdfFixture());
//            }
//        });
//
//        TickingFixtureClock.replaceExisting();
//    }
//
//    @Before
//    public void setUp() {
//        propertyForOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
//
//        buyer = partyRepository.findPartyByReference(PropertyForOxfGb.PARTY_REF_OWNER);
//        seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
//
//        greatBritain = countryRepository.findCountry(CountriesRefData.GBR);
//        charge_for_works = chargeRepository.findByReference("WORKS");
//
//        project = projectRepository.create("My-proj", "My project", null, null, propertyForOxf.getAtPath(), null);
//
//        bankAccount = bankAccountRepository.findBankAccountByReference(seller, BankAccountForTopModelGb.REF);
//
//        final LocalDate invoiceDate = clockService.now().minusDays(30);
//        final LocalDate dueDate = clockService.now().minusDays(10);
//        final LocalDate dateReceived = clockService.now();
//
//        incomingInvoice = incomingInvoiceRepository
//                .create(IncomingInvoice.Type.CAPEX, "123", propertyForOxf.getAtPath(), buyer, seller,
//                        invoiceDate,
//                        dueDate, PaymentMethod.CHEQUE, InvoiceStatus.NEW, dateReceived, null);
//
//        final List<Document> incomingDocuments = incomingDocumentRepository.findIncomingDocuments();
//        invoiceDoc = incomingDocuments.get(0);
//
//        paperclipRepository.attach(invoiceDoc, null, incomingInvoice);
//
//        assertThat(incomingInvoice).isNotNull();
//        assertState(bankAccount, NOT_VERIFIED);
//
//    }
//
//    @After
//    public void tearDown() {
//        TickingFixtureClock.reinstateExisting();
//    }
//
//    private void postProcessInvoice(){
//        // TODO: I think that this event is no longer needed, as we now listen at the Invoice persisted lifecycle event
//        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev = new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(
//                invoiceDoc, null, IncomingDocumentCategorisationStateTransitionType.PROCESS_INVOICE);
//        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
//        eventBusService.post(ev);
//    }
//
//    @Test
//    public void when_document_associated_with_entity_then_invoice_approval_state_to_new() {
//
//        // given
//        assertThat(incomingInvoice.getBankAccount()).isNull();
//
//        // when
//        postProcessInvoice();
//
//        // then
//        assertState(incomingInvoice, NEW);
//
//        final List<IncomingInvoiceApprovalStateTransition> transitions = findTransitions(this.incomingInvoice);
//        assertThat(transitions.size()).isEqualTo(2);
//        assertTransition(transitions.get(0), NEW, COMPLETE, null);
//        assertTransition(transitions.get(1), null, INSTANTIATE, NEW);
//
//    }
//
//    @Test
//    public void when_document_has_classification_complete_then_invoice_approval_state_to_classified() {
//
//        // given
//        assertThat(incomingInvoice.getBankAccount()).isNull();
//        postProcessInvoice();
//        assertState(incomingInvoice, NEW);
//
//        // when
//        wrap(mixin(IncomingInvoice._changeBankAccount.class, incomingInvoice)).act(bankAccount, null);
//        transactionService.nextTransaction();
//
//        // then
//        assertState(incomingInvoice, COMPLETED);
//
//        final List<IncomingInvoiceApprovalStateTransition> transitions = findTransitions(this.incomingInvoice);
//        assertThat(transitions.size()).isEqualTo(3);
//        assertTransition(transitions.get(0), COMPLETED, APPROVE_AS_ASSET_MANAGER, null);
//        assertTransition(transitions.get(1), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitions.get(2), null, INSTANTIATE, NEW);
//
//    }
//
//    @Test
//    public void when_document_associated_with_entity_then_invoice_approval_state_to_classified_and_next_is_asset_manager() {
//
//        // given
//        incomingInvoice.setBankAccount(bankAccount);
//
//        // when
//        postProcessInvoice();
//
//        // then
//        assertState(incomingInvoice, COMPLETED);
//
//        final List<IncomingInvoiceApprovalStateTransition> transitions = findTransitions(this.incomingInvoice);
//        assertThat(transitions.size()).isEqualTo(3);
//        assertTransition(transitions.get(0), COMPLETED, APPROVE_AS_ASSET_MANAGER, null);
//        assertTransition(transitions.get(1), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitions.get(2), null, INSTANTIATE, NEW);
//
//    }
//
//    @Test
//    public void complete_and_next_is_project_manager() {
//
//        // given
//        incomingInvoice.setBankAccount(bankAccount);
//
//        // and given also associated with a project
//        givenInvoiceAssociatedWithProject();
//
//        // when
//        postProcessInvoice();
//
//        // then
//        assertState(incomingInvoice, COMPLETED);
//
//        final List<IncomingInvoiceApprovalStateTransition> transitions = findTransitions(this.incomingInvoice);
//        assertThat(transitions.size()).isEqualTo(3);
//        assertTransition(transitions.get(0), COMPLETED, APPROVE_AS_PROJECT_MANAGER, null);
//        assertTransition(transitions.get(1), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitions.get(2), null, INSTANTIATE, NEW);
//
//    }
//
//    @Test
//    public void approve_by_asset_manager() {
//
//        // given
//        incomingInvoice.setBankAccount(bankAccount);
//        postProcessInvoice();
//
//        assertState(incomingInvoice, COMPLETED);
//        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
//        assertThat(transitionsBefore.size()).isEqualTo(3);
//        assertTransition(transitionsBefore.get(0), COMPLETED, APPROVE_AS_ASSET_MANAGER, null);
//        assertTransition(transitionsBefore.get(1), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsBefore.get(2), null, INSTANTIATE, NEW);
//
//
//        // when
//        wrap(mixin(IncomingInvoice_approveAsAssetManager.class, incomingInvoice)).act("looks good to me!");
//        transactionService.nextTransaction();
//
//        // then
//        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
//        assertThat(transitionsAfter.size()).isEqualTo(4);
//        assertTransition(transitionsAfter.get(0), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, null);
//        assertTransition(transitionsAfter.get(1), COMPLETED, APPROVE_AS_ASSET_MANAGER, APPROVED);
//        assertTransition(transitionsAfter.get(2), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsAfter.get(3), null, INSTANTIATE, NEW);
//
//        assertState(incomingInvoice, APPROVED);
//
//    }
//
//    @Test
//    public void approve_by_project_manager() {
//
//        // given also associated with a project
//        givenInvoiceAssociatedWithProject();
//
//        // and given
//        incomingInvoice.setBankAccount(bankAccount);
//        postProcessInvoice();
//
//        assertState(incomingInvoice, COMPLETED);
//        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
//        assertThat(transitionsBefore.size()).isEqualTo(3);
//        assertTransition(transitionsBefore.get(0), COMPLETED, APPROVE_AS_PROJECT_MANAGER, null);
//        assertTransition(transitionsBefore.get(1), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsBefore.get(2), null, INSTANTIATE, NEW);
//
//        // when
//        wrap(mixin(IncomingInvoice_approveAsProjectManager.class, incomingInvoice)).act("looks good to me!");
//        transactionService.nextTransaction();
//
//        // then
//        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
//        assertThat(transitionsAfter.size()).isEqualTo(4);
//        assertTransition(transitionsAfter.get(0), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, null);
//        assertTransition(transitionsAfter.get(1), COMPLETED, APPROVE_AS_PROJECT_MANAGER, APPROVED);
//        assertTransition(transitionsAfter.get(2), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsAfter.get(3), null, INSTANTIATE, NEW);
//
//        assertState(incomingInvoice, APPROVED);
//
//    }
//
//    @Test
//    public void approve_by_country_directory_once_approved_by_project_manager_and_bank_account_not_yet_been_verified() {
//
//        // given associated with a project
//        givenInvoiceAssociatedWithProject();
//
//        // and given
//        incomingInvoice.setBankAccount(bankAccount);
//        postProcessInvoice();
//
//        // and given
//        wrap(mixin(IncomingInvoice_approveAsProjectManager.class, incomingInvoice)).act("looks good to me!");
//
//        assertState(incomingInvoice, APPROVED);
//
//        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
//        assertThat(transitionsBefore.size()).isEqualTo(4);
//        assertTransition(transitionsBefore.get(0), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, null);
//        assertTransition(transitionsBefore.get(1), COMPLETED, APPROVE_AS_PROJECT_MANAGER, APPROVED);
//        assertTransition(transitionsBefore.get(2), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsBefore.get(3), null, INSTANTIATE, NEW);
//
//        // when
//        wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act("me too!");
//        transactionService.nextTransaction();
//
//        // then
//        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
//        assertThat(transitionsAfter.size()).isEqualTo(5);
//        assertTransition(transitionsAfter.get(0), APPROVED_BY_COUNTRY_DIRECTOR, CONFIRM_BANK_ACCOUNT_VERIFIED, null);
//        assertTransition(transitionsAfter.get(1), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, APPROVED_BY_COUNTRY_DIRECTOR);
//        assertTransition(transitionsAfter.get(2), COMPLETED, APPROVE_AS_PROJECT_MANAGER, APPROVED);
//        assertTransition(transitionsAfter.get(3), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsAfter.get(4), null, INSTANTIATE, NEW);
//
//        assertState(incomingInvoice, APPROVED_BY_COUNTRY_DIRECTOR);
//    }
//
//    @Test
//    public void approve_by_country_directory_once_approved_by_asset_manager_and_bank_account_not_yet_been_verified() {
//
//        // given
//        incomingInvoice.setBankAccount(bankAccount);
//        postProcessInvoice();
//
//        assertState(bankAccount, NOT_VERIFIED);
//
//
//        // and given
//        wrap(mixin(IncomingInvoice_approveAsAssetManager.class, incomingInvoice)).act("looks good to me!");
//        transactionService.nextTransaction();
//
//        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
//        assertThat(transitionsBefore.size()).isEqualTo(4);
//        assertTransition(transitionsBefore.get(0), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, null);
//        assertTransition(transitionsBefore.get(1), COMPLETED, APPROVE_AS_ASSET_MANAGER, APPROVED);
//        assertTransition(transitionsBefore.get(2), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsBefore.get(3), null, INSTANTIATE, NEW);
//
//        assertState(incomingInvoice, APPROVED);
//
//        // when
//        wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act("me too!");
//        transactionService.nextTransaction();
//
//        // then
//        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
//        assertThat(transitionsAfter.size()).isEqualTo(5);
//        assertTransition(transitionsAfter.get(0), APPROVED_BY_COUNTRY_DIRECTOR, CONFIRM_BANK_ACCOUNT_VERIFIED, null);
//        assertTransition(transitionsAfter.get(1), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, APPROVED_BY_COUNTRY_DIRECTOR);
//        assertTransition(transitionsAfter.get(2), COMPLETED, APPROVE_AS_ASSET_MANAGER, APPROVED);
//        assertTransition(transitionsAfter.get(3), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsAfter.get(4), null, INSTANTIATE, NEW);
//
//        assertState(incomingInvoice, APPROVED_BY_COUNTRY_DIRECTOR);
//    }
//
//    @Test
//    public void approve_by_country_directory_once_approved_by_asset_manager_and_bank_account_is_verified() {
//
//        // given
//        incomingInvoice.setBankAccount(bankAccount);
//        postProcessInvoice();
//
//        // and given the bank account is verified
//        wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);
//        transactionService.nextTransaction();
//
//        assertState(bankAccount, VERIFIED);
//
//        // and given
//        wrap(mixin(IncomingInvoice_approveAsAssetManager.class, incomingInvoice)).act("looks good to me!");
//        transactionService.nextTransaction();
//
//        assertState(incomingInvoice, APPROVED);
//
//        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
//        assertThat(transitionsBefore.size()).isEqualTo(4);
//        assertTransition(transitionsBefore.get(0), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, null);
//        assertTransition(transitionsBefore.get(1), COMPLETED, APPROVE_AS_ASSET_MANAGER, APPROVED);
//        assertTransition(transitionsBefore.get(2), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsBefore.get(3), null, INSTANTIATE, NEW);
//
//        // when
//        wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act("me too!");
//        transactionService.nextTransaction();
//
//        // then
//        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
//        assertThat(transitionsAfter.size()).isEqualTo(5);
//        assertTransition(transitionsAfter.get(0), APPROVED_BY_COUNTRY_DIRECTOR, CONFIRM_BANK_ACCOUNT_VERIFIED, PAYABLE);
//        assertTransition(transitionsAfter.get(1), APPROVED, APPROVE_AS_COUNTRY_DIRECTOR, APPROVED_BY_COUNTRY_DIRECTOR);
//        assertTransition(transitionsAfter.get(2), COMPLETED, APPROVE_AS_ASSET_MANAGER, APPROVED);
//        assertTransition(transitionsAfter.get(3), NEW, COMPLETE, COMPLETED);
//        assertTransition(transitionsAfter.get(4), null, INSTANTIATE, NEW);
//
//        assertState(incomingInvoice, PAYABLE);
//
//    }
//
//
//    private void givenInvoiceAssociatedWithProject() {
//        final LocalDate startDate = clockService.now().plusDays(20);
//        final LocalDate endDate = clockService.now().plusDays(30);
//
//        incomingInvoice.addItem(
//                charge_for_works, "some invoice item description",
//                new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("120.00"),
//                charge_for_works.getTax(),
//                incomingInvoice.getDueDate(), startDate, endDate,
//                propertyForOxf, project, null);
//    }
//
//
//    static void assertTransition(
//            final IncomingInvoiceApprovalStateTransition transition,
//            final IncomingInvoiceApprovalState from,
//            final IncomingInvoiceApprovalStateTransitionType type,
//            final IncomingInvoiceApprovalState to) {
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
//    void assertState(final IncomingInvoice incomingInvoice, final IncomingInvoiceApprovalState expected) {
//        assertThat(wrap(mixin(IncomingInvoice_approvalState.class, incomingInvoice)).prop()).isEqualTo(
//                expected);
//    }
//
//    void assertState(final BankAccount bankAccount, final BankAccountVerificationState expected) {
//        assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
//                expected);
//    }
//
//    protected List<IncomingInvoiceApprovalStateTransition> findTransitions(final IncomingInvoice incomingInvoice) {
//        return incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
//    }
//
//    @Inject
//    ProjectRepository projectRepository;
//
//    @Inject
//    EventBusService eventBusService;
//
//    @Inject
//    IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;
//
//    @Inject
//    IncomingDocumentRepository incomingDocumentRepository;
//
//    @Inject
//    IncomingInvoiceRepository incomingInvoiceRepository;
//
//    @Inject
//    PropertyRepository propertyRepository;
//
//    @Inject
//    PartyRepository partyRepository;
//
//    @Inject
//    CountryRepository countryRepository;
//
//    @Inject
//    ChargeRepository chargeRepository;
//
//    @Inject
//    BankAccountRepository bankAccountRepository;
//
//    @Inject
//    PaperclipRepository paperclipRepository;
//
//    @Inject
//    ClockService clockService;

}

