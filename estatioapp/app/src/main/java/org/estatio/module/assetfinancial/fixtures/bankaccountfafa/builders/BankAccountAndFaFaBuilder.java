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
package org.estatio.module.assetfinancial.fixtures.bankaccountfafa.builders;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.fixtures.fafa.builders.BankAccountFaFaBuilder;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.builders.BankAccountBuilder;
import org.estatio.module.party.dom.Party;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"party", "iban", "property"})
@Accessors(chain = true)
public class BankAccountAndFaFaBuilder extends BuilderScriptAbstract<BankAccountAndFaFaBuilder>
{

    @Getter @Setter
    Party party;

    @Getter @Setter
    String iban;

    /**
     * Optional.
     */
    @Getter @Setter
    String bic;

    @Getter @Setter
    Property property;

    @Getter
    BankAccount bankAccount;

    @Getter
    FixedAssetFinancialAccount fixedAssetFinancialAccount;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        bankAccount = new BankAccountBuilder()
                .setParty(party)
                .setIban(iban)
                .setBic(bic)
                .build(this, executionContext)
                .getBankAccount();

        if(property != null) {
            fixedAssetFinancialAccount = new BankAccountFaFaBuilder()
                    .setBankAccount(bankAccount)
                    .setProperty(property)
                    .build(this, executionContext)
                    .getFixedAssetFinancialAccount();
        }
    }

}
