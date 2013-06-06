package org.estatio.dom.financial;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.financial.utils.IBANHelper;

public class IBANHelperTest {

    @Before
    public void setup() {

    }
    
    @Test
    public void testDutchAccount() {
        IBANHelper ibanHelper = new IBANHelper("NL31ABNA0580744434");
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertThat(ba.getNationalBankCode(), Is.is("ABNA"));
        Assert.assertThat(ba.getAccountNumber(), Is.is("0580744434"));
    }

    @Test
    public void testItalianAccount() {
        IBANHelper ibanHelper = new IBANHelper("IT69N0347501601000051986922");
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertThat(ba.getNationalBankCode(), Is.is("03475"));
        Assert.assertThat(ba.getBranchCode(), Is.is("01601"));
        Assert.assertThat(ba.getAccountNumber(), Is.is("000051986922"));
    }

    
    @Test
    public void testEmptyAccount() {
        IBANHelper ibanHelper = new IBANHelper(null);
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertNull(ba.getNationalBankCode());
        Assert.assertNull(ba.getBranchCode());
        Assert.assertNull(ba.getAccountNumber());
    }

    
    @Test
    public void testFalseAccount() {
        IBANHelper ibanHelper = new IBANHelper("IT1231231");
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertNull(ba.getNationalBankCode());
        Assert.assertNull(ba.getBranchCode());
        Assert.assertNull(ba.getAccountNumber());
    }

}
