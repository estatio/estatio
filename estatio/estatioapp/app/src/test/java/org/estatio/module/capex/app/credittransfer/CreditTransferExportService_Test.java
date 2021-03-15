package org.estatio.module.capex.app.credittransfer;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.payment.CreditTransfer;
import org.estatio.module.capex.dom.payment.PaymentLine;

public class CreditTransferExportService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceRepository mockIncomingInvoiceRepository;

    @Test
    public void isFirstUseBankAccount_works_when_just_invoices_in_transfer_found() throws Exception {

        // given
        CreditTransferExportService service = new CreditTransferExportService();
        service.incomingInvoiceRepository = mockIncomingInvoiceRepository;
        CreditTransfer transfer = new CreditTransfer();

        // when
        IncomingInvoice invoiceInPaymentLine1 = new IncomingInvoice();
        IncomingInvoice invoiceInPaymentLine2 = new IncomingInvoice();
        PaymentLine line1 = new PaymentLine();
        line1.setInvoice(invoiceInPaymentLine1);
        PaymentLine line2 = new PaymentLine();
        line2.setInvoice(invoiceInPaymentLine2);
        transfer.setLines(Arrays.asList(line1, line2));

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).findByBankAccount(transfer.getSellerBankAccount());
            // when ..
            will(returnValue(Arrays.asList(invoiceInPaymentLine1, invoiceInPaymentLine2)));
        }});

        // then
        Assertions.assertThat(service.isFirstUseBankAccount(transfer)).isTrue();


    }

    @Test
    public void isFirstUseBankAccount_works_when_other_invoices_than_in_transfer_found() throws Exception {

        // given
        CreditTransferExportService service = new CreditTransferExportService();
        service.incomingInvoiceRepository = mockIncomingInvoiceRepository;
        CreditTransfer transfer = new CreditTransfer();

        // when
        IncomingInvoice invoiceInPaymentLine1 = new IncomingInvoice();
        IncomingInvoice invoiceInPaymentLine2 = new IncomingInvoice();
        PaymentLine line1 = new PaymentLine();
        line1.setInvoice(invoiceInPaymentLine1);
        PaymentLine line2 = new PaymentLine();
        line2.setInvoice(invoiceInPaymentLine2);
        transfer.setLines(Arrays.asList(line1, line2));

        IncomingInvoice otherInvoice = new IncomingInvoice();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).findByBankAccount(transfer.getSellerBankAccount());
            // when ..
            will(returnValue(Arrays.asList(invoiceInPaymentLine1, invoiceInPaymentLine2, otherInvoice)));
        }});

        // then
        Assertions.assertThat(service.isFirstUseBankAccount(transfer)).isFalse();


    }

    @Test
    public void isFirstUseBankAccount_works_when_no_incoming_invoices_found_for_bank_account() throws Exception {

        // given
        CreditTransferExportService service = new CreditTransferExportService();
        service.incomingInvoiceRepository = mockIncomingInvoiceRepository;
        CreditTransfer transfer = new CreditTransfer();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).findByBankAccount(transfer.getSellerBankAccount());
            // when
            will(returnValue(Arrays.asList()));
        }});

        // then
        Assertions.assertThat(service.isFirstUseBankAccount(transfer)).isTrue();

    }

    @Test
    public void getApprovalStateTransitionSummary_works() throws Exception {

        // given
        CreditTransferExportService service = new CreditTransferExportService();
        IncomingInvoice invoice = new IncomingInvoice(){
            @Override
            public List<ApprovalString> getApprovals() {
                return Arrays.asList(new ApprovalString("some manager", "2017-01-01", LocalDate.parse("2017-01-01")));
            }
        };

        // when
        String result = service.getApprovalStateTransitionSummary(invoice);

        // then
        Assertions.assertThat(result).isEqualTo("some manager");

    }

}