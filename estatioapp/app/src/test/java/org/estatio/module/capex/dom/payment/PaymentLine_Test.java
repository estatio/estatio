package org.estatio.module.capex.dom.payment;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.financial.dom.BankAccount;

public class PaymentLine_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceRepository mockIncomingInvoiceRepository;

    @Test
    public void getUpstreamCreditNoteFound_works() throws Exception {

        // given
        PaymentLine paymentLine = new PaymentLine();
        paymentLine.incomingInvoiceRepository = mockIncomingInvoiceRepository;
        BankAccount creditorBankAccount = new BankAccount();
        paymentLine.setCreditorBankAccount(creditorBankAccount);
        IncomingInvoice invoice = new IncomingInvoice();

        // expect
        context.checking(new Expectations(){{
            allowing(mockIncomingInvoiceRepository).findByBankAccount(creditorBankAccount);
            will(returnValue(Arrays.asList(invoice)));
        }});

        // when
        invoice.setApprovalState(IncomingInvoiceApprovalState.COMPLETED);
        invoice.setNetAmount(new BigDecimal("-0.01"));
        // then
        Assertions.assertThat(paymentLine.getUpstreamCreditNoteFound()).isTrue();

        // when approval state gets checked
        invoice.setApprovalState(IncomingInvoiceApprovalState.NEW);
        invoice.setNetAmount(new BigDecimal("-0.01"));
        // then
        Assertions.assertThat(paymentLine.getUpstreamCreditNoteFound()).isFalse();

        // when approval state is null
        invoice.setApprovalState(null);
        invoice.setNetAmount(new BigDecimal("-0.01"));
        // then
        Assertions.assertThat(paymentLine.getUpstreamCreditNoteFound()).isFalse();

        // and when net amount gets checked
        invoice.setApprovalState(IncomingInvoiceApprovalState.COMPLETED);
        invoice.setNetAmount(new BigDecimal("0.01"));
        // then
        Assertions.assertThat(paymentLine.getUpstreamCreditNoteFound()).isFalse();

        // and when net amount is null
        invoice.setApprovalState(IncomingInvoiceApprovalState.COMPLETED);
        invoice.setNetAmount(null);
        // then
        Assertions.assertThat(paymentLine.getUpstreamCreditNoteFound()).isFalse();
        
    }

}