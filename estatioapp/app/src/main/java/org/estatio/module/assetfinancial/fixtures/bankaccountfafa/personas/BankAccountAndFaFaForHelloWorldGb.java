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
package org.estatio.module.assetfinancial.fixtures.bankaccountfafa.personas;

import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.BankAccountAndFaFaAbstract;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccountAndFaFa_enum;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;

public class BankAccountAndFaFaForHelloWorldGb extends BankAccountAndFaFaAbstract {

    public static final BankAccountAndFaFa_enum data = BankAccountAndFaFa_enum.HelloWorldGb;

    public static final String REF = data.getIban();
    public static final String PARTY_REF = data.getOrganisation_d().getRef();

    public BankAccountAndFaFaForHelloWorldGb() {
        this(null, null);
    }

    public BankAccountAndFaFaForHelloWorldGb(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());

        // exec
        createBankAccountAndOptionallyFixedAssetFinancialAsset(
                PARTY_REF,
                REF,
                data.getProperty_d(), // create FAFA
                executionContext);
    }

}
