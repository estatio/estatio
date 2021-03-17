package org.estatio.module.financial.dom.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.financial.dom.BankAccount;

public class IBANHelper_disassembleIBAN_Test {

    @Test
    public void for_a_typical_Italian_IBAN() throws Exception {

        // given
        BankAccount account = new BankAccount();
        account.setIban("IT10M0558401700000000024213");

        // when
        IBANHelper.disassembleIBAN(account);

        // then
        Assertions.assertThat(account.getNationalBankCode() ).isEqualTo("05584");
        Assertions.assertThat(account.getBranchCode() ).isEqualTo("01700");
        Assertions.assertThat(account.getNationalBankCode() + account.getBranchCode() ).isEqualTo("0558401700");
    }

}