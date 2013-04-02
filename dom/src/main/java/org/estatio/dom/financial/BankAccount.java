package org.estatio.dom.financial;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;

@PersistenceCapable
public class BankAccount extends FinancialAccount {

    // {{ Bank (property)
    private Party bank;

    @Optional
    @MemberOrder(sequence = "10")
    public Party getBank() {
        return bank;
    }

    public void setBank(final Party bank) {
        this.bank = bank;
    }

    // }}

    // {{ Country (property)
    private Country country;

    @MemberOrder(sequence = "11")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // }}

    // {{ IBAN (property)
    private String IBAN;

    @MemberOrder(sequence = "12")
    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(final String IBAN) {
        this.IBAN = IBAN;
    }

    // }}

    // {{ NationalCheckCode (property)
    private String nationalCheckCode;

    @MemberOrder(sequence = "13")
    public String getNationalCheckCode() {
        return nationalCheckCode;
    }

    public void setNationalCheckCode(final String nationalCheckCode) {
        this.nationalCheckCode = nationalCheckCode;
    }

    // }}

    // {{ NationalBankCode (property)
    private String nationalBankCode;

    @MemberOrder(sequence = "14")
    public String getNationalBankCode() {
        return nationalBankCode;
    }

    public void setNationalBankCode(final String nationalBankCode) {
        this.nationalBankCode = nationalBankCode;
    }

    // }}

    // {{ BranchCode (property)
    private String branchCode;

    @MemberOrder(sequence = "7")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(final String branchCode) {
        this.branchCode = branchCode;
    }

    // }}

    // {{ AccountNumber (property)
    private String accountNumber;

    @MemberOrder(sequence = "8")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }
    // }}

}
