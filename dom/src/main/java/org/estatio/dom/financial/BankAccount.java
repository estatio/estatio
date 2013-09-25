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
import org.estatio.dom.financial.utils.IBANValidator;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "FINANCIALACCOUNT_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable
public class BankAccount extends FinancialAccount {

    private Party bank;

    @javax.jdo.annotations.Column(name = "BANK_ID", allowsNull = "true")
    public Party getBank() {
        return bank;
    }

    public void setBank(final Party bank) {
        this.bank = bank;
    }

    // //////////////////////////////////////

    private BankAccountType bankAccountType;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Disabled
    public BankAccountType getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(final BankAccountType bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    // //////////////////////////////////////

    private Country country;

    @javax.jdo.annotations.Column(name = "COUNTRY_ID", allowsNull = "true")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // //////////////////////////////////////

    private String IBAN;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(final String IBAN) {
        this.IBAN = IBAN;
    }

    // //////////////////////////////////////

    public boolean isValidIBAN() {
        return IBANValidator.valid(getIBAN());
    }

    // //////////////////////////////////////

    public BankAccount verifyIBAN() {
        IBANHelper.verifyAndUpdate(this);
        return this;
    }

    public String disableVerifyIBAN() {
        return isLocked() ? "Cannot modify when locked" : null;
    }

    // //////////////////////////////////////

    private String nationalCheckCode;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public String getNationalCheckCode() {
        return nationalCheckCode;
    }

    public void setNationalCheckCode(final String nationalCheckCode) {
        this.nationalCheckCode = nationalCheckCode;
    }

    // //////////////////////////////////////

    private String nationalBankCode;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public String getNationalBankCode() {
        return nationalBankCode;
    }

    public void setNationalBankCode(final String nationalBankCode) {
        this.nationalBankCode = nationalBankCode;
    }

    // //////////////////////////////////////

    private String branchCode;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(final String branchCode) {
        this.branchCode = branchCode;
    }

    // //////////////////////////////////////

    private String accountNumber;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
