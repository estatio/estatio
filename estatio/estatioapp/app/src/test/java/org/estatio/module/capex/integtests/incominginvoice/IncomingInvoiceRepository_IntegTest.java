package org.estatio.module.capex.integtests.incominginvoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
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
    public void findUniquePaymentMethodsForSeller_works() throws Exception {
        // given
        IncomingInvoice invoice1 = createIncomingInvoice();

        // when
        List<PaymentMethod> paymentMethods = incomingInvoiceRepository.findUniquePaymentMethodsForSeller(invoice1.getSeller());

        // then
        assertThat(paymentMethods).hasSize(1);
        assertThat(paymentMethods).containsExactly(PaymentMethod.BANK_TRANSFER);

        // and given
        IncomingInvoice invoice2 = createIncomingInvoice();
        invoice2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);

        // and when
        paymentMethods = incomingInvoiceRepository.findUniquePaymentMethodsForSeller(invoice2.getSeller());

        // and then
        assertThat(paymentMethods).hasSize(2);
        assertThat(paymentMethods).containsExactlyInAnyOrder(PaymentMethod.BANK_TRANSFER, PaymentMethod.DIRECT_DEBIT);

        // and given
        IncomingInvoice invoice3 = createIncomingInvoice();
        invoice3.setPaymentMethod(null);

        // and when
        paymentMethods = incomingInvoiceRepository.findUniquePaymentMethodsForSeller(invoice3.getSeller());

        // and then
        assertThat(paymentMethods).hasSize(2);
        assertThat(paymentMethods).containsExactlyInAnyOrder(PaymentMethod.BANK_TRANSFER, PaymentMethod.DIRECT_DEBIT);
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

        final LocalDate vatRegistrationDate = null;
        final boolean postedToCodaBooks = false;
        final LocalDate paidDate = null;
        final IncomingInvoiceApprovalState approvalState = null;

        Property property = existingInvoice.getProperty();

        IncomingInvoice updatedInvoice = incomingInvoiceRepository.upsert(IncomingInvoiceType.CAPEX, invoiceNumber,
                property, updatedAtPath, updatedBuyer, seller, invoiceDate, updatedDueDate, vatRegistrationDate,
                updatedPaymentMethod, updatedStatus, updatedDateReceived, updatedBankAccount,
                approvalState, postedToCodaBooks, paidDate
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

    @Test
    public void UUID_is_created_and_finder_works() throws Exception {

        // given
        IncomingInvoice invoice1 = createIncomingInvoice();
        IncomingInvoice invoice2 = createIncomingInvoice();
        assertThat(invoice1.getUuid()).isNotNull();
        assertThat(invoice2.getUuid()).isNotNull();
        final String uuidInvoice1 = invoice1.getUuid();
        final String uuidInvoice2 = invoice2.getUuid();

        // when
        assertThat(incomingInvoiceRepository.findUnique(uuidInvoice1)).isEqualTo(invoice1);
        assertThat(incomingInvoiceRepository.findUnique(uuidInvoice2)).isEqualTo(invoice2);

    }

    private IncomingInvoice createIncomingInvoice() {
        seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        buyer = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
        property = Property_enum.OxfGb.findUsing(serviceRegistry);
        invoiceNumber = "123";
        invoiceDate = new LocalDate(2017, 1, 1);
        dueDate = invoiceDate.minusMonths(1);
        final LocalDate vatRegistrationDate = null;
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        invoiceStatus = InvoiceStatus.NEW;
        atPath = "/GBR";
        approvalStateIfAny = IncomingInvoiceApprovalState.PAID;

        final LocalDate dateReceived = null;
        final BankAccount bankAccount = null;
        final boolean postedToCodaBooks = false;
        final LocalDate paidDate = null;

        return incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate,
                vatRegistrationDate, paymentMethod, invoiceStatus, dateReceived, bankAccount,
                approvalStateIfAny, postedToCodaBooks, paidDate);
    }

    @Inject
    BankAccountRepository bankAccountRepository;

}
