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
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.Status;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class BankMandate extends Agreement<Status> {
    

    public BankMandate() {
        super(Status.LOCKED, Status.UNLOCKED);
    }
    
    // //////////////////////////////////////

    private Status status;

    @Hidden
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }

    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="BANKACCOUNT_ID")
    private FinancialAccount bankAccount;

    public FinancialAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(final FinancialAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
    
    public String disableBankAccount() {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }

    // //////////////////////////////////////

    public Party getPrimaryParty() {
        return findParty(FinancialConstants.ART_CREDITOR);
    }

    public Party getSecondaryParty() {
        return findParty(FinancialConstants.ART_DEBTOR);
    }


}
