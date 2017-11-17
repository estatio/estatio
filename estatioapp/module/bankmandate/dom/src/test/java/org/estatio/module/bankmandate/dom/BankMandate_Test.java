package org.estatio.module.bankmandate.dom;

import org.junit.Test;

import org.estatio.module.financial.dom.BankAccount;

import static org.assertj.core.api.Assertions.assertThat;

public class BankMandate_Test {
    @Test
    public void changeBankAccount() throws Exception {

        // given
        final BankMandate bankMandate = new BankMandate();
        final BankAccount bankAccount = new BankAccount();
        bankMandate.setBankAccount(bankAccount);

        // when
        final BankAccount newBankAccount = new BankAccount();
        bankMandate.changeBankAccount(newBankAccount);

        // then
        assertThat(bankMandate.getBankAccount()).isSameAs(newBankAccount);
    }

}