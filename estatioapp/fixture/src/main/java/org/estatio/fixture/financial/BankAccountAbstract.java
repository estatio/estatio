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
package org.estatio.fixture.financial;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.asset.dom.Property;
import org.estatio.asset.dom.PropertyRepository;
import org.estatio.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

public abstract class BankAccountAbstract extends FixtureScript {

    protected BankAccountAbstract(final String friendlyName, final String localName) {
        super(friendlyName, localName);
    }

    protected BankAccount createBankAccountAndOptionallyFixedAssetFinancialAsset(
            final String partyStr,
            final String bankAccountRef,
            final String propertyRef,
            final ExecutionContext executionContext) {

        final Party party = partyRepository.findPartyByReference(partyStr);

        final BankAccount bankAccount = bankAccountRepository.newBankAccount(party, bankAccountRef, null);
        executionContext.addResult(this, bankAccount.getReference(), bankAccount);

        if (propertyRef != null) {
            final Property property = propertyRepository.findPropertyByReference(propertyRef);
            fixedAssetFinancialAccountRepository.newFixedAssetFinancialAccount(property, bankAccount);
        }

        return bankAccount;
    }

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    private FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
