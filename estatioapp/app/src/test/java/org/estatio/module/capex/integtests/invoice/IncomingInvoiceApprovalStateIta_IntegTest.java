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
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCenterManager;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCountryDirector;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

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
                        Person_enum.RobertCountryDirectorIt
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

        // given
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(incomingInvoice.getNetAmount()).isEqualTo(new BigDecimal("100000.00"));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FloellaAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE_WHY??", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS);

    }

    @Test
    public void approved_invoice_with_net_amount_higher_then_100000_threshold_does_need_further_approval() throws Exception {

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

    }

    @Test
    public void invoice_approved_by_director_transitions_to_pending_in_coda_books() throws Exception {

        // given
        incomingInvoice.changeAmounts(new BigDecimal("100000.01"), new BigDecimal("122000.01"));
        IncomingInvoiceItem item = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        item.addAmounts(new BigDecimal("0.01"), BigDecimal.ZERO, new BigDecimal("0.01"));
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.CarmenIncomingInvoiceManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("INCOMING_INVOICE_MANAGER", null, null));
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FloellaAssetManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approve.class, incomingInvoice)).act("SOME_ROLE_WHY??", null, null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.RobertCountryDirectorIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, incomingInvoice)).act(null, false));

        // then
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PENDING_IN_CODA_BOOKS);

    }

    @Test
    public void recoverable_invoice_for_property_having_center_manager_needs_to_be_approved_by_center_manager() throws Exception {

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
        assertThat(pendingTransition.getFromState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(pendingTransition.getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER);
        assertThat(pendingTransition.getToState()).isNull();
        assertThat(pendingTransition.isCompleted()).isFalse();
        final Task pendingTransitionTask = pendingTransition.getTask();
        assertThat(pendingTransitionTask).isNotNull();
        assertThat(pendingTransitionTask.getAssignedTo()).isEqualTo(partyRoleTypeRepository.findByKey(FixedAssetRoleTypeEnum.CENTER_MANAGER.getKey()));
        assertThat(pendingTransitionTask.isCompleted()).isFalse();

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.IlicCenterManagerIt.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_approveAsCenterManager.class, recoverableInvoice)).act( null, null, false));

        // then
        assertThat(recoverableInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
        transitionsOfInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(recoverableInvoice);
        assertThat(transitionsOfInvoice).hasSize(4);
        final IncomingInvoiceApprovalStateTransition lastCompletedTransition = transitionsOfInvoice.get(1);
        assertThat(lastCompletedTransition.isCompleted()).isTrue();
        assertThat(lastCompletedTransition.getCompletedBy()).isEqualTo("iresponsabile");
        final IncomingInvoiceApprovalStateTransition newPendingTransition = transitionsOfInvoice.get(0);
        assertThat(newPendingTransition.isCompleted()).isFalse();
        assertThat(newPendingTransition.getFromState()).isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
        assertThat(newPendingTransition.getTransitionType()).isEqualTo(IncomingInvoiceApprovalStateTransitionType.APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER);

    }


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

