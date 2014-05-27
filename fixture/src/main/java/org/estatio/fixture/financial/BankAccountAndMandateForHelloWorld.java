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

import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.party.OrganisationForHelloWorld;

public class BankAccountAndMandateForHelloWorld extends BankAccountAndMandateAbstract {

    public BankAccountAndMandateForHelloWorld() {
        this(null, null);
    }

    public BankAccountAndMandateForHelloWorld(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        execute(new OrganisationForHelloWorld(), executionContext);
        execute(new PropertyForOxf(), executionContext);

        // exec
        createBankAccountAndMandate(
                OrganisationForHelloWorld.PARTY_REFERENCE,
                "NL31ABNA0580744434",
                null,
                PropertyForOxf.PROPERTY_REFERENCE,
                executionContext);
    }

}
