package org.estatio.module.capex.integtests.invoice;

import java.math.BigDecimal;

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
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceApprovalStateIta_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForRon;
    Party buyer;
    Party seller;

    Country italy;
    Charge charge_for_works;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;
    Project project;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        IncomingInvoiceNoDocument_enum.invoiceForItaNoOrder,
                        Person_enum.CarmenIncomingInvoiceManagerIt,
                        Person_enum.FloellaAssetManagerIt
//                        Person_enum.BertrandIncomingInvoiceManagerFr,
//                        Person_enum.PeterPanProjectManagerFr,
//                        Person_enum.GabrielCountryDirectorFr
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
    public void approved_invoice_with_net_amount_higher_then_100000_threshold_does_not_need_further_approval() throws Exception {

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

