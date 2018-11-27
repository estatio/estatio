package org.estatio.module.capex.integtests.invoice;

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
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoiceNoDocument_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceApprovalStateItaMin_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForRon;
    Party buyer;
    Party seller;

    Country italy;

    IncomingInvoice invoiceForDirectDebit;

    BankAccount bankAccount;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        IncomingInvoiceNoDocument_enum.invoiceForItaDirectDebit
                );
            }
        });
        transactionService.nextTransaction();

    }

    @Before
    public void setUp() {
        propertyForRon = Property_enum.RonIt.findUsing(serviceRegistry);

        buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);
        seller = Organisation_enum.TopModelIt.findUsing(serviceRegistry);

        italy = countryRepository.findCountry(Country_enum.ITA.getRef3());

        bankAccount = BankAccount_enum.TopModelIt.findUsing(serviceRegistry);


        invoiceForDirectDebit = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("1234567", seller, new LocalDate(2017,12,20));
        invoiceForDirectDebit.setBankAccount(bankAccount);
        assertThat(invoiceForDirectDebit.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.AUTO_PAYABLE);


    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);



    @Test
    public void invoice_having_payment_method_other_then_bank_transfer_are_automatically_approved() throws Exception {

        // given
        // when
        // then
        List<IncomingInvoiceApprovalStateTransition> transitionsOfDirectDebitInvoice = incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceForDirectDebit);
        assertThat(transitionsOfDirectDebitInvoice).hasSize(2);

        transactionService.nextTransaction();
//        invoiceForDirectDebit = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("1234567", seller, new LocalDate(2017,12,20));
        assertThat(invoiceForDirectDebit.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.AUTO_PAYABLE);

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

