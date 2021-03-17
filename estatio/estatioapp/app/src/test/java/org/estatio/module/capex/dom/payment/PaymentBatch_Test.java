package org.estatio.module.capex.dom.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.assertj.core.api.Assertions;
import org.jmock.auto.Mock;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

import iso.std.iso._20022.tech.xsd.pain_001_001.CreditTransferTransactionInformation10;
import static org.assertj.core.api.Assertions.assertThat;

public class PaymentBatch_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @JUnitRuleMockery2.Ignoring
    @Mock
    ServiceRegistry2 mockServiceRegistry2;

    PaymentBatch paymentBatch;

    private Party seller1;
    private Party seller2;
    private BankAccount seller1BankAccount;
    private BankAccount seller2BankAccount;

    @Before
    public void setUp() throws Exception {
        paymentBatch = new PaymentBatch() {
            @Override public String getId() {
                return "97834"; // say
            }
        };
        paymentBatch.setCreatedOn(new DateTime(2017,7,14,15,50));
        paymentBatch.setApprovalState(PaymentBatchApprovalState.NEW);
        paymentBatch.serviceRegistry2 = mockServiceRegistry2;
        paymentBatch.queryResultsCache = new QueryResultsCache() {
            @Override public <T> T execute(
                    final Callable<T> callable,
                    final Class<?> callingClass,
                    final String methodName,
                    final Object... keys) {
                try {
                    return callable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        this.seller1 = newOrganisation("ACME", "/FRA");
        this.seller2 = newOrganisation("YOUKEA", "/FRA");

        this.seller1BankAccount = newBankAccount("NL42RBOS0601349900", "INGBFRPP", seller1);
        this.seller2BankAccount = newBankAccount("NL46RABO0370675415", "BSPFFRPPXXX", seller2);

    }

    @Test
    public void aggregates_transfers_ok() throws Exception {

        // given
        final IncomingInvoice invoice1 =
                newInvoice(new LocalDate(2017, 7, 7), seller1, seller1BankAccount, "EUR", "361754.46", "AF3T2017", "/FRA");
        invoice1.setCommunicationNumber("+++111/222/333+++");
        final IncomingInvoice invoice2 =
                newInvoice(new LocalDate(2017, 6, 30), seller2, seller2BankAccount, "EUR", "15251.76", "DGD 11420 - 170522", "/FRA");
        final IncomingInvoice invoice3 =
                newInvoice(new LocalDate(2017, 6, 5), seller1, seller1BankAccount, "EUR", "-172805.79", "REDD2016VT", "/FRA");

        paymentBatch.addLineIfRequired(invoice1); // sequence = 1
        paymentBatch.addLineIfRequired(invoice2); // sequence = 2
        paymentBatch.addLineIfRequired(invoice3); // sequence = 3

        // then
        List<CreditTransfer> transfers = paymentBatch.getTransfers();
        
        assertThat(transfers).hasSize(2);
        final CreditTransfer transfer1 = transfers.get(0);
        final CreditTransfer transfer2 = transfers.get(1);

        assertThat(transfer1.getBatch()).isEqualTo(paymentBatch);
        assertThat(transfer1.getSellerName()).isEqualTo(seller1.getName());
        assertThat(transfer1.getAmount()).isEqualTo(invoice1.getGrossAmount().add(invoice3.getGrossAmount()));
        assertThat(transfer1.getEndToEndId()).isEqualTo("97834-1-3"); // the "1-3" suffix indicates payment lines with sequence 1 and 3 together
        assertThat(transfer1.getRemittanceInformation()).isEqualTo("AF3T2017 (+++111/222/333+++);REDD2016VT");
        assertThat(transfer1.getSellerBankAccount()).isEqualTo(seller1BankAccount);
        assertThat(transfer1.getSellerBic()).isEqualTo(seller1BankAccount.getBic());
        assertThat(transfer1.getSellerIban()).isEqualTo(seller1BankAccount.getIban());
        assertThat(transfer1.getSellerPostalAddressCountry()).isEqualTo("FR");

        assertThat(transfer2.getBatch()).isEqualTo(paymentBatch);
        assertThat(transfer2.getSellerName()).isEqualTo(seller2.getName());
        assertThat(transfer2.getAmount()).isEqualTo(invoice2.getGrossAmount());
        assertThat(transfer2.getEndToEndId()).isEqualTo("97834-2");
        assertThat(transfer2.getRemittanceInformation()).isEqualTo("DGD 11420 - 170522");
        assertThat(transfer2.getSellerBankAccount()).isEqualTo(seller2BankAccount);
        assertThat(transfer2.getSellerBic()).isEqualTo(seller2BankAccount.getBic());
        assertThat(transfer2.getSellerIban()).isEqualTo(seller2BankAccount.getIban());
        assertThat(transfer2.getSellerPostalAddressCountry()).isEqualTo("FR");

        // and also
        final CreditTransferTransactionInformation10 cdtTrf1 = transfer1.asXml();

        assertThat(cdtTrf1.getCdtr().getNm()).isEqualTo(transfer1.getSellerName());
        assertThat(cdtTrf1.getAmt().getInstdAmt().getValue()).isEqualTo(transfer1.getAmount());
        assertThat(cdtTrf1.getAmt().getInstdAmt().getCcy()).isEqualTo("EUR");
        assertThat(cdtTrf1.getPmtId().getEndToEndId()).isEqualTo(transfer1.getEndToEndId());
        assertThat(cdtTrf1.getRmtInf().getUstrds().get(0)).isEqualTo(transfer1.getRemittanceInformation());
        assertThat(cdtTrf1.getCdtrAgt().getFinInstnId().getBIC()).isEqualTo(transfer1.getSellerBic());
        assertThat(cdtTrf1.getCdtrAcct().getId().getIBAN()).isEqualTo(transfer1.getSellerIban());
        assertThat(cdtTrf1.getCdtr().getPstlAdr().getCtry()).isEqualTo(transfer1.getSellerPostalAddressCountry());
    }


    Organisation newOrganisation(final String reference, final String applicationTenancyPath) {
        final Organisation organisation = new Organisation() {
            @Override public String getAtPath() {
                return applicationTenancyPath;
            }
        };
        organisation.setReference(reference);
        organisation.setName(reference + " Organisation");
        organisation.setApplicationTenancyPath(applicationTenancyPath);
        return organisation;
    }

    BankAccount newBankAccount(final String iban, final String bic, final Party owner) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(iban);
        bankAccount.setBic(bic);
        bankAccount.setOwner(owner);
        return bankAccount;
    }

    IncomingInvoice newInvoice(
            final LocalDate invoiceDate,
            final Party seller,
            final BankAccount sellerBankAccount,
            final String currencyRef, final String grossAmount,
            final String invoiceNumber,
            final String atPath) {
        final IncomingInvoice invoice = new IncomingInvoice(){
            @Override public String getAtPath() {
                return atPath;
            }
        };
        invoice.setInvoiceDate(invoiceDate);
        invoice.setSeller(seller);
        invoice.setBankAccount(sellerBankAccount);
        invoice.setGrossAmount(new BigDecimal(grossAmount));
        invoice.setInvoiceNumber(invoiceNumber);
        final Currency currency = new Currency();
        currency.setReference(currencyRef);
        invoice.setCurrency(currency);
        return invoice;
    }

    @Test
    public void getUpstreamCreditNoteFound_works() throws Exception {

        // given
        PaymentBatch paymentBatchWithCreditNote = new PaymentBatch();
        PaymentBatch paymentBatchWithOutCreditNote = new PaymentBatch();
        PaymentLine paymentLineWithCreditNoteFound = new PaymentLine(){
            @Override
            public boolean getUpstreamCreditNoteFound(){
                return true;
            }

        };
        PaymentLine paymentLineWithoutCreditNoteFound = new PaymentLine(){
            @Override
            public boolean getUpstreamCreditNoteFound(){
                return false;
            }

        };
        paymentBatchWithCreditNote.getLines().add(paymentLineWithCreditNoteFound);
        paymentBatchWithCreditNote.getLines().add(paymentLineWithoutCreditNoteFound);

        paymentBatchWithOutCreditNote.getLines().add(paymentLineWithoutCreditNoteFound);

        // when, then
        Assertions.assertThat(paymentBatchWithCreditNote.getUpstreamCreditNoteFound()).isTrue();
        Assertions.assertThat(paymentBatchWithOutCreditNote.getUpstreamCreditNoteFound()).isFalse();

    }

    @Test
    public void construct_remittance_information_works() throws Exception {

        // given
        PaymentBatch paymentBatch = new PaymentBatch();
        IncomingInvoice invoiceFra = new IncomingInvoice(){
            @Override public String getAtPath() {
                return "/FRA";
            }
        };
        invoiceFra.setInvoiceNumber(" nr12345  ");

        // when, then
        final String justInvoiceNumberFra = paymentBatch.constructRemittanceInformation(invoiceFra);
        Assertions.assertThat(justInvoiceNumberFra).isEqualTo("nr12345");
        Assertions.assertThat(justInvoiceNumberFra).isEqualTo(invoiceFra.getInvoiceNumber().trim());

        // and when
        invoiceFra.setCommunicationNumber(" com34567  ");
        final String invoiceNumberAndCommunicationNumberFra = paymentBatch.constructRemittanceInformation(invoiceFra);
        // then
        Assertions.assertThat(invoiceNumberAndCommunicationNumberFra).isEqualTo("nr12345 (com34567)");

        // and when
        invoiceFra.setCommunicationNumber("    nr12345      ");
        final String invoiceNumberAndCommunicationNumberFraSimilar = paymentBatch.constructRemittanceInformation(invoiceFra);
        // then
        Assertions.assertThat(invoiceNumberAndCommunicationNumberFraSimilar).isEqualTo("nr12345");

        // and given
        IncomingInvoice invoiceBel = new IncomingInvoice(){
            @Override public String getAtPath() {
                return "/BEL";
            }
        };
        invoiceBel.setInvoiceNumber("  nr12345 ");

        // when, then
        final String justInvoiceNumberBel = paymentBatch.constructRemittanceInformation(invoiceBel);
        Assertions.assertThat(justInvoiceNumberBel).isEqualTo("nr12345");
        Assertions.assertThat(justInvoiceNumberBel).isEqualTo(invoiceBel.getInvoiceNumber().trim());

        // and when
        invoiceBel.setCommunicationNumber(" com34567   ");
        final String invoiceNumberAndCommunicationNumberBel = paymentBatch.constructRemittanceInformation(invoiceBel);
        // then
        Assertions.assertThat(invoiceNumberAndCommunicationNumberBel).isEqualTo("com34567 (nr12345)");

        // and when
        invoiceBel.setInvoiceNumber("     com34567    ");
        final String invoiceNumberAndCommunicationNumberBelSimilar = paymentBatch.constructRemittanceInformation(invoiceBel);
        // then
        Assertions.assertThat(invoiceNumberAndCommunicationNumberBelSimilar).isEqualTo("com34567");



    }

    @Test
    public void concatRemittanceInformation_works() throws Exception {

        // given
        PaymentBatch paymentBatch = new PaymentBatch();
        // when
        List<PaymentLine> lines = new ArrayList<>();
        // then
        Assertions.assertThat(paymentBatch.concatRemittanceInformationAndTruncateIfNeeded(lines)).isEqualTo("");

        // and when
        final PaymentLine line1 = new PaymentLine();
        line1.setRemittanceInformation("info1");
        lines.add(line1);
        // then
        Assertions.assertThat(paymentBatch.concatRemittanceInformationAndTruncateIfNeeded(lines)).isEqualTo("info1");

        // and when
        final PaymentLine line2 = new PaymentLine();
        line2.setRemittanceInformation("info2");
        lines.add(line2);
        // then
        Assertions.assertThat(paymentBatch.concatRemittanceInformationAndTruncateIfNeeded(lines)).isEqualTo("info1;info2");

    }

    @Test
    public void concatRemittanceInformation_works_when_exceeding_140_chars() throws Exception {

        // given
        PaymentBatch paymentBatch = new PaymentBatch();

        // when
        List<PaymentLine> lines = new ArrayList<>();
        final PaymentLine line = new PaymentLine();
        final String stringWith138Chars = "xxxxxxxxx1xxxxxxxxx2xxxxxxxxx3xxxxxxxxx4xxxxxxxxx5xxxxxxxxx6xxxxxxxxx7xxxxxxxxx8xxxxxxxxx9xxxxxxxx10xxxxxxxx11xxxxxxxx12xxxxxxxx13xxxxxxxx";
        Assertions.assertThat(stringWith138Chars.length()).isEqualTo(138);
        line.setRemittanceInformation(stringWith138Chars);
        lines.add(line);
       // then
        Assertions.assertThat(paymentBatch.concatRemittanceInformationAndTruncateIfNeeded(lines)).isEqualTo(stringWith138Chars);

        // and when
        final PaymentLine line2 = new PaymentLine();
        line2.setRemittanceInformation("YYY");
        lines.add(line2);

        // then
        // then
        Assertions.assertThat(paymentBatch.concatRemittanceInformationAndTruncateIfNeeded(lines)).isEqualTo(stringWith138Chars + ";Y");
        Assertions.assertThat(paymentBatch.concatRemittanceInformationAndTruncateIfNeeded(lines).length()).isEqualTo(140);

    }

}