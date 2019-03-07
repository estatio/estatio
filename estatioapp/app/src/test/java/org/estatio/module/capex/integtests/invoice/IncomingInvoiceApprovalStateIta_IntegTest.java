package org.estatio.module.capex.integtests.invoice;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_advise;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_adviseToApprove;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCenterManager;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCountryDirector;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveWhenApprovedByCenterManager;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_noAdvise;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_reject;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_suspend;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.IncomingInvoiceItem_orderItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceApprovalStateIta_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForRon;
    Party buyer;
    Party seller;

    Country italy;

    IncomingInvoice incomingInvoice;

    IncomingInvoice recoverableInvoice;

    IncomingInvoice invoiceForDirectDebit;

    IncomingInvoice invoiceWithPaidDate;

    BankAccount bankAccount;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        Person_enum.CarmenIncomingInvoiceManagerIt,
                        Person_enum.IlicCenterManagerIt,
                        Person_enum.FloellaAssetManagerIt,
                        Person_enum.FlorisAssetManagerIt,
                        Person_enum.RobertCountryDirectorIt,
                        Person_enum.SergioPreferredCountryDirectorIt,
                        Person_enum.FabrizioPreferredManagerIt,
                        Person_enum.TechnizioAdvisorIt,
                        Person_enum.LoredanaPropertyInvoiceMgrIt, // this fixture has to be set before the creation of the recoverable invoice because task assignment is determined by the role being setup here
                        Organisation_enum.IncomingBuyerIt,
                        IncomingInvoiceNoDocument_enum.invoiceForItaNoOrder,
                        IncomingInvoiceNoDocument_enum.invoiceForItaRecoverable,
                        IncomingInvoiceNoDocument_enum.invoiceForItaDirectDebit,
                        IncomingInvoiceNoDocument_enum.invoiceForItaWithPaidDate,
                        Project_enum.GraProject
                );
            }
        });
        transactionService.nextTransaction();

    }

    @Before
    public void setUp() {
        propertyForRon = Property_enum.RonIt.findUsing(serviceRegistry);
        assertThat(propertyForRon.getRoles()).hasSize(5);

        buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);
        seller = Organisation_enum.TopModelIt.findUsing(serviceRegistry);

        italy = countryRepository.findCountry(Country_enum.ITA.getRef3());

        bankAccount = BankAccount_enum.TopModelIt.findUsing(serviceRegistry);

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("12345", seller, new LocalDate(2017, 12, 20));
        incomingInvoice.setBankAccount(bankAccount);

        recoverableInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("123456", seller, new LocalDate(2017, 12, 20));
        recoverableInvoice.setBankAccount(bankAccount);

        invoiceForDirectDebit = IncomingInvoiceNoDocument_enum.invoiceForItaDirectDebit.findUsing(serviceRegistry2);

        invoiceForDirectDebit.setBankAccount(bankAccount);

        invoiceWithPaidDate = IncomingInvoiceNoDocument_enum.invoiceForItaWithPaidDate.findUsing(serviceRegistry2);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);

        assertThat(recoverableInvoice).isNotNull();
        assertThat(recoverableInvoice.getApprovalState()).isNotNull();
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Inject StateTransitionService stateTransitionService;

    @Test
    public void approved_invoice_with_gross_amount_equal_or_lower_then_100000_threshold_does_not_need_further_approval() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(2);
        IncomingInvoiceApprovalStateTransition nextPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(nextPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.NEW,
                null,
                IncomingInvoiceApprovalStateTransitionType.COMPLETE
        ));
        Task nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER,
                null,       // task assigned to Role only, not to person
                "Complete"
        ));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getGrossAmount()).isEqualTo(new BigDecimal("100000.00"));

        // then
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);
        nextPendingTransition = transitionsOfInvoice.get(0);
        nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FlorisAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        final IncomingInvoiceApprovalStateTransition completedByAssetManager = transitionsOfInvoice.get(2);
        assertTransition(completedByAssetManager, new ExpectedTransitionResult(
                true,
                "fdigrande",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));

        final Task taskForAssetManager = completedByAssetManager.getTask();
        assertTask(taskForAssetManager, new ExpectedTaskResult(
                true,
                partyRoleTypeRepository.findByKey(FixedAssetRoleTypeEnum.ASSET_MANAGER.getKey()),
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(1);
        assertTransition(lastAutomatic, new ExpectedTransitionResult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS_WHEN_APPROVED
        ));

        final IncomingInvoiceApprovalStateTransition lastPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(lastPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                null,
                IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));
        assertThat(lastPendingTransition.getTask()).isNull();

        // and when in coda books - artifically setting postedToCodaBooks and triggering the transition
        queryResultsCache.resetForNextTransaction(); // otherwise would be manager auto transitioning
        incomingInvoice.setPostedToCodaBooks(true);
        stateTransitionService.trigger(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS, null, null);

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAYABLE);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);
        IncomingInvoiceApprovalStateTransition pendingTransition = transitionsOfInvoice.get(0);
        assertTransition(pendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PAYABLE,
                null,
                IncomingInvoiceApprovalStateTransitionType.PAID_IN_CODA
        ));
        assertThat(pendingTransition.getTask()).isNull();

        final IncomingInvoiceApprovalStateTransition lastTransition = transitionsOfInvoice.get(1);
        assertTransition(lastTransition, new ExpectedTransitionResult(
                true,
                "tester",
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                IncomingInvoiceApprovalState.PAYABLE,
                IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));

        // then also - not pretty but this one saves us a separate integ test...
        assertThat(mixin(IncomingInvoice_reject.class, incomingInvoice).hideAct()).isTrue();

        // and when paid - artifically setting paid date here and triggering the transition
        incomingInvoice.setPaidDate(FixtureClock.getTimeAsLocalDate());
        stateTransitionService.trigger(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.PAID_IN_CODA, null, null);

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAID);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);

        final IncomingInvoiceApprovalStateTransition finalTransition = transitionsOfInvoice.get(0);
        assertTransition(finalTransition, new ExpectedTransitionResult(
                true,
                "tester",
                IncomingInvoiceApprovalState.PAYABLE,
                IncomingInvoiceApprovalState.PAID,
                IncomingInvoiceApprovalStateTransitionType.PAID_IN_CODA
        ));

    }

    @Test
    public void slow_approval_means_hits_pending_coda_books_with_coda_doc_already_in_books_then_straight_through() throws Exception {

        // given
        incomingInvoice.setPostedToCodaBooks(true);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FlorisAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAYABLE);

    }

    @Test
    public void approved_invoice_with_gross_amount_higher_then_100000_threshold_needs_directors_approval() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        incomingInvoice.changeAmounts(new BigDecimal("81967.22"), new BigDecimal("100000.01"));
        IncomingInvoiceItem item = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        item.addAmounts(new BigDecimal("0.01"), BigDecimal.ZERO, new BigDecimal("0.01"));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FlorisAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED);

        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition completedByAssetManager = transitionsOfInvoice.get(1);
        assertTransition(completedByAssetManager,
                new ExpectedTransitionResult(true,
                        "fdigrande",
                        IncomingInvoiceApprovalState.COMPLETED,
                        IncomingInvoiceApprovalState.APPROVED,
                        IncomingInvoiceApprovalStateTransitionType.APPROVE));

        final Task taskForAssetManager = completedByAssetManager.getTask();
        assertTask(taskForAssetManager, new ExpectedTaskResult(
                true,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));

        final IncomingInvoiceApprovalStateTransition nextPending = transitionsOfInvoice.get(0);
        assertTransition(nextPending,
                new ExpectedTransitionResult(false,
                        null,
                        IncomingInvoiceApprovalState.APPROVED,
                        null,
                        IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
                ));
        final Task taskForDirector = nextPending.getTask();
        assertTask(taskForDirector,
                new ExpectedTaskResult(
                        false,
                        PartyRoleTypeEnum.COUNTRY_DIRECTOR,
                        null
                )
        );

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.RobertCountryDirectorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);

        final IncomingInvoiceApprovalStateTransition completedByDirector = transitionsOfInvoice.get(2);
        assertTransition(completedByDirector, new ExpectedTransitionResult(
                true,
                "rstracciatella",
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));
        assertThat(completedByDirector.getTask().getCompletedBy()).isEqualTo("rstracciatella");

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(1);
        assertTransition(lastAutomatic, new ExpectedTransitionResult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS
        ));

        final IncomingInvoiceApprovalStateTransition lastPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(lastPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                null,
                IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));
        assertThat(lastPendingTransition.getTask()).isNull();

    }

    @Test
    public void recoverable_invoice_for_property_having_center_manager_needs_to_be_approved_by_center_manager_when_NOT_over_100000() throws Exception {

        // given
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(2);
        assertThat(recoverableInvoice.getProperty()).isSameAs(propertyForRon);
        assertThat(propertyForRon.getRoles()).hasSize(5);

        IncomingInvoiceApprovalStateTransition pendingTransition = transitionsOfInvoice.get(0);
        Task pendingTask = pendingTransition.getTask();
        assertTask(pendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.PROPERTY_INV_MANAGER,
                Person_enum.LoredanaPropertyInvoiceMgrIt.findUsing(serviceRegistry2)
        ));

        // and when completed
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.LoredanaPropertyInvoiceMgrIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, recoverableInvoice)).act("SOME_ROLE", null, null));
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(recoverableInvoice.getGrossAmount()).isEqualTo(new BigDecimal("100000.00"));
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);

        pendingTransition = transitionsOfInvoice.get(0);
        assertTransition(pendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.COMPLETED,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER
        ));

        final Task pendingTransitionTask = pendingTransition.getTask();
        assertTask(pendingTransitionTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.CENTER_MANAGER,
                Person_enum.IlicCenterManagerIt.findUsing(serviceRegistry2)
        ));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.IlicCenterManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCenterManager.class, recoverableInvoice)).act(null, null, false));

        // then
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition lastCompletedTransition = transitionsOfInvoice.get(1);
        assertTransition(lastCompletedTransition, new ExpectedTransitionResult(
                true,
                "iresponsabile",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER
        ));

        assertThat(lastCompletedTransition.isCompleted()).isTrue();
        assertThat(lastCompletedTransition.getCompletedBy()).isEqualTo("iresponsabile");

        final IncomingInvoiceApprovalStateTransition newPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(newPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER
        ));

        final Task newPendingTransitionTask = newPendingTransition.getTask();
        assertTask(newPendingTransitionTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FloellaAssetManagerIt.findUsing(serviceRegistry2)
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FloellaAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveWhenApprovedByCenterManager.class, recoverableInvoice)).act(null, null, false));

        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);

        final IncomingInvoiceApprovalStateTransition completedByAssetManager = transitionsOfInvoice.get(2);
        assertTransition(completedByAssetManager, new ExpectedTransitionResult(
                true,
                "fgestore",
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER
        ));

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(1);
        assertTransition(lastAutomatic, new ExpectedTransitionResult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS_WHEN_APPROVED
        ));

        final IncomingInvoiceApprovalStateTransition lastPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(lastPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                null,
                IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));
        assertThat(lastPendingTransition.getTask()).isNull();

    }

    @Test
    public void recoverable_invoice_for_property_having_center_manager_needs_to_be_approved_by_center_manager_when_over_100000() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        recoverableInvoice.changeAmounts(new BigDecimal("81967.22"), new BigDecimal("100000.01"));
        IncomingInvoiceItem item = (IncomingInvoiceItem) recoverableInvoice.getItems().first();
        item.addAmounts(new BigDecimal("0.01"), BigDecimal.ZERO, new BigDecimal("0.01"));

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.LoredanaPropertyInvoiceMgrIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, recoverableInvoice)).act("SOME_ROLE", null, null));
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.IlicCenterManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCenterManager.class, recoverableInvoice)).act(null, null, false));

        // then
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition lastCompletedTransition = transitionsOfInvoice.get(1);
        assertTransition(lastCompletedTransition, new ExpectedTransitionResult(
                true,
                "iresponsabile",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER
        ));

        final IncomingInvoiceApprovalStateTransition newPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(newPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));

        final Task newPendingTransitionTask = newPendingTransition.getTask();
        assertTask(newPendingTransitionTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.INV_APPROVAL_DIRECTOR,
                Person_enum.SergioPreferredCountryDirectorIt.findUsing(serviceRegistry2)
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.SergioPreferredCountryDirectorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, recoverableInvoice)).act(null, false));

        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);

        final IncomingInvoiceApprovalStateTransition completedByInvoiceApprovalDirector = transitionsOfInvoice.get(2);
        assertTransition(completedByInvoiceApprovalDirector, new ExpectedTransitionResult(
                true,
                "sgalati",
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(1);
        assertTransition(lastAutomatic, new ExpectedTransitionResult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS
        ));

        final IncomingInvoiceApprovalStateTransition lastPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(lastPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                null,
                IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));
        assertThat(lastPendingTransition.getTask()).isNull();

    }

    @Test
    public void when_buyer_has_role_ECP_MGT_COMPANY_then_assign_tasks_to_preferred_manager_and_director() throws Exception {

        // given
        Organisation buyerWithPreferredManagerAndDirector = Organisation_enum.IncomingBuyerIt.findUsing(serviceRegistry2);
        buyerWithPreferredManagerAndDirector.addRole(IncomingInvoiceRoleTypeEnum.ECP_MGT_COMPANY);
        transactionService.nextTransaction();
        assertThat(IncomingInvoiceApprovalStateTransitionType.hasPreferredManagerAndDirector(buyerWithPreferredManagerAndDirector)).isTrue();
        incomingInvoice.setBuyer(buyerWithPreferredManagerAndDirector);

        Person preferredManager = Person_enum.FabrizioPreferredManagerIt.findUsing(serviceRegistry2);
        final PartyRoleType preferredManagerRoleType = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.PREFERRED_MANAGER.getKey());
        assertThat(preferredManager.hasPartyRoleType(preferredManagerRoleType));

        Person preferredDirector = Person_enum.SergioPreferredCountryDirectorIt.findUsing(serviceRegistry2);
        final PartyRoleType preferredDirectorRoleType = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.PREFERRED_DIRECTOR.getKey());
        assertThat(preferredDirector.hasPartyRoleType(preferredDirectorRoleType));

        // sets net amount above threshold so to approvals will be needed
        incomingInvoice.changeAmounts(new BigDecimal("81967.22"), new BigDecimal("100000.01"));
        IncomingInvoiceItem item = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        item.addAmounts(new BigDecimal("0.01"), BigDecimal.ZERO, new BigDecimal("0.01"));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));

        // then
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);

        IncomingInvoiceApprovalStateTransition nextPending = transitionsOfInvoice.get(0);
        assertTransition(nextPending, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.COMPLETED,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));
        Task taskForPreferredManager = nextPending.getTask();
        assertTask(taskForPreferredManager, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.PREFERRED_MANAGER,
                preferredManager
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FabrizioPreferredManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE_WHY??", null, null, true));

        // then
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        IncomingInvoiceApprovalStateTransition lastCompleted = transitionsOfInvoice.get(1);
        assertTransition(lastCompleted, new ExpectedTransitionResult(
                true,
                "fdarespons",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));
        Task lastCompletedTask = lastCompleted.getTask();
        assertTask(lastCompletedTask, new ExpectedTaskResult(
                true,
                PartyRoleTypeEnum.PREFERRED_MANAGER,
                preferredManager
        ));
        nextPending = transitionsOfInvoice.get(0);
        assertTransition(nextPending, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.APPROVED,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));
        Task taskForPreferredDirector = nextPending.getTask();
        assertTask(taskForPreferredDirector, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.PREFERRED_DIRECTOR,
                preferredDirector
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.SergioPreferredCountryDirectorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act(null, true));

        // then
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);

        lastCompleted = transitionsOfInvoice.get(2);
        assertTransition(lastCompleted, new ExpectedTransitionResult(
                true,
                "sgalati",
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));
        lastCompletedTask = lastCompleted.getTask();
        assertTask(lastCompletedTask, new ExpectedTaskResult(
                true,
                PartyRoleTypeEnum.PREFERRED_DIRECTOR,
                preferredDirector
        ));
        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(1);
        assertTransition(lastAutomatic, new ExpectedTransitionResult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS
        ));

        final IncomingInvoiceApprovalStateTransition lastPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(lastPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                null,
                IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));
        assertThat(lastPendingTransition.getTask()).isNull();

    }

    @Test
    public void when_invoice_has_no_property_approval_task_is_for_corporate_manager() throws Exception {

        // given
        incomingInvoice.setProperty(null);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));

        // then
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);

        IncomingInvoiceApprovalStateTransition nextPending = transitionsOfInvoice.get(0);
        Task pendingTask = nextPending.getTask();
        assertTask(pendingTask, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.CORPORATE_MANAGER,
                null    // in this case because no fixture is set up for this role
        ));

    }

    @Test
    public void italian_invoice_with_type_capex_have_approval_task_for_asset_manager() throws Exception {

        // given
        incomingInvoice.setType(IncomingInvoiceType.CAPEX);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));

        // then
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);

        IncomingInvoiceApprovalStateTransition nextPending = transitionsOfInvoice.get(0);
        Task pendingTask = nextPending.getTask();
        assertTask(pendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));

    }

    @Test
    public void invoice_having_payment_method_other_then_bank_transfer_bypasses_all_approval() throws Exception {

        // given
        List<IncomingInvoiceApprovalStateTransition> transitionsOfDirectDebitInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceForDirectDebit);

        // when, then
        assertThat(transitionsOfDirectDebitInvoice).hasSize(2);

        assertThat(invoiceForDirectDebit.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL);
        IncomingInvoiceApprovalStateTransition firstTransition = transitionsOfDirectDebitInvoice.get(1);
        assertTransition(firstTransition, new ExpectedTransitionResult(
                true, "tester", null, IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL, IncomingInvoiceApprovalStateTransitionType.INSTANTIATE_TO_PAYABLE
        ));
        assertThat(firstTransition.getTask()).isNull();

        IncomingInvoiceApprovalStateTransition nextPending = transitionsOfDirectDebitInvoice.get(0);
        assertTransition(nextPending, new ExpectedTransitionResult(
                false, null, IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL, null, IncomingInvoiceApprovalStateTransitionType.PAID_IN_CODA
        ));
        assertThat(nextPending.getTask()).isNull();

        // and when rejected
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_reject.class, invoiceForDirectDebit)).act("INCOMING_INVOICE_MANAGER", null, "No good; but rejection is of no use since has payment method that does not require approval"));

        // then will end up in pending coda books check again
        transitionsOfDirectDebitInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceForDirectDebit);
        assertThat(transitionsOfDirectDebitInvoice).hasSize(4);
        IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfDirectDebitInvoice.get(1);
        assertTransition(lastAutomatic, new ExpectedTransitionResult(
                true, null, IncomingInvoiceApprovalState.NEW, IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL, IncomingInvoiceApprovalStateTransitionType.AUTO_TRANSITION_TO_PENDING_CODA_BOOKS
        ));
        assertThat(lastAutomatic.getTask()).isNull();

        nextPending = transitionsOfDirectDebitInvoice.get(0);
        assertTransition(nextPending, new ExpectedTransitionResult(
                false, null, IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL, null, IncomingInvoiceApprovalStateTransitionType.PAID_IN_CODA
        ));
        assertThat(nextPending.getTask()).isNull();

    }

    @Test
    public void invoice_created_when_having_paid_date_is_automatically_approved() throws Exception {

        // given
        List<IncomingInvoiceApprovalStateTransition> transitionsOfPaidInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceWithPaidDate);

        // when, then
        assertThat(transitionsOfPaidInvoice).hasSize(2);

        assertThat(invoiceWithPaidDate.getPaidDate()).isNotNull();
        assertThat(invoiceWithPaidDate.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK);
        IncomingInvoiceApprovalStateTransition firstTransition = transitionsOfPaidInvoice.get(1);
        assertTransition(firstTransition, new ExpectedTransitionResult(
                true, "tester", null, IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK, IncomingInvoiceApprovalStateTransitionType.INSTANTIATE_BYPASSING_APPROVAL
        ));
        assertThat(firstTransition.getTask()).isNull();

        IncomingInvoiceApprovalStateTransition nextPending = transitionsOfPaidInvoice.get(0);
        assertTransition(nextPending, new ExpectedTransitionResult(
                false, null, IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK, null, IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS
        ));
        assertThat(nextPending.getTask()).isNull();
    }

    @Test
    public void completed_invoice_positive_advise_works() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);
        IncomingInvoiceApprovalStateTransition nextPendingTransition = transitionsOfInvoice.get(0);
        Task nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FlorisAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_advise.class, incomingInvoice)).act("ADVISOR", Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2), "Please advise me. Is this correct?", true));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_ADVISE);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);
        IncomingInvoiceApprovalStateTransition lastTransition = transitionsOfInvoice.get(1);
        assertTransition(lastTransition, new ExpectedTransitionResult(
                true,
                "fdigrande",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.PENDING_ADVISE,
                IncomingInvoiceApprovalStateTransitionType.ADVISE
        ));
        assertThat(lastTransition.getTask()).isNull();
        nextPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(nextPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.PENDING_ADVISE,
                null,
                IncomingInvoiceApprovalStateTransitionType.ADVISE_TO_APPROVE
        ));
        nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.ADVISOR,
                Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2)
        ));

        // and when advise is positive
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.TechnizioAdvisorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_adviseToApprove.class, incomingInvoice)).act("SOME_ROLE", null, "Looks good to me", true));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(6);

        IncomingInvoiceApprovalStateTransition adviseToApproveTransision = transitionsOfInvoice.get(2);
        assertTransition(adviseToApproveTransision, new ExpectedTransitionResult(
                true,
                "tmulticomp",
                IncomingInvoiceApprovalState.PENDING_ADVISE,
                IncomingInvoiceApprovalState.ADVISE_POSITIVE,
                IncomingInvoiceApprovalStateTransitionType.ADVISE_TO_APPROVE
        ));
        Task adviseToApproveTask = adviseToApproveTransision.getTask();
        assertTask(adviseToApproveTask, new ExpectedTaskResult(
                true,
                PartyRoleTypeEnum.ADVISOR,
                Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2)
        ));

        IncomingInvoiceApprovalStateTransition autoTransition = transitionsOfInvoice.get(1);
        assertTransition(autoTransition, new ExpectedTransitionResult(
                true,
                null,
                IncomingInvoiceApprovalState.ADVISE_POSITIVE,
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalStateTransitionType.AUTO_TRANSITION_WHEN_ADVISED_TO_APPROVE
        ));
        assertThat(autoTransition.getTask()).isNull();

        nextPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(nextPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.COMPLETED,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));
        nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));
    }

    @Test
    public void completed_invoice_No_advise_works_and_cleans_up_task() throws Exception {

        // given
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FlorisAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_advise.class, incomingInvoice)).act("ADVISOR", Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2), "Please advise me. Is this correct?", true));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_ADVISE);

        // when no advise
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.TechnizioAdvisorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_noAdvise.class, incomingInvoice)).act("ADVISOR", null, "Sorry, not my cup of tea", true));
        transactionService.nextTransaction();

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        IncomingInvoiceApprovalStateTransition noAdviseTransision = transitionsOfInvoice.get(1);
        assertTransition(noAdviseTransision, new ExpectedTransitionResult(
                true,
                "tmulticomp",
                IncomingInvoiceApprovalState.PENDING_ADVISE,
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalStateTransitionType.NO_ADVISE
        ));
        assertThat(noAdviseTransision.getTask()).isNull();

        IncomingInvoiceApprovalStateTransition nextPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(nextPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.COMPLETED,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));
        Task nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FlorisAssetManagerIt.findUsing(serviceRegistry2)
        ));

        List<Task> uncompletedForTechnizio = taskRepository.findIncompleteByPersonAssignedTo(Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2));
        assertThat(uncompletedForTechnizio).isEmpty();
    }

    @Test
    public void completed_invoice_when_rejected_by_advisor_works_and_cleans_up_task() throws Exception {

        // given
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FlorisAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_advise.class, incomingInvoice)).act("ADVISOR", Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2), "Please advise me. Is this correct?", true));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_ADVISE);

        // when rejected by advisor
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.TechnizioAdvisorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_reject.class, incomingInvoice)).act("ADVISOR", null, "Sorry, but this is not good at all"));
        transactionService.nextTransaction();

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);

        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        IncomingInvoiceApprovalStateTransition rejectTransision = transitionsOfInvoice.get(1);
        assertTransition(rejectTransision, new ExpectedTransitionResult(
                true,
                "tmulticomp",
                IncomingInvoiceApprovalState.PENDING_ADVISE,
                IncomingInvoiceApprovalState.NEW,
                IncomingInvoiceApprovalStateTransitionType.REJECT
        ));
        assertThat(rejectTransision.getTask()).isNull();

        IncomingInvoiceApprovalStateTransition nextPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(nextPendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.NEW,
                null,
                IncomingInvoiceApprovalStateTransitionType.COMPLETE
        ));
        Task nextPendingTask = nextPendingTransition.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER,
                null
        ));
        assertThat(nextPendingTask.getDescription()).isEqualTo("Complete (Sorry, but this is not good at all)");

        List<Task> uncompletedForTechnizio = taskRepository.findIncompleteByPersonAssignedTo(Person_enum.TechnizioAdvisorIt.findUsing(serviceRegistry2));
        assertThat(uncompletedForTechnizio).isEmpty();

    }

    @Test
    public void invoice_without_order_can_have_project() throws Exception {

        // given
        assertThat(incomingInvoice.getItems()).hasSize(1);
        final OrderItem orderItem = mixin(IncomingInvoiceItem_orderItem.class, incomingInvoice.getItems().first()).prop();
        assertThat(orderItem).isNull();

        // when
        Project project = Project_enum.GraProject.findUsing(serviceRegistry2);
        IncomingInvoiceItem item = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        item.setProject(project);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

    }

    @Test
    public void suspending_an_invoice_works() throws Exception {

        // given
        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(2);
        assertTransition(transitionsOfInvoice.get(0), new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.NEW,
                null,
                IncomingInvoiceApprovalStateTransitionType.COMPLETE
        ));
        assertThat(transitionsOfInvoice.get(0).getTask()).isNotNull();

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_suspend.class, incomingInvoice)).act("Do not pay for a while"));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.SUSPENDED);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(2); // still, no pending transition is created
        assertTransition(transitionsOfInvoice.get(0), new ExpectedTransitionResult(
                true,
                "crigatoni",
                IncomingInvoiceApprovalState.NEW,
                IncomingInvoiceApprovalState.SUSPENDED,
                IncomingInvoiceApprovalStateTransitionType.SUSPEND
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, "Time to pay now"));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);
        IncomingInvoiceApprovalStateTransition completedTransition = transitionsOfInvoice.get(1);
        assertTransition(completedTransition, new ExpectedTransitionResult(
                true,
                "crigatoni",
                IncomingInvoiceApprovalState.SUSPENDED,
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalStateTransitionType.COMPLETE
        ));
        IncomingInvoiceApprovalStateTransition pendingTransition = transitionsOfInvoice.get(0);
        assertTransition(pendingTransition, new ExpectedTransitionResult(
                false,
                null,
                IncomingInvoiceApprovalState.COMPLETED,
                null,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));
        assertThat(pendingTransition.getTask()).isNotNull();

    }

    private void assertTransition(IncomingInvoiceApprovalStateTransition transition, ExpectedTransitionResult result) {
        assertThat(transition.isCompleted()).isEqualTo(result.isCompleted());
        assertThat(transition.getCompletedBy()).isEqualTo(result.getCompletedBy());
        assertThat(transition.getFromState()).isEqualTo(result.getFromState());
        assertThat(transition.getToState()).isEqualTo(result.getToState());
        assertThat(transition.getTransitionType()).isEqualTo(result.getTransitionType());

    }

    @AllArgsConstructor
    @Getter
    private class ExpectedTransitionResult {
        private boolean completed;
        private String completedBy;
        private IncomingInvoiceApprovalState fromState;
        private IncomingInvoiceApprovalState toState;
        private IncomingInvoiceApprovalStateTransitionType transitionType;
    }

    private void assertTask(Task task, ExpectedTaskResult result) {
        assertThat(task.isCompleted()).isEqualTo(result.isCompleted());
        assertThat(task.getAssignedTo()).isEqualTo(partyRoleTypeRepository.findByKey(result.getAssignedTo().getKey()));
        assertThat(task.getPersonAssignedTo()).isEqualTo(result.getPersonAssignedTo());

        if (result.getTaskDescription() != null) {
            assertThat(task.getDescription()).isEqualTo(result.getTaskDescription());
        }
    }

    @AllArgsConstructor
    @Getter
    private class ExpectedTaskResult {

        public ExpectedTaskResult(final boolean completed, final IPartyRoleType assignedTo, final Person personAssignedTo) {
            this.completed = completed;
            this.assignedTo = assignedTo;
            this.personAssignedTo = personAssignedTo;
        }

        private boolean completed;
        private IPartyRoleType assignedTo;
        private Person personAssignedTo;
        private String taskDescription;
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    TaskRepository taskRepository;

}

