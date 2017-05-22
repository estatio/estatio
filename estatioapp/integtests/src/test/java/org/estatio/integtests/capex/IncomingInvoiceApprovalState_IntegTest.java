package org.estatio.integtests.capex;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixture.CountriesRefData;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccount_verificationState;
import org.estatio.capex.dom.bankaccount.verification.transitions.BankAccount_verify;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.invoice.approval.IncomingInvoice_approvalState;
import org.estatio.capex.dom.invoice.approval.transitions.IncomingInvoice_approveAsAssetManager;
import org.estatio.capex.dom.invoice.approval.transitions.IncomingInvoice_approveAsCountryDirector;
import org.estatio.capex.dom.invoice.approval.transitions.IncomingInvoice_approveAsProjectManager;
import org.estatio.capex.dom.invoice.payment.PaymentRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.documents.incoming.IncomingPdfFixture;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.PENDING;
import static org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState.VERIFIED;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.NEW;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.PAYABLE;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_ASSET_MANAGER;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_PROJECT_MANAGER;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.CHECK_BANK_ACCOUNT;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.INSTANTIATE;

public class IncomingInvoiceApprovalState_IntegTest extends EstatioIntegrationTest {

    Property propertyForOxf;
    Party buyer;
    Party seller;

    Country greatBritain;
    Charge charge_for_works;

    IncomingInvoice incomingInvoice;
    Document invoiceDoc;

