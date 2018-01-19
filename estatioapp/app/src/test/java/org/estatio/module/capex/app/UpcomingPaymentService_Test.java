package org.estatio.module.capex.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.app.invoice.UpcomingPaymentTotal;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class UpcomingPaymentService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock IncomingInvoiceRepository mockIncomingInvoiceRepository;

    @Test
    public void getUpcomingPayments_works_with_no_invoice_found() throws Exception {

        // given
        UpcomingPaymentService service = new UpcomingPaymentService();
        service.incomingInvoiceRepository = mockIncomingInvoiceRepository;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.COMPLETED, PaymentMethod.BANK_TRANSFER);
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.APPROVED, PaymentMethod.BANK_TRANSFER);
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK, PaymentMethod.BANK_TRANSFER);
        }});

        // when
        service.getUpcomingPayments();

    }

    @Mock DebtorBankAccountService mockDebtorBankAccountService;

    @Test
    public void getUpcomingPayments_works_when_no_bankAccount_found() throws Exception {

        // given
        UpcomingPaymentService service = new UpcomingPaymentService();
        service.incomingInvoiceRepository = mockIncomingInvoiceRepository;
        service.debtorBankAccountService = mockDebtorBankAccountService;

        IncomingInvoice completedInvoice = new IncomingInvoice();
        completedInvoice.setApprovalState(IncomingInvoiceApprovalState.COMPLETED);
        final BigDecimal amountForCompleted = new BigDecimal("1234.56");
        completedInvoice.setGrossAmount(amountForCompleted);

        IncomingInvoice approvedInvoice = new IncomingInvoice();
        approvedInvoice.setApprovalState(IncomingInvoiceApprovalState.APPROVED);
        final BigDecimal amountForApproved = new BigDecimal("1000.00");
        approvedInvoice.setGrossAmount(amountForApproved);

        IncomingInvoice pendingInvoice = new IncomingInvoice();
        pendingInvoice.setApprovalState(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
        final BigDecimal amountForPending = new BigDecimal("2000.00");
        pendingInvoice.setGrossAmount(amountForPending);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.COMPLETED, PaymentMethod.BANK_TRANSFER);
            will(returnValue(Arrays.asList(completedInvoice)));
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.APPROVED, PaymentMethod.BANK_TRANSFER);
            will(returnValue(Arrays.asList(approvedInvoice)));
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK, PaymentMethod.BANK_TRANSFER);
            will(returnValue(Arrays.asList(pendingInvoice)));

            allowing(mockDebtorBankAccountService).uniqueDebtorAccountToPay(completedInvoice);
            will(returnValue(null));
            allowing(mockDebtorBankAccountService).uniqueDebtorAccountToPay(approvedInvoice);
            will(returnValue(null));
            allowing(mockDebtorBankAccountService).uniqueDebtorAccountToPay(pendingInvoice);
            will(returnValue(null));
        }});

        // when
        List<UpcomingPaymentTotal> result = service.getUpcomingPayments();

        // then
        assertThat(result.size()).isEqualTo(1);
        UpcomingPaymentTotal totalWithoutBankAccount = result.get(0);
        assertThat(totalWithoutBankAccount.getDebtorBankAccount()).isNull();
        assertThat(totalWithoutBankAccount.getUpcomingAmountForCompleted()).isEqualTo(amountForCompleted);
        assertThat(totalWithoutBankAccount.getUpcomingAmountForApprovedByManager()).isEqualTo(amountForApproved);
        assertThat(totalWithoutBankAccount.getUpcomingAmountForPendingBankAccountCheck()).isEqualTo(amountForPending);
        assertThat(totalWithoutBankAccount.getTotalUpcomingAmount()).isEqualTo(new BigDecimal("4234.56"));

    }

    @Test
    public void getUpcomingPayments_works_when_bankAccount_found() throws Exception {

        // given
        UpcomingPaymentService service = new UpcomingPaymentService();
        service.incomingInvoiceRepository = mockIncomingInvoiceRepository;
        service.debtorBankAccountService = mockDebtorBankAccountService;

        IncomingInvoice completedInvoice = new IncomingInvoice();
        completedInvoice.setApprovalState(IncomingInvoiceApprovalState.COMPLETED);
        final BigDecimal amountForCompleted = new BigDecimal("1234.56");
        completedInvoice.setGrossAmount(amountForCompleted);

        IncomingInvoice approvedInvoice = new IncomingInvoice();
        approvedInvoice.setApprovalState(IncomingInvoiceApprovalState.APPROVED);
        final BigDecimal amountForApproved = new BigDecimal("1000.00");
        approvedInvoice.setGrossAmount(amountForApproved);

        BankAccount bankAccountForPending = new BankAccount();
        IncomingInvoice pendingInvoice = new IncomingInvoice();
        pendingInvoice.setApprovalState(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
        final BigDecimal amountForPending = new BigDecimal("2000.00");
        pendingInvoice.setGrossAmount(amountForPending);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.COMPLETED, PaymentMethod.BANK_TRANSFER);
            will(returnValue(Arrays.asList(completedInvoice)));
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.APPROVED, PaymentMethod.BANK_TRANSFER);
            will(returnValue(Arrays.asList(approvedInvoice)));
            oneOf(mockIncomingInvoiceRepository).findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK, PaymentMethod.BANK_TRANSFER);
            will(returnValue(Arrays.asList(pendingInvoice)));

            allowing(mockDebtorBankAccountService).uniqueDebtorAccountToPay(completedInvoice);
            will(returnValue(null));
            allowing(mockDebtorBankAccountService).uniqueDebtorAccountToPay(approvedInvoice);
            will(returnValue(null));
            allowing(mockDebtorBankAccountService).uniqueDebtorAccountToPay(pendingInvoice);
            will(returnValue(bankAccountForPending));
        }});

        // when
        List<UpcomingPaymentTotal> result = service.getUpcomingPayments();

        // then
        assertThat(result.size()).isEqualTo(2);
        UpcomingPaymentTotal totalWithoutBankAccount = result.get(0);
        assertThat(totalWithoutBankAccount.getDebtorBankAccount()).isNull();
        assertThat(totalWithoutBankAccount.getUpcomingAmountForCompleted()).isEqualTo(amountForCompleted);
        assertThat(totalWithoutBankAccount.getUpcomingAmountForApprovedByManager()).isEqualTo(amountForApproved);
        assertThat(totalWithoutBankAccount.getUpcomingAmountForPendingBankAccountCheck()).isNull();
        assertThat(totalWithoutBankAccount.getTotalUpcomingAmount()).isEqualTo(new BigDecimal("2234.56"));

        UpcomingPaymentTotal totalWithBankAccount = result.get(1);
        assertThat(totalWithBankAccount.getDebtorBankAccount()).isEqualTo(bankAccountForPending);
        assertThat(totalWithBankAccount.getUpcomingAmountForCompleted()).isNull();
        assertThat(totalWithBankAccount.getUpcomingAmountForApprovedByManager()).isNull();
        assertThat(totalWithBankAccount.getUpcomingAmountForPendingBankAccountCheck()).isEqualTo(amountForPending);
        assertThat(totalWithBankAccount.getTotalUpcomingAmount()).isEqualTo(amountForPending);

    }


}