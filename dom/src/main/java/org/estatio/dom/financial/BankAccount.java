package org.estatio.dom.financial;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.IBANHelper;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "FINANCIALACCOUNT_ID")
@Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public class BankAccount extends FinancialAccount {

    private Party bank;

    @Optional
    @MemberOrder(name = "Account Details", sequence = "10")
    public Party getBank() {
        return bank;
    }

    public void setBank(final Party bank) {
        this.bank = bank;
    }

    private Country country;

    @MemberOrder(name = "Account Details", sequence = "11")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    private String IBAN;

    @MemberOrder(name = "Account Details", sequence = "12")
    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(final String IBAN) {
        this.IBAN = IBAN;
    }

    private String nationalCheckCode;

    @MemberOrder(name = "Account Details", sequence = "13")
    public String getNationalCheckCode() {
        return nationalCheckCode;
    }

    public void setNationalCheckCode(final String nationalCheckCode) {
        this.nationalCheckCode = nationalCheckCode;
    }

    private String nationalBankCode;

    @MemberOrder(name = "Account Details", sequence = "14")
    public String getNationalBankCode() {
        return nationalBankCode;
    }

    public void setNationalBankCode(final String nationalBankCode) {
        this.nationalBankCode = nationalBankCode;
    }

    private String branchCode;

    @MemberOrder(name = "Account Details", sequence = "15")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(final String branchCode) {
        this.branchCode = branchCode;
    }

    private String accountNumber;

    @MemberOrder(name = "Account Details", sequence = "16")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void checkAccount() {
        IBANHelper ibanHelper = new IBANHelper(getIBAN());
        ibanHelper.update(this);
    }
    
}
