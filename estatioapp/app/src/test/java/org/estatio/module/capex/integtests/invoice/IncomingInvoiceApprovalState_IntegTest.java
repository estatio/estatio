package org.estatio.module.capex.integtests.invoice;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccount_verificationState;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCountryDirector;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_checkPayment;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.CapexChargeHierarchyXlsxFixture;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState.NOT_VERIFIED;

public class IncomingInvoiceApprovalState_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForOxf;
    Party buyer;
    Party seller;

    Country greatBritain;
    Charge charge_for_works;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;
    Project project;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext ec) {
                ec.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                ec.executeChild(this, new CapexChargeHierarchyXlsxFixture());
                ec.executeChildren(this,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        BankAccount_enum.TopModelGb,
                        Person_enum.EmmaTreasurerGb,
                        Person_enum.JonathanPropertyManagerGb,
                        Person_enum.PeterPanProjectManagerGb,
                        Person_enum.OscarCountryDirectorGb);
            }
        });
    }

    @Before
    public void setUp() {
        propertyForOxf = Property_enum.OxfGb.findUsing(serviceRegistry);

        buyer = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
        seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);

        greatBritain = countryRepository.findCountry(Country_enum.GBR.getRef3());
        charge_for_works = chargeRepository.findByReference("WORKS");

        project = projectRepository.findByReference("OXF-02");

        bankAccount = BankAccount_enum.TopModelGb.findUsing(serviceRegistry);

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("65432", seller, new LocalDate(2014,5,13));
        incomingInvoice.setBankAccount(bankAccount);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
        assertState(bankAccount, NOT_VERIFIED);

    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Test
    public void complete_should_fail_when_not_having_appropriate_role_type_test() {

        Exception error = new Exception();

        // given
        Person personEmmaWithNoRoleAsPropertyManager = (Person) partyRepository.findPartyByReference(
                Person_enum.EmmaTreasurerGb.getRef());
        SortedSet<PartyRole> rolesforEmma = personEmmaWithNoRoleAsPropertyManager.getRoles();
        assertThat(rolesforEmma.size()).isEqualTo(1);
        assertThat(rolesforEmma.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey()));

        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(Person_enum.EmmaTreasurerGb.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNotNull();
        assertThat(error.getMessage()).contains("Reason: Task assigned to 'PROPERTY_MANAGER' role");

    }

    @Test
    public void complete_works_when_having_appropriate_role_type_test() {

        Exception error = new Exception();

        // given
        Person personEmma = (Person) partyRepository.findPartyByReference(
                Person_enum.EmmaTreasurerGb.getRef());
        PartyRoleType roleAsPropertyManager = partyRoleTypeRepository.findByKey("PROPERTY_MANAGER");
        personEmma.addRole(roleAsPropertyManager);
        transactionService.nextTransaction();
        SortedSet<PartyRole> rolesforEmma = personEmma.getRoles();
        assertThat(rolesforEmma.size()).isEqualTo(2);
        assertThat(rolesforEmma.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey("PROPERTY_MANAGER"));

        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(Person_enum.EmmaTreasurerGb.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

    }

    void assertState(final BankAccount bankAccount, final BankAccountVerificationState expected) {
        Assertions.assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
                expected);
    }

    @Test
    public void paid_by_credit_card_skips_bank_account_verification_and_creates_check_task_for_treasury(){

        // given
        PartyRoleType typeForTreasurer = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey());

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(incomingInvoice).changePaymentMethod(PaymentMethod.CREDIT_CARD));
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.PeterPanProjectManagerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("COUNTRY_DIRECTOR", null, null, false));
        List<Task> tasksForTreasury = taskRepository.findIncompleteByRole(typeForTreasurer);
        assertThat(tasksForTreasury).isEmpty();

        BankAccount bankAccount = incomingInvoice.getBankAccount();
        assertThat(bankAccount).isNotNull();
        BankAccountVerificationState state = stateTransitionService
                .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
        assertThat(state).isEqualTo(BankAccountVerificationState.NOT_VERIFIED);


        // when

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.OscarCountryDirectorGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAYABLE);
        List<IncomingInvoiceApprovalStateTransition> transitions = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitions.size()).isEqualTo(7);
        assertThat(transitions.get(0).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.CHECK_PAYMENT);
        tasksForTreasury = taskRepository.findIncompleteByRole(typeForTreasurer);
        assertThat(tasksForTreasury.size()).isEqualTo(1);
        assertThat(tasksForTreasury.get(0).getDescription()).isEqualTo("Check Payment");
        // and still
        state = stateTransitionService
                .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
        assertThat(state).isEqualTo(BankAccountVerificationState.NOT_VERIFIED);

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.EmmaTreasurerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_checkPayment.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAID);

    }

    @Test
    public void refund_by_supplier_skips_bank_account_verification_and_creates_check_task_for_treasury(){

        // given
        PartyRoleType typeForTreasurer = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey());

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(incomingInvoice).changePaymentMethod(PaymentMethod.REFUND_BY_SUPPLIER));
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.PeterPanProjectManagerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("COUNTRY_DIRECTOR", null, null, false));
        List<Task> tasksForTreasury = taskRepository.findIncompleteByRole(typeForTreasurer);
        assertThat(tasksForTreasury).isEmpty();

        BankAccount bankAccount = incomingInvoice.getBankAccount();
        assertThat(bankAccount).isNotNull();
        BankAccountVerificationState state = stateTransitionService
                .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
        assertThat(state).isEqualTo(BankAccountVerificationState.NOT_VERIFIED);


        // when

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.OscarCountryDirectorGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAYABLE);
        List<IncomingInvoiceApprovalStateTransition> transitions = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitions.size()).isEqualTo(7);
        assertThat(transitions.get(0).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.CHECK_PAYMENT);
        tasksForTreasury = taskRepository.findIncompleteByRole(typeForTreasurer);
        assertThat(tasksForTreasury.size()).isEqualTo(1);
        assertThat(tasksForTreasury.get(0).getDescription()).isEqualTo("Check Payment");
        // and still
        state = stateTransitionService
                .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
        assertThat(state).isEqualTo(BankAccountVerificationState.NOT_VERIFIED);

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.EmmaTreasurerGb.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_checkPayment.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAID);

    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    TaskRepository taskRepository;

    @Inject
    StateTransitionService stateTransitionService;
}

