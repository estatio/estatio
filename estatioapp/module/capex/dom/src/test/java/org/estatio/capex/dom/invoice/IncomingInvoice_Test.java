package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoice_Test {

    IncomingInvoice incomingInvoice;

    @Test
    public void recalculateAmounts() throws Exception {

        // given
        incomingInvoice = new IncomingInvoice();
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();
        IncomingInvoiceItem item3 = new IncomingInvoiceItem();
        IncomingInvoiceItem item4 = new IncomingInvoiceItem();
        item1.setNetAmount(new BigDecimal("100.00"));
        item1.setGrossAmount(new BigDecimal("120.00"));
        item2.setNetAmount(new BigDecimal("50.00"));
        item2.setGrossAmount(new BigDecimal("55.00"));
        item3.setNetAmount(null); // explicit for test
        item3.setGrossAmount(new BigDecimal("-1.00"));
        item4.setNetAmount(new BigDecimal("-1.00"));
        item4.setGrossAmount(null); // explicit for test
        incomingInvoice.getItems().addAll(Arrays.asList(item1, item2, item3, item4));

        // when
        incomingInvoice.recalculateAmounts();

        // then
        Assertions.assertThat(incomingInvoice.getNetAmount()).isEqualTo(new BigDecimal("149.00"));
        Assertions.assertThat(incomingInvoice.getGrossAmount()).isEqualTo(new BigDecimal("174.00"));

    }

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setPaymentMethod(PaymentMethod.MANUAL_PROCESS);
        invoice.setBankAccount(new BankAccount());

        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        item1.setSequence(BigInteger.ONE);
        invoice.getItems().add(item1);

        // when
        String result = invoice.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("invoice number, buyer, seller, date received, due date, net amount, gross amount, (on item 1) start date, end date, net amount, gross amount required");

        // and when
        invoice.setInvoiceNumber("123");
        invoice.setNetAmount(new BigDecimal("100"));
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());
        item1.setNetAmount(new BigDecimal("100"));
        item1.setGrossAmount(new BigDecimal("100"));
        result = invoice.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, date received, due date, gross amount required");

        // and when
        invoice.setBuyer(new Organisation());
        invoice.setSeller(new Organisation());
        invoice.setBankAccount(new BankAccount());
        invoice.setDateReceived(new LocalDate());
        invoice.setDueDate(new LocalDate());
        invoice.setGrossAmount(BigDecimal.ZERO);
        result = invoice.reasonIncomplete();

        // then
        assertThat(result).isNull();

    }

}