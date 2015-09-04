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

import org.estatio.fixture.lease.LeaseForOxfMiracl005Gb;
import org.estatio.fixture.party.OrganisationForMiracleGb;

public class BankAccountForMiracleGb extends BankAccountAbstract {

    public static final String REF = "NL31ABNA0580744439";
    public static final String PARTY_REF = OrganisationForMiracleGb.REF;

    public BankAccountForMiracleGb() {
        this(null, null);
    }

    public BankAccountForMiracleGb(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new LeaseForOxfMiracl005Gb());
        }

        // exec
        createBankAccountAndOptionallyFixedAssetFinancialAsset(
                PARTY_REF,
                REF,
                null, // no property = no FAFA
                executionContext);
    }

}
