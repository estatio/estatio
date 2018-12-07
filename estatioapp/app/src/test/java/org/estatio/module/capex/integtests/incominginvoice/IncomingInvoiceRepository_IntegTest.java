package org.estatio.module.capex.integtests.incominginvoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.fixtures.enums.BankAccountFaFa_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;
    private IncomingInvoiceApprovalState approvalStateIfAny;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
                executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldGb.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, BankAccount_enum.HelloWorldNl.builder());
                executionContext.executeChild(this, BankAccountFaFa_enum.HelloWorldNl.builder());
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
    public void findInvoicesPayableByBankTransferWithDifferentHistoricalPaymentMethods_works() throws Exception {
        // given
        IncomingInvoice invoice1 = createIncomingInvoice();
        invoice1.setInvoiceNumber("001");
        invoice1.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        IncomingInvoice invoice2 = createIncomingInvoice();
        invoice2.setApprovalState(IncomingInvoiceApprovalState.PAYABLE);

        // when
        List<IncomingInvoice> historicallyDifferentPaymentMethods = incomingInvoiceRepository.findInvoicesPayableByBankTransferWithDifferentHistoricalPaymentMethods(dueDate.minusDays(1), dueDate.plusDays(1), "/GBR");

        // then
        assertThat(historicallyDifferentPaymentMethods).containsExactly(invoice2);

        // and given
        invoice1.setPaymentMethod(PaymentMethod.BANK_TRANSFER);

        // when
        historicallyDifferentPaymentMethods = incomingInvoiceRepository.findInvoicesPayableByBankTransferWithDifferentHistoricalPaymentMethods(dueDate.minusDays(1), dueDate.plusDays(1), "/GBR");

        // then
        assertThat(historicallyDifferentPaymentMethods).isEmpty();
    }

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
        Party updatedBuyer = Organisation_enum.HelloWorldNl.findUsing(serviceRegistry);
        LocalDate updatedDueDate = dueDate.minusWeeks(1);
        PaymentMethod updatedPaymentMethod = PaymentMethod.DIRECT_DEBIT;
        InvoiceStatus updatedStatus = InvoiceStatus.INVOICED;
        LocalDate updatedDateReceived = new LocalDate(2017, 1, 2);
        BankAccount updatedBankAccount = bankAccountRepository.allBankAccounts().get(0);

        Property property = existingInvoice.getProperty();

        IncomingInvoice updatedInvoice = incomingInvoiceRepository.upsert(IncomingInvoiceType.CAPEX, invoiceNumber,
                property, updatedAtPath, updatedBuyer, seller, invoiceDate, updatedDueDate, updatedPaymentMethod, updatedStatus, updatedDateReceived, updatedBankAccount,
                null, null
        );

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

    private IncomingInvoice createIncomingInvoice() {
        seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        buyer = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
        property = Property_enum.OxfGb.findUsing(serviceRegistry);
        invoiceNumber = "123";
        invoiceDate = new LocalDate(2017, 1, 1);
        dueDate = invoiceDate.minusMonths(1);
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        invoiceStatus = InvoiceStatus.NEW;
        atPath = "/GBR";
        approvalStateIfAny = IncomingInvoiceApprovalState.PAID;

        return incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, null, null,
                approvalStateIfAny, null);
    }

    @Inject
    PartyRepository partyRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

}
