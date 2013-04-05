package org.estatio.dom.financial;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BankAccountTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        BankAccount account = new BankAccount();
        account.setIBAN("NL31ABNA0580744434");
        account.checkAccount();
        Assert.assertThat(account.getAccountNumber(), Is.is("0580744434"));
    }
}
