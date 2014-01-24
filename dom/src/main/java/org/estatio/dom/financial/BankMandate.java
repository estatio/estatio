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

import javax.jdo.annotations.InheritanceStrategy;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable // identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
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

    @javax.jdo.annotations.Column(name="bankAccountId", allowsNull="false")
    public FinancialAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(final FinancialAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
    
    // //////////////////////////////////////

    public Party getPrimaryParty() {
        return findCurrentOrMostRecentParty(FinancialConstants.ART_CREDITOR);
    }

    public Party getSecondaryParty() {
        return findCurrentOrMostRecentParty(FinancialConstants.ART_DEBTOR);
    }


}
