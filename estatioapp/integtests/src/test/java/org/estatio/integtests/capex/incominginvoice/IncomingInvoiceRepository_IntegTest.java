package org.estatio.integtests.capex.incominginvoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.bankaccount.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.financial.BankAccountForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;
    private IncomingInvoiceApprovalState approvalStateIfAny;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForTopModelGb());
                executionContext.executeChild(this, new OrganisationForHelloWorldGb());
                executionContext.executeChild(this, new PropertyForOxfGb());
                executionContext.executeChild(this, new BankAccountForHelloWorldNl());
            }
        });
    }


    Party seller;
    Party buyer;
    String invoiceNumber;
    String atPath;
    LocalDate invoiceDate;
    LocalDate dueDate;
    PaymentMethod paymentMethod;
    InvoiceStatus invoiceStatus;
    Property property;

    @Test
    public void findByInvoiceNumberAndSellerAndInvoiceDate_works() throws Exception {

        // given
        IncomingInvoice invoice = createIncomingInvoice();

        // when
        IncomingInvoice invoiceFound = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate(invoiceNumber, seller, invoiceDate);

        // then
        assertThat(invoiceFound).isEqualTo(invoice);

    }

    @Test
    public void upsert_works() throws Exception {

        // given
        IncomingInvoice existingInvoice = createIncomingInvoice();

        IncomingInvoice invoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate(invoiceNumber, seller, invoiceDate);
        assertThat(invoice.getInvoiceNumber()).isEqualTo(invoiceNumber);
        assertThat(invoice.getAtPath()).isEqualTo(atPath);
        assertThat(invoice.getBuyer()).isEqualTo(buyer);
        assertThat(invoice.getDueDate()).isEqualTo(dueDate);
        assertThat(invoice.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(invoice.getStatus()).isEqualTo(invoiceStatus);
        assertThat(invoice.getDateReceived()).isNull();
        assertThat(invoice.getBankAccount()).isNull();

        // when
        String updatedAtPath = "/NLD";
        Party updatedBuyer = partyRepository.findPartyByReference(OrganisationForHelloWorldNl.REF);
        LocalDate updatedDueDate = dueDate.minusWeeks(1);
        PaymentMethod updatedPaymentMethod = PaymentMethod.DIRECT_DEBIT;
        InvoiceStatus updatedStatus = InvoiceStatus.INVOICED;
        LocalDate updatedDateReceived = new LocalDate(2017,1,2);
        BankAccount updatedBankAccount = bankAccountRepository.allBankAccounts().get(0);

        Property property = existingInvoice.getProperty();

        IncomingInvoice updatedInvoice = incomingInvoiceRepository.upsert(IncomingInvoiceType.CAPEX, invoiceNumber,
                property, updatedAtPath, updatedBuyer, seller, invoiceDate, updatedDueDate, updatedPaymentMethod, updatedStatus, updatedDateReceived, updatedBankAccount,
                null);

        // then
        assertThat(updatedInvoice.getInvoiceNumber()).isEqualTo(invoiceNumber);
        assertThat(updatedInvoice.getSeller()).isEqualTo(seller);
        assertThat(updatedInvoice.getInvoiceDate()).isEqualTo(invoiceDate);
        assertThat(updatedInvoice.getAtPath()).isEqualTo(updatedAtPath);
        assertThat(updatedInvoice.getBuyer()).isEqualTo(updatedBuyer);
        assertThat(updatedInvoice.getDueDate()).isEqualTo(updatedDueDate);
        assertThat(updatedInvoice.getPaymentMethod()).isEqualTo(updatedPaymentMethod);
        assertThat(updatedInvoice.getStatus()).isEqualTo(updatedStatus);
        assertThat(updatedInvoice.getDateReceived()).isEqualTo(updatedDateReceived);
        assertThat(updatedInvoice.getBankAccount()).isEqualTo(updatedBankAccount);

    }

    private IncomingInvoice createIncomingInvoice(){
        seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
        buyer = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
        property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        invoiceNumber = "123";
        invoiceDate = new LocalDate(2017,1,1);
        dueDate = invoiceDate.minusMonths(1);
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        invoiceStatus = InvoiceStatus.NEW;
        atPath = "/GBR";
        approvalStateIfAny = IncomingInvoiceApprovalState.PAID;

        return incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, null,null,
                approvalStateIfAny);
    }

    @Inject
    PartyRepository partyRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

}
