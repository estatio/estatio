package org.estatio.module.capex.integtests.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingState;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransition;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.capex.dom.invoice.accountingaudit.transitions.IncomingInvoice_accountingTransitions;
import org.estatio.module.capex.dom.invoice.accountingaudit.triggers.IncomingInvoice_audit;
import org.estatio.module.capex.dom.invoice.accountingaudit.triggers.IncomingInvoice_escalate;
import org.estatio.module.capex.dom.invoice.accountingaudit.triggers.IncomingInvoice_reAudit;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_reject;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;
import org.estatio.module.task.dom.task.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceAccountingState_IntegTest extends CapexModuleIntegTestAbstract {

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
            protected void execute(final ExecutionContext ec) {
                ec.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                ec.executeChild(this, new IncomingChargesFraXlsxFixture());
                ec.executeChildren(this,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        BankAccount_enum.TopModelFr,
                        Person_enum.FifineLacroixFr);
            }
        });
    }

    @Before
    public void setUp() {
        propertyForViv = Property_enum.VivFr.findUsing(serviceRegistry);

        buyer = OrganisationAndComms_enum.HelloWorldFr.findUsing(serviceRegistry);
        seller = OrganisationAndComms_enum.TopModelFr.findUsing(serviceRegistry);
        ((Organisation) seller).setChamberOfCommerceCode("Code");

        france = countryRepository.findCountry(Country_enum.FRA.getRef3());
        charge_for_works = chargeRepository.findByReference("WORKS");

        project = projectRepository.findByReference("VIV-01");

        bankAccount = BankAccount_enum.TopModelFr.findUsing(serviceRegistry);

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("65432", seller, new LocalDate(2014,5,13));
        incomingInvoice.setBankAccount(bankAccount);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.NEW);

    }

    @Test
    public void normal_accounting_workflow_scenario_test() {

        // given
        // when
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, null));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);

        List<IncomingInvoiceAccountingStateTransition> transitions = mixin(IncomingInvoice_accountingTransitions.class,
                incomingInvoice).coll();
        assertThat(transitions).hasSize(3);

        final IncomingInvoiceAccountingStateTransition transitionToAuditable = transitions.get(1);
        assertThat(transitionToAuditable.isCompleted()).isTrue();
        assertThat(transitionToAuditable.getFromState()).isEqualTo(IncomingInvoiceAccountingState.NEW);
        assertThat(transitionToAuditable.getToState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);

        final IncomingInvoiceAccountingStateTransition pendingTransition = transitions.get(0);
        assertThat(pendingTransition.isCompleted()).isFalse();
        assertThat(pendingTransition.getFromState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);
        assertThat(pendingTransition.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.AUDIT);
        assertThat(pendingTransition.getTask().getAssignedTo()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.AUDITOR_ACCOUNTANT.getKey()));

        // and when
        queryResultsCache.resetForNextTransaction();
        sudoService.sudo(Person_enum.AudreyExternalFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_audit.class, incomingInvoice)).act("Is OK!", false));

        // then
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.AUDITED);
        transitions = mixin(IncomingInvoice_accountingTransitions.class,
                incomingInvoice).coll();
        assertThat(transitions).hasSize(3);
        final IncomingInvoiceAccountingStateTransition completedTransition = transitions.get(0);
        assertThat(completedTransition.isCompleted()).isTrue();
        assertThat(completedTransition.getFromState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);
        assertThat(completedTransition.getToState()).isEqualTo(IncomingInvoiceAccountingState.AUDITED);
        assertThat(completedTransition.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.AUDIT);
        assertThat(completedTransition.getComment()).isEqualTo("Is OK!");

    }

    @Test
    public void when_audited_and_completed_again_scenario_test() {

        // given
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, null));
        queryResultsCache.resetForNextTransaction();
        sudoService.sudo(Person_enum.AudreyExternalFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_audit.class, incomingInvoice)).act("Is OK!", false));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.AUDITED);

        // when
        queryResultsCache.resetForNextTransaction();
        sudoService.sudo(Person_enum.FloellaAssetManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_reject.class, incomingInvoice)).act(null, null, "Sorry no good"));
        queryResultsCache.resetForNextTransaction();
        sudoService.sudo(Person_enum.FloellaAssetManagerFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, "Better?"));

        // then
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.CHANGED);
        List<IncomingInvoiceAccountingStateTransition> transitions = mixin(IncomingInvoice_accountingTransitions.class,
                incomingInvoice).coll();
        assertThat(transitions).hasSize(5);

        final IncomingInvoiceAccountingStateTransition transitionToChanged = transitions.get(1);
        assertThat(transitionToChanged.isCompleted()).isTrue();
        assertThat(transitionToChanged.getFromState()).isEqualTo(IncomingInvoiceAccountingState.AUDITED);
        assertThat(transitionToChanged.getToState()).isEqualTo(IncomingInvoiceAccountingState.CHANGED);
        assertThat(transitionToChanged.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.MARK_CHANGED);

        final IncomingInvoiceAccountingStateTransition pendingTransition = transitions.get(0);
        assertThat(pendingTransition.isCompleted()).isFalse();
        assertThat(pendingTransition.getFromState()).isEqualTo(IncomingInvoiceAccountingState.CHANGED);
        assertThat(pendingTransition.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.RE_AUDIT);
        assertThat(pendingTransition.getTask().getAssignedTo()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.AUDITOR_ACCOUNTANT.getKey()));

        // and when
        queryResultsCache.resetForNextTransaction();
        sudoService.sudo(Person_enum.AudreyExternalFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_reAudit.class, incomingInvoice)).act("Is OK now!", false));

        // then
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.AUDITED);
        transitions = mixin(IncomingInvoice_accountingTransitions.class,
                incomingInvoice).coll();
        assertThat(transitions).hasSize(5);
        final IncomingInvoiceAccountingStateTransition completedTransition = transitions.get(0);
        assertThat(completedTransition.isCompleted()).isTrue();
        assertThat(completedTransition.getFromState()).isEqualTo(IncomingInvoiceAccountingState.CHANGED);
        assertThat(completedTransition.getToState()).isEqualTo(IncomingInvoiceAccountingState.AUDITED);
        assertThat(completedTransition.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.RE_AUDIT);
        assertThat(completedTransition.getComment()).isEqualTo("Is OK now!");

    }

    @Test
    public void when_escalated_scenario_test() throws Exception {

        // given
        final Person lacroix = Person_enum.FifineLacroixFr.findUsing(serviceRegistry);
        final PartyRoleType escalatorRoleType = partyRoleTypeRepository.findOrCreate(PartyRoleTypeEnum.ESCALATOR);
        lacroix.addRole(escalatorRoleType);
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);

        // when
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_escalate.class, incomingInvoice)).act("We're in a hurry"));

        // then
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.ESCALATED);
        List<IncomingInvoiceAccountingStateTransition> transitions = mixin(IncomingInvoice_accountingTransitions.class,
                incomingInvoice).coll();
        assertThat(transitions).hasSize(4);
        final IncomingInvoiceAccountingStateTransition transitionToEscalated = transitions.get(1);
        assertThat(transitionToEscalated.isCompleted()).isTrue();
        assertThat(transitionToEscalated.getFromState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);
        assertThat(transitionToEscalated.getToState()).isEqualTo(IncomingInvoiceAccountingState.ESCALATED);
        assertThat(transitionToEscalated.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.ESCALATE);
        assertThat(transitionToEscalated.getComment()).isEqualTo("We're in a hurry");

        final IncomingInvoiceAccountingStateTransition pendingTransition = transitions.get(0);
        assertThat(pendingTransition.isCompleted()).isFalse();
        assertThat(pendingTransition.getFromState()).isEqualTo(IncomingInvoiceAccountingState.ESCALATED);
        assertThat(pendingTransition.getTransitionType()).isEqualTo(IncomingInvoiceAccountingStateTransitionType.AUDIT);
        assertThat(pendingTransition.getTask().getAssignedTo()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.AUDITOR_ACCOUNTANT.getKey()));

    }

    @Test
    public void escalation_needs_escalator_role_test() throws Exception {

        Exception error = new Exception();

        // given
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getAccountingState()).isEqualTo(IncomingInvoiceAccountingState.AUDITABLE);

        // when
        try {
            sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_escalate.class, incomingInvoice)).act("Some very good reason"));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNotNull();
        assertThat(error.getMessage()).contains("You need role ESCALATOR");

    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    IncomingInvoiceAccountingStateTransition.Repository incomingInvoiceAccountingStateTransitionRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    TaskRepository taskRepository;

}

