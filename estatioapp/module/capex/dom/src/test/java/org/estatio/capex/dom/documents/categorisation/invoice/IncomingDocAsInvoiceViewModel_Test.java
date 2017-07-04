package org.estatio.capex.dom.documents.categorisation.invoice;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocAsInvoiceViewModel_Test {

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        IncomingDocAsInvoiceViewModel vm = new IncomingDocAsInvoiceViewModel();
        vm.setPaymentMethod(PaymentMethod.MANUAL_PROCESS);

        // when
        String result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isEqualTo("invoice number, buyer, seller, date received, due date, net amount, gross amount, period required");

        // and when
        vm.setInvoiceNumber("123");
        vm.setNetAmount(new BigDecimal("100"));
        result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, date received, due date, gross amount, period required");

        // and when
        vm.setBuyer(new Organisation());
        vm.setSeller(new Organisation());
        vm.setBankAccount(new BankAccount());
        vm.setDateReceived(new LocalDate());
        vm.setDueDate(new LocalDate());
        vm.setGrossAmount(BigDecimal.ZERO);
        vm.setPeriod("2017");
        result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isNull();

    }

}