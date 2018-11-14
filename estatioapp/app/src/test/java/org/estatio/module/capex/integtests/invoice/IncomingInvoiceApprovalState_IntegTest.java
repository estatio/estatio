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
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_reject;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
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

    Property propertyForViv;
    Party buyer;
    Party seller;

    Country france;
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
                ec.executeChild(this, new IncomingChargesFraXlsxFixture());
                ec.executeChildren(this,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        BankAccount_enum.TopModelFr,
                        Person_enum.BrunoTreasurerFr,
                        Person_enum.BertrandIncomingInvoiceManagerFr,
                        Person_enum.PeterPanProjectManagerFr,
                        Person_enum.GabrielCountryDirectorFr);
            }
        });

        Person Peter = Person_enum.PeterPanProjectManagerFr.findUsing(serviceRegistry);
        Peter.addRole(ProjectRoleTypeEnum.PROJECT_MANAGER);
    }

    @Before
    public void setUp() {
        propertyForViv = Property_enum.VivFr.findUsing(serviceRegistry);

        buyer = OrganisationAndComms_enum.HelloWorldFr.findUsing(serviceRegistry);
        seller = OrganisationAndComms_enum.TopModelFr.findUsing(serviceRegistry);

        france = countryRepository.findCountry(Country_enum.FRA.getRef3());
        charge_for_works = chargeRepository.findByReference("WORKS");

        project = projectRepository.findByReference("VIV-01");

        bankAccount = BankAccount_enum.TopModelFr.findUsing(serviceRegistry);

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
                Person_enum.BrunoTreasurerFr.getRef());
        SortedSet<PartyRole> rolesforBruno = personEmmaWithNoRoleAsPropertyManager.getRoles();
        assertThat(rolesforBruno.size()).isEqualTo(1);
        assertThat(rolesforBruno.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey()));

        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(Person_enum.BrunoTreasurerFr.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNotNull();
        assertThat(error.getMessage()).contains("Reason: Task assigned to 'INCOMING_INVOICE_MANAGER' role");

    }

    @Test
    public void complete_works_when_having_appropriate_role_type_test() {

        Exception error = new Exception();

        // given
        Person personEmma = (Person) partyRepository.findPartyByReference(
                Person_enum.BrunoTreasurerFr.getRef());
        PartyRoleType roleAsIncInvoiceManager = partyRoleTypeRepository.findByKey("INCOMING_INVOICE_MANAGER");
        personEmma.addRole(roleAsIncInvoiceManager);
        transactionService.nextTransaction();
        SortedSet<PartyRole> rolesforEmma = personEmma.getRoles();
        assertThat(rolesforEmma.size()).isEqualTo(2);
        assertThat(rolesforEmma.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey("INCOMING_INVOICE_MANAGER"));

        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(Person_enum.BrunoTreasurerFr.getRef().toLowerCase(), (Runnable) () ->
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
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(incomingInvoice).changePaymentMethod(PaymentMethod.CREDIT_CARD));
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.PeterPanProjectManagerFr.getRef().toLowerCase(), (Runnable) () ->
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
        sudoService.sudo(Person_enum.GabrielCountryDirectorFr.getRef().toLowerCase(), (Runnable) () ->
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
        sudoService.sudo(Person_enum.BrunoTreasurerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_checkPayment.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAID);

    }

    @Test
    public void refund_by_supplier_skips_bank_account_verification_and_creates_check_task_for_treasury(){

        // given
        PartyRoleType typeForTreasurer = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey());

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(incomingInvoice).changePaymentMethod(PaymentMethod.REFUND_BY_SUPPLIER));
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.PeterPanProjectManagerFr.getRef().toLowerCase(), (Runnable) () ->
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
        sudoService.sudo(Person_enum.GabrielCountryDirectorFr.getRef().toLowerCase(), (Runnable) () ->
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
        sudoService.sudo(Person_enum.BrunoTreasurerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_checkPayment.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAID);

    }

    @Test
    public void explicit_and_automatic_approvals_work(){

        // given
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache

        // when
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.PeterPanProjectManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("PROJECT_MANAGER", null, null, false));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.GabrielCountryDirectorFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act( null, false));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.BrunoTreasurerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_reject.class, incomingInvoice)).act("TREASURER",null, "No good"));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));

        // then
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        Assertions.assertThat(transitionsOfInvoice.size()).isEqualTo(11);
        Assertions.assertThat(transitionsOfInvoice.get(0).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.CONFIRM_BANK_ACCOUNT_VERIFIED);
        Assertions.assertThat(transitionsOfInvoice.get(0).isCompleted()).isFalse();
        Assertions.assertThat(transitionsOfInvoice.get(1).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.CHECK_BANK_ACCOUNT);
        Assertions.assertThat(transitionsOfInvoice.get(1).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(2).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR);
        Assertions.assertThat(transitionsOfInvoice.get(2).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(2).getCompletedBy()).isNull();
        Assertions.assertThat(transitionsOfInvoice.get(3).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.APPROVE);
        Assertions.assertThat(transitionsOfInvoice.get(3).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(3).getCompletedBy()).isNull();
        Assertions.assertThat(transitionsOfInvoice.get(4).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        Assertions.assertThat(transitionsOfInvoice.get(4).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(4).getTask()).isNotNull();
        Assertions.assertThat(transitionsOfInvoice.get(4).getCompletedBy()).isEqualTo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase());
        Assertions.assertThat(transitionsOfInvoice.get(5).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.REJECT);
        Assertions.assertThat(transitionsOfInvoice.get(5).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(5).getTask()).isNull();
        Assertions.assertThat(transitionsOfInvoice.get(5).getCompletedBy()).isEqualTo(Person_enum.BrunoTreasurerFr.getRef().toLowerCase());
        Assertions.assertThat(transitionsOfInvoice.get(6).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.CHECK_BANK_ACCOUNT);
        Assertions.assertThat(transitionsOfInvoice.get(6).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(6).getTask()).isNull();
        Assertions.assertThat(transitionsOfInvoice.get(6).getCompletedBy()).isNull();
        Assertions.assertThat(transitionsOfInvoice.get(7).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR);
        Assertions.assertThat(transitionsOfInvoice.get(7).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(7).getTask()).isNotNull();
        Assertions.assertThat(transitionsOfInvoice.get(7).getCompletedBy()).isEqualTo(Person_enum.GabrielCountryDirectorFr.getRef().toLowerCase());
        Assertions.assertThat(transitionsOfInvoice.get(8).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.APPROVE);
        Assertions.assertThat(transitionsOfInvoice.get(8).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(8).getTask()).isNotNull();
        Assertions.assertThat(transitionsOfInvoice.get(8).getCompletedBy()).isEqualTo(Person_enum.PeterPanProjectManagerFr.getRef().toLowerCase());
        Assertions.assertThat(transitionsOfInvoice.get(9).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        Assertions.assertThat(transitionsOfInvoice.get(9).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(9).getTask()).isNotNull();
        Assertions.assertThat(transitionsOfInvoice.get(9).getCompletedBy()).isEqualTo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase());
        Assertions.assertThat(transitionsOfInvoice.get(10).getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.INSTANTIATE);
        Assertions.assertThat(transitionsOfInvoice.get(10).isCompleted()).isTrue();
        Assertions.assertThat(transitionsOfInvoice.get(10).getTask()).isNull();
        Assertions.assertThat(transitionsOfInvoice.get(10).getCompletedBy()).isEqualTo(Person_enum.DanielOfficeAdministratorFr.getRef().toLowerCase());

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

