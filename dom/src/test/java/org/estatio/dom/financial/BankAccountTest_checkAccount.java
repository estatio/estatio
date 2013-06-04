package org.estatio.dom.financial;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BankAccountTest_checkAccount {

    private BankAccount account;

    @Before
    public void setUp() throws Exception {
        account = new BankAccount();
    }

    @Test
    public void happyCase() {
        account.setIBAN("NL31ABNA0580744434");
        account.checkAccount();
        assertThat(account.getAccountNumber(), is("0580744434"));
    }
}
