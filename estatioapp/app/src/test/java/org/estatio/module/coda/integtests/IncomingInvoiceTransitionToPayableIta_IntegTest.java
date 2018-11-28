package org.estatio.module.coda.integtests;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
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
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
import org.estatio.module.capex.integtests.invoice.IncomingInvoiceApprovalState_IntegTest;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceTransitionToPayableIta_IntegTest extends CodaModuleIntegTestAbstract {

    Property propertyForRon;
    Party buyer;
    Party seller;

    Country italy;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        IncomingInvoiceNoDocument_enum.invoiceForItaNoOrder,
                        Person_enum.CarmenIncomingInvoiceManagerIt,
                        Person_enum.FloellaAssetManagerIt,
                        Person_enum.FlorisAssetManagerIt,
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


        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);

    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Test
    @Ignore
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

    /**
     * the opposite is tested {@link IncomingInvoiceApprovalState_IntegTest#payable_non_italian_invoice_can_be_rejected}
     */
    @Test
    public void payable_italian_invoice_cannot_be_rejected() throws Exception {
        //TODO: since at the moment we have no route to payable for Italian invoices yet

        // given
        CodaDocHead docHead = new CodaDocHead();
        docHead.setIncomingInvoice(incomingInvoice);
        docHead.setLocation("books");
        codaDocHeadRepository.persistAsReplacementIfRequired(docHead);
        assertThat(codaDocHeadRepository.findByIncomingInvoice(incomingInvoice)).isSameAs(docHead);



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

    @Inject
    CodaDocHeadRepository codaDocHeadRepository;

}

