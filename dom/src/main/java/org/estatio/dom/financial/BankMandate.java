/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import java.util.List;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findBankMandatesFor", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.financial.BankMandate "
                        + "WHERE bankAccount == :bankAccount")
})
public class BankMandate extends Agreement {

    // //////////////////////////////////////

    private FinancialAccount bankAccount;

    @javax.jdo.annotations.Column(name = "bankAccountId", allowsNull = "false")
    public FinancialAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(final FinancialAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BankMandate changeBankAccount(
            final BankAccount bankAccount
            ) {
        setBankAccount(bankAccount);
        return this;
    }

    public List<BankAccount> choices0ChangeBankAccount() {
        return financialAccounts.findBankAccountsByOwner(getSecondaryParty());
    }

    // //////////////////////////////////////

    private String sepaMandateIdentifier;

    @Optional
    @javax.jdo.annotations.Column(length = JdoColumnLength.BankMandate.SEPA_MANDATE_IDENTIFIER)
    public String getSepaMandateIdentifier() {
        return sepaMandateIdentifier;
    }

    public void setSepaMandateIdentifier(final String sepaMandateIdentifier) {
        this.sepaMandateIdentifier = sepaMandateIdentifier;
    }

    // //////////////////////////////////////

    public Party getPrimaryParty() {
        return findCurrentOrMostRecentParty(FinancialConstants.ART_CREDITOR);
    }

    public Party getSecondaryParty() {
        return findCurrentOrMostRecentParty(FinancialConstants.ART_DEBTOR);
    }

    // //////////////////////////////////////

    private FinancialAccounts financialAccounts;

    public final void injectFinancialAccounts(final FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

}
