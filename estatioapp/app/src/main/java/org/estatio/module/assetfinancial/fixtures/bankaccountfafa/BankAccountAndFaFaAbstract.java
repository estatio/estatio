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
package org.estatio.module.assetfinancial.fixtures.bankaccountfafa;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccountAndFaFa_enum;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.dom.PartyRepository;

public abstract class BankAccountAndFaFaAbstract extends FixtureScript {

    private final BankAccountAndFaFa_enum data;

    protected BankAccountAndFaFaAbstract(BankAccountAndFaFa_enum data) {
        this.data = data;
    }

//    protected BankAccount createBankAccountAndOptionallyFixedAssetFinancialAsset(
//            final String partyStr,
//            final String bankAccountRef,
//            final Property_enum property_data,
//            final ExecutionContext executionContext) {
//
//        final Party party = partyRepository.findPartyByReference(partyStr);
//
//        final BankAccount bankAccount = bankAccountRepository.newBankAccount(party, bankAccountRef, null);
//        executionContext.addResult(this, bankAccount.getReference(), bankAccount);
//
//        if (property_data != null) {
//            final String propertyRef = property_data.getRef();
//            final Property property = propertyRepository.findPropertyByReference(propertyRef);
//            fixedAssetFinancialAccountRepository.newFixedAssetFinancialAccount(property, bankAccount);
//        }
//
//        return bankAccount;
//    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChildT(this, data.toFixtureScript());
    }


    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