    BankAccount bankAccount;
    Project project;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PropertyForOxfGb());
                executionContext.executeChild(this, new BankAccountForTopModelGb());
                executionContext.executeChild(this, new IncomingPdfFixture());
            }
        });
    }

    @Before
    public void setUp() {
        propertyForOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

        buyer = partyRepository.findPartyByReference(PropertyForOxfGb.PARTY_REF_OWNER);
        seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);

        greatBritain = countryRepository.findCountry(CountriesRefData.GBR);
        charge_for_works = chargeRepository.findByReference("WORKS");

        project = projectRepository.create("My-proj", "My project", null, null, propertyForOxf.getAtPath(), null);

        bankAccount = bankAccountRepository.findBankAccountByReference(seller, BankAccountForTopModelGb.REF);

        final LocalDate invoiceDate = clockService.now().minusDays(30);
        final LocalDate dueDate = clockService.now().minusDays(10);
        final LocalDate dateReceived = clockService.now();

        incomingInvoice = incomingInvoiceRepository
                .create("123", propertyForOxf.getAtPath(), buyer, seller,
                        invoiceDate,
                        dueDate, PaymentMethod.CHEQUE, InvoiceStatus.NEW, dateReceived, bankAccount);

        final List<Document> incomingDocuments = incomingDocumentRepository.findIncomingDocuments();
        invoiceDoc = incomingDocuments.get(0);

        paperclipRepository.attach(invoiceDoc, null, incomingInvoice);

        assertThat(incomingInvoice).isNotNull();
        assertState(bankAccount, PENDING);
    }

    @Test
    public void when_document_associated_with_entity_then_invoice_approval_state_to_new_and_next_is_asset_manager() {

        // when
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev = new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(
                invoiceDoc, null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        // then
        assertState(incomingInvoice, NEW);

        final List<IncomingInvoiceApprovalStateTransition> transitions = findTransitions(this.incomingInvoice);
        assertThat(transitions.size()).isEqualTo(2);
        assertTransition(transitions.get(0), NEW, APPROVE_AS_ASSET_MANAGER, null);
        assertTransition(transitions.get(1), null, INSTANTIATE, NEW);

    }

    @Test
    public void when_document_associated_with_entity_then_invoice_approval_state_to_new_and_next_is_project_manager() {

        // given

        // and given also associated with a project
        givenInvoiceAssociatedWithProject();

        // when
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev =
                new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(invoiceDoc,
                        null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        // then
        assertState(incomingInvoice, NEW);

        final List<IncomingInvoiceApprovalStateTransition> transitions = findTransitions(this.incomingInvoice);
        assertThat(transitions.size()).isEqualTo(2);
        assertTransition(transitions.get(0), NEW, APPROVE_AS_PROJECT_MANAGER, null);
        assertTransition(transitions.get(1), null, INSTANTIATE, NEW);

    }

    @Test
    public void approve_by_asset_manager() {

        // given
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev =
                new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(invoiceDoc,
                        null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        assertState(incomingInvoice, NEW);
        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
        assertThat(transitionsBefore.size()).isEqualTo(2);
        assertTransition(transitionsBefore.get(0), NEW, APPROVE_AS_ASSET_MANAGER, null);
        assertTransition(transitionsBefore.get(1), null, INSTANTIATE, NEW);

        // when
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsAssetManager.class, incomingInvoice)).act("looks good to me!");

        // then
        assertState(incomingInvoice, APPROVED_BY_ASSET_MANAGER);

        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
        assertThat(transitionsAfter.size()).isEqualTo(3);
        assertTransition(transitionsAfter.get(0), APPROVED_BY_ASSET_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, null);
        assertTransition(transitionsAfter.get(1), NEW, APPROVE_AS_ASSET_MANAGER, APPROVED_BY_ASSET_MANAGER);
        assertTransition(transitionsAfter.get(2), null, INSTANTIATE, NEW);

    }

    @Test
    public void approve_by_project_manager() {

        // given also associated with a project
        givenInvoiceAssociatedWithProject();

        // and given
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev =
                new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(invoiceDoc,
                        null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        assertState(incomingInvoice, NEW);
        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
        assertThat(transitionsBefore.size()).isEqualTo(2);
        assertTransition(transitionsBefore.get(0), NEW, APPROVE_AS_PROJECT_MANAGER, null);
        assertTransition(transitionsBefore.get(1), null, INSTANTIATE, NEW);

        // when
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsProjectManager.class, incomingInvoice)).act("looks good to me!");

        // then
        assertState(incomingInvoice, APPROVED_BY_PROJECT_MANAGER);

        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
        assertThat(transitionsAfter.size()).isEqualTo(3);
        assertTransition(transitionsAfter.get(0), APPROVED_BY_PROJECT_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, null);
        assertTransition(transitionsAfter.get(1), NEW, APPROVE_AS_PROJECT_MANAGER, APPROVED_BY_PROJECT_MANAGER);
        assertTransition(transitionsAfter.get(2), null, INSTANTIATE, NEW);

    }

    @Test
    public void approve_by_country_directory_once_approved_by_project_manager_and_bank_account_not_yet_been_verified() {

        // given associated with a project
        givenInvoiceAssociatedWithProject();

        // and given
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev =
                new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(invoiceDoc,
                        null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        // and given
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsProjectManager.class, incomingInvoice)).act("looks good to me!");

        assertState(incomingInvoice, APPROVED_BY_PROJECT_MANAGER);

        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
        assertThat(transitionsBefore.size()).isEqualTo(3);
        assertTransition(transitionsBefore.get(0), APPROVED_BY_PROJECT_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, null);
        assertTransition(transitionsBefore.get(1), NEW, APPROVE_AS_PROJECT_MANAGER, APPROVED_BY_PROJECT_MANAGER);
        assertTransition(transitionsBefore.get(2), null, INSTANTIATE, NEW);

        // when
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act("me too!");

        // then
        assertState(incomingInvoice, APPROVED_BY_COUNTRY_DIRECTOR);

        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
        assertThat(transitionsAfter.size()).isEqualTo(4);
        assertTransition(transitionsAfter.get(0), APPROVED_BY_COUNTRY_DIRECTOR, CHECK_BANK_ACCOUNT, null);
        assertTransition(transitionsAfter.get(1), APPROVED_BY_PROJECT_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, APPROVED_BY_COUNTRY_DIRECTOR);
        assertTransition(transitionsAfter.get(2), NEW, APPROVE_AS_PROJECT_MANAGER, APPROVED_BY_PROJECT_MANAGER);
        assertTransition(transitionsAfter.get(3), null, INSTANTIATE, NEW);
    }

    @Test
    public void approve_by_country_directory_once_approved_by_asset_manager_and_bank_account_not_yet_been_verified() {

        // given
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev =
                new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(invoiceDoc,
                        null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        assertState(bankAccount, PENDING);


        // and given
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsAssetManager.class, incomingInvoice)).act("looks good to me!");

        assertState(incomingInvoice, APPROVED_BY_ASSET_MANAGER);

        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
        assertThat(transitionsBefore.size()).isEqualTo(3);
        assertTransition(transitionsBefore.get(0), APPROVED_BY_ASSET_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, null);
        assertTransition(transitionsBefore.get(1), NEW, APPROVE_AS_ASSET_MANAGER, APPROVED_BY_ASSET_MANAGER);
        assertTransition(transitionsBefore.get(2), null, INSTANTIATE, NEW);

        // when
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act("me too!");

        // then
        assertState(incomingInvoice, APPROVED_BY_COUNTRY_DIRECTOR);

        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
        assertThat(transitionsAfter.size()).isEqualTo(4);
        assertTransition(transitionsAfter.get(0), APPROVED_BY_COUNTRY_DIRECTOR, CHECK_BANK_ACCOUNT, null);
        assertTransition(transitionsAfter.get(1), APPROVED_BY_ASSET_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, APPROVED_BY_COUNTRY_DIRECTOR);
        assertTransition(transitionsAfter.get(2), NEW, APPROVE_AS_ASSET_MANAGER, APPROVED_BY_ASSET_MANAGER);
        assertTransition(transitionsAfter.get(3), null, INSTANTIATE, NEW);
    }

    @Ignore() // not passing, we think, cos cannot nest event bus calls when using guava
    @Test
    public void approve_by_country_directory_once_approved_by_asset_manager_and_bank_account_is_verified() {

        // given
        final IncomingDocumentCategorisationStateTransitionType.TransitionEvent ev =
                new IncomingDocumentCategorisationStateTransitionType.TransitionEvent(invoiceDoc,
                        null, IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY);
        ev.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(ev);

        // and given the bank account is verified
        getFixtureClock().addTime(0,1);
        wrap(mixin(BankAccount_verify.class, bankAccount)).act(null);
        assertState(bankAccount, VERIFIED);

        // and given
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsAssetManager.class, incomingInvoice)).act("looks good to me!");

        assertState(incomingInvoice, APPROVED_BY_ASSET_MANAGER);

        final List<IncomingInvoiceApprovalStateTransition> transitionsBefore = findTransitions(this.incomingInvoice);
        assertThat(transitionsBefore.size()).isEqualTo(3);
        assertTransition(transitionsBefore.get(0), APPROVED_BY_ASSET_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, null);
        assertTransition(transitionsBefore.get(1), NEW, APPROVE_AS_ASSET_MANAGER, APPROVED_BY_ASSET_MANAGER);
        assertTransition(transitionsBefore.get(2), null, INSTANTIATE, NEW);

        // when
        getFixtureClock().addTime(0,1);
        wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act("me too!");

        // then
        assertState(incomingInvoice, PAYABLE);

        final List<IncomingInvoiceApprovalStateTransition> transitionsAfter = findTransitions(this.incomingInvoice);
        assertThat(transitionsAfter.size()).isEqualTo(4);
        assertTransition(transitionsAfter.get(0), APPROVED_BY_COUNTRY_DIRECTOR, CHECK_BANK_ACCOUNT, PAYABLE);
        assertTransition(transitionsAfter.get(1), APPROVED_BY_ASSET_MANAGER, APPROVE_AS_COUNTRY_DIRECTOR, APPROVED_BY_COUNTRY_DIRECTOR);
        assertTransition(transitionsAfter.get(2), NEW, APPROVE_AS_ASSET_MANAGER, APPROVED_BY_ASSET_MANAGER);
        assertTransition(transitionsAfter.get(3), null, INSTANTIATE, NEW);

    }


    private void givenInvoiceAssociatedWithProject() {
        final LocalDate startDate = clockService.now().plusDays(20);
        final LocalDate endDate = clockService.now().plusDays(30);

        incomingInvoice.addItem(
                charge_for_works, "some invoice item description",
                new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("120.00"),
                charge_for_works.getTax(),
                incomingInvoice.getDueDate(), startDate, endDate,
                propertyForOxf, project);
    }


    static void assertTransition(
            final IncomingInvoiceApprovalStateTransition transition,
            final IncomingInvoiceApprovalState from,
            final IncomingInvoiceApprovalStateTransitionType type,
            final IncomingInvoiceApprovalState to) {

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

    void assertState(final IncomingInvoice incomingInvoice, final IncomingInvoiceApprovalState expected) {
        assertThat(wrap(mixin(IncomingInvoice_approvalState.class, incomingInvoice)).prop()).isEqualTo(
                expected);
    }

    void assertState(final BankAccount bankAccount, final BankAccountVerificationState expected) {
        assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
                expected);
    }

    protected List<IncomingInvoiceApprovalStateTransition> findTransitions(final IncomingInvoice incomingInvoice) {
        return incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
    }

    @Inject
    ProjectRepository projectRepository;

    @Inject
    EventBusService eventBusService;

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    CurrencyRepository currencyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    FactoryService factoryService;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    ClockService clockService;

}

