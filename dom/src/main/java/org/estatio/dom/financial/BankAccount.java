/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.financial;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.financial.utils.IBANHelper;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "FINANCIALACCOUNT_ID")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
@Bookmarkable
@MemberGroups({"General", "Account Details"})
public class BankAccount extends FinancialAccount {

    
    @javax.jdo.annotations.Column(name="BANK_ID")
    private Party bank;

    @MemberOrder(name = "Account Details", sequence = "9")
    public Party getBank() {
        return bank;
    }

    public void setBank(final Party bank) {
        this.bank = bank;
    }

    public String disableBank() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    
    // //////////////////////////////////////

    private BankAccountType bankAccountType;

    @MemberOrder(name = "Account Details", sequence = "10")
    @Disabled
    public BankAccountType getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(final BankAccountType bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String disableBankAccountType() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="COUNTRY_ID")
    private Country country;

    @MemberOrder(name = "Account Details", sequence = "11")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    public String disableCountry() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////

    private String IBAN;

    @MemberOrder(name = "Account Details", sequence = "12")
    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(final String IBAN) {
        this.IBAN = IBAN;
    }

    public String disableIBAN() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    
    // //////////////////////////////////////
    
    @MemberOrder(name="IBAN", sequence="1")
    public void verifyIBAN() {
        IBANHelper ibanHelper = new IBANHelper(getIBAN());
        ibanHelper.update(this);
    }

    public String disableVerifyIBAN() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////
    
    private String nationalCheckCode;

    @MemberOrder(name = "Account Details", sequence = "13")
    public String getNationalCheckCode() {
        return nationalCheckCode;
    }

    public void setNationalCheckCode(final String nationalCheckCode) {
        this.nationalCheckCode = nationalCheckCode;
    }

    public String disableNationalCheckCode() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////

    private String nationalBankCode;

    @MemberOrder(name = "Account Details", sequence = "14")
    public String getNationalBankCode() {
        return nationalBankCode;
    }

    public void setNationalBankCode(final String nationalBankCode) {
        this.nationalBankCode = nationalBankCode;
    }

    public String disableNationalBankCode() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////

    private String branchCode;

    @MemberOrder(name = "Account Details", sequence = "15")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(final String branchCode) {
        this.branchCode = branchCode;
    }

    public String disableBranchCode() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    
    // //////////////////////////////////////

    private String accountNumber;

    @MemberOrder(name = "Account Details", sequence = "16")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String disableAccountNumber() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    

}
