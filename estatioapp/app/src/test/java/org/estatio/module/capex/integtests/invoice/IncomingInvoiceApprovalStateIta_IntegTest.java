package org.estatio.module.capex.integtests.invoice;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

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
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCenterManager;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCountryDirector;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveWhenApprovedByCenterManager;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
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

    BankAccount bankAccount;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        IncomingInvoiceNoDocument_enum.invoiceForItaNoOrder,
                        IncomingInvoiceNoDocument_enum.invoiceForItaRecoverable,
                        Person_enum.CarmenIncomingInvoiceManagerIt,
                        Person_enum.IlicCenterManagerIt,
                        Person_enum.FloellaAssetManagerIt,
                        Person_enum.FlorisAssetManagerIt,
                        Person_enum.RobertCountryDirectorIt,
                        Person_enum.SergioPreferredCountryDirectorIt,
                        Person_enum.FabrizioPreferredManagerIt,
                        Organisation_enum.IncomingBuyerIt
                );
            }
        });

    }

    @Before
    public void setUp() {
        propertyForRon = Property_enum.RonIt.findUsing(serviceRegistry);

        buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);
        seller = Organisation_enum.TopModelIt.findUsing(serviceRegistry);

        italy = countryRepository.findCountry(Country_enum.ITA.getRef3());

        bankAccount = BankAccount_enum.TopModelIt.findUsing(serviceRegistry);

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("12345", seller, new LocalDate(2017,12,20));
        incomingInvoice.setBankAccount(bankAccount);

        recoverableInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("123456", seller, new LocalDate(2017,12,20));
        recoverableInvoice.setBankAccount(bankAccount);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);

        assertThat(recoverableInvoice).isNotNull();
        assertThat(recoverableInvoice.getApprovalState()).isNotNull();
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Test
    public void approved_invoice_with_net_amount_equal_or_lower_then_100000_threshold_does_not_need_further_approval() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(2);
        IncomingInvoiceApprovalStateTransition nextpending = transitionsOfInvoice.get(0);
        Task nextPendingTask = nextpending.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER,
                null        // task assigned to Role only, not to person
        ));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getNetAmount()).isEqualTo(new BigDecimal("100000.00"));

        // then
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);
        nextpending = transitionsOfInvoice.get(0);
        nextPendingTask = nextpending.getTask();
        assertTask(nextPendingTask, new ExpectedTaskResult(
                false,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FloellaAssetManagerIt.findUsing(serviceRegistry2)
        ));

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FloellaAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE_WHY??", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition completedByAssetManager = transitionsOfInvoice.get(1);
        assertTransition(completedByAssetManager, new ExpectedTransitionRestult(
                true,
                "fgestore",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalStateTransitionType.APPROVE
        ));

        final Task taskForAssetManager = completedByAssetManager.getTask();
        assertTask(taskForAssetManager, new ExpectedTaskResult(
                true,
                partyRoleTypeRepository.findByKey(FixedAssetRoleTypeEnum.ASSET_MANAGER.getKey()),
                Person_enum.FloellaAssetManagerIt.findUsing(serviceRegistry2)
        ));

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(0);
        assertTransition(lastAutomatic, new ExpectedTransitionRestult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS_WHEN_APPROVED
        ));

    }

    @Test
    public void approved_invoice_with_net_amount_higher_then_100000_threshold_needs_directors_approval() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        incomingInvoice.changeAmounts(new BigDecimal("100000.01"), new BigDecimal("122000.01"));
        IncomingInvoiceItem item = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        item.addAmounts(new BigDecimal("0.01"), BigDecimal.ZERO, new BigDecimal("0.01"));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FloellaAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE_WHY??", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED);

        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition completedByAssetManager = transitionsOfInvoice.get(1);
        assertTransition(completedByAssetManager,
                new ExpectedTransitionRestult(true,
                        "fgestore",
                        IncomingInvoiceApprovalState.COMPLETED,
                        IncomingInvoiceApprovalState.APPROVED,
                        IncomingInvoiceApprovalStateTransitionType.APPROVE));

        final Task taskForAssetManager = completedByAssetManager.getTask();
        assertTask(taskForAssetManager, new ExpectedTaskResult(
                true,
                FixedAssetRoleTypeEnum.ASSET_MANAGER,
                Person_enum.FloellaAssetManagerIt.findUsing(serviceRegistry2)
        ));

        final IncomingInvoiceApprovalStateTransition nextPending = transitionsOfInvoice.get(0);
        assertTransition(nextPending,
                new ExpectedTransitionRestult(false,
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
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        final IncomingInvoiceApprovalStateTransition completedByDirector = transitionsOfInvoice.get(1);
        assertTransition(completedByDirector, new ExpectedTransitionRestult(
                true,
                "rstracciatella",
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));
        assertThat(completedByDirector.getTask().getCompletedBy()).isEqualTo("rstracciatella");

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(0);
        assertTransition(lastAutomatic, new ExpectedTransitionRestult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS
        ));

    }

    @Test
    public void recoverable_invoice_for_property_having_center_manager_needs_to_be_approved_by_center_manager_when_NOT_over_100000() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, recoverableInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(recoverableInvoice.getNetAmount()).isEqualTo(new BigDecimal("100000.00"));
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(3);

        final IncomingInvoiceApprovalStateTransition pendingTransition = transitionsOfInvoice.get(0);
        assertTransition(pendingTransition, new ExpectedTransitionRestult(
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
                wrap(mixin(IncomingInvoice_approveAsCenterManager.class, recoverableInvoice)).act( null, null, false));

        // then
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition lastCompletedTransition = transitionsOfInvoice.get(1);
        assertTransition(lastCompletedTransition, new ExpectedTransitionRestult(
                true,
                "iresponsabile",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER
        ));

        assertThat(lastCompletedTransition.isCompleted()).isTrue();
        assertThat(lastCompletedTransition.getCompletedBy()).isEqualTo("iresponsabile");

        final IncomingInvoiceApprovalStateTransition newPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(newPendingTransition, new ExpectedTransitionRestult(
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
                wrap(mixin(IncomingInvoice_approveWhenApprovedByCenterManager.class, recoverableInvoice)).act( null, null, false));

        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        final IncomingInvoiceApprovalStateTransition completedByAssetManager = transitionsOfInvoice.get(1);
        assertTransition(completedByAssetManager, new ExpectedTransitionRestult(
                true,
                "fgestore",
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER
        ));

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(0);
        assertTransition(lastAutomatic, new ExpectedTransitionRestult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED,
                IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS_WHEN_APPROVED
        ));

    }

    @Test
    public void recoverable_invoice_for_property_having_center_manager_needs_to_be_approved_by_center_manager_when_over_100000() throws Exception {

        List<IncomingInvoiceApprovalStateTransition> transitionsOfInvoice;

        // given
        recoverableInvoice.changeAmounts(new BigDecimal("100000.01"), new BigDecimal("122000.01"));
        IncomingInvoiceItem item = (IncomingInvoiceItem) recoverableInvoice.getItems().first();
        item.addAmounts(new BigDecimal("0.01"), BigDecimal.ZERO, new BigDecimal("0.01"));

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, recoverableInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.IlicCenterManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCenterManager.class, recoverableInvoice)).act( null, null, false));

        // then
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);

        final IncomingInvoiceApprovalStateTransition lastCompletedTransition = transitionsOfInvoice.get(1);
        assertTransition(lastCompletedTransition, new ExpectedTransitionRestult(
                true,
                "iresponsabile",
                IncomingInvoiceApprovalState.COMPLETED,
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER
        ));

        final IncomingInvoiceApprovalStateTransition newPendingTransition = transitionsOfInvoice.get(0);
        assertTransition(newPendingTransition, new ExpectedTransitionRestult(
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
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, recoverableInvoice)).act( null, false));

        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        final IncomingInvoiceApprovalStateTransition completedByInvoiceApprovalDirector = transitionsOfInvoice.get(1);
        assertTransition(completedByInvoiceApprovalDirector, new ExpectedTransitionRestult(
                true,
                "sgalati",
                IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
        ));

        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(0);
        assertTransition(lastAutomatic, new ExpectedTransitionRestult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS
        ));

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
        incomingInvoice.changeAmounts(new BigDecimal("100000.01"), new BigDecimal("122000.01"));
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
        assertTransition(nextPending, new ExpectedTransitionRestult(
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
        assertTransition(lastCompleted, new ExpectedTransitionRestult(
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
        assertTransition(nextPending, new ExpectedTransitionRestult(
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
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act( null, true));

        // then
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(incomingInvoice);
        assertThat(transitionsOfInvoice).hasSize(5);

        lastCompleted = transitionsOfInvoice.get(1);
        assertTransition(lastCompleted, new ExpectedTransitionRestult(
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
        final IncomingInvoiceApprovalStateTransition lastAutomatic = transitionsOfInvoice.get(0);
        assertTransition(lastAutomatic, new ExpectedTransitionRestult(
                true,
                null,
                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS,
                IncomingInvoiceApprovalStateTransitionType.CHECK_IN_CODA_BOOKS
        ));

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
                Person_enum.FloellaAssetManagerIt.findUsing(serviceRegistry2)
        ));


    }


    /**
     * the opposite is tested {@link IncomingInvoiceApprovalState_IntegTest#payable_non_italian_invoice_can_be_rejected}
     */
    @Test
    public void payable_italian_invoice_cannot_be_rejected() throws Exception {
        //TODO: since at the moment we have no route to payable for Italian invoices yet
    }

    private void assertTransition(IncomingInvoiceApprovalStateTransition transition, ExpectedTransitionRestult result){
        assertThat(transition.isCompleted()).isEqualTo(result.isCompleted());
        assertThat(transition.getCompletedBy()).isEqualTo(result.getCompletedBy());
        assertThat(transition.getFromState()).isEqualTo(result.getFromState());
        assertThat(transition.getToState()).isEqualTo(result.getToState());
        assertThat(transition.getTransitionType()).isEqualTo(result.getTransitionType());

    }

    @AllArgsConstructor
    @Getter
    private class ExpectedTransitionRestult {
        private boolean completed;
        private String completedBy;
        private IncomingInvoiceApprovalState fromState;
        private IncomingInvoiceApprovalState toState;
        private IncomingInvoiceApprovalStateTransitionType transitionType;
    }

    private void assertTask(Task task, ExpectedTaskResult result){
        assertThat(task.isCompleted()).isEqualTo(result.isCompleted());
        assertThat(task.getAssignedTo()).isEqualTo(partyRoleTypeRepository.findByKey(result.getAssignedTo().getKey()));
        assertThat(task.getPersonAssignedTo()).isEqualTo(result.getPersonAssignedTo());
    }

    @AllArgsConstructor
    @Getter
    private class ExpectedTaskResult {
        private boolean completed;
        private IPartyRoleType assignedTo;
        private Person personAssignedTo;
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

}

