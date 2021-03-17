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
package org.estatio.module.assetfinancial.fixtures.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.fixtures.builders.BankAccountFaFaBuilder;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum BankAccountFaFa_enum
        implements PersonaWithBuilderScript<FixedAssetFinancialAccount, BankAccountFaFaBuilder> {

    AcmeNl          (BankAccount_enum.AcmeNl, Property_enum.KalNl),
    HelloWorldGb    (BankAccount_enum.HelloWorldGb, Property_enum.OxfGb),
    HelloWorldNl    (BankAccount_enum.HelloWorldNl, Property_enum.KalNl),

    // nb: this referenced bank account is misnamed, is actually second bank account for HelloWorldGb party
    Oxford          (BankAccount_enum.Oxford, Property_enum.OxfGb),
    ;

    private final BankAccount_enum bankAccount_d;
    private final Property_enum property_d;

    @Override
    public BankAccountFaFaBuilder builder() {
        return new BankAccountFaFaBuilder()
                .setPrereq((f,ec) -> f.setBankAccount(f.objectFor(bankAccount_d, ec)))
                .setPrereq((f,ec) -> f.setProperty(f.objectFor(property_d, ec)));
    }


}
