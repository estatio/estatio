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

import org.estatio.dom.financial.utils.IBANHelper;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "FINANCIALACCOUNT_ID")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
@Bookmarkable
public class BankAccount extends FinancialAccount {

    
    @javax.jdo.annotations.Column(name="BANK_ID")
    private Party bank;

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
    
    public void verifyIBAN() {
        IBANHelper ibanHelper = new IBANHelper(getIBAN());
        ibanHelper.update(this);
    }

    public String disableVerifyIBAN() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////
    
    private String nationalCheckCode;

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
