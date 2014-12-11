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
package org.estatio.fixture.asset;

import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;
import org.estatio.fixture.party.OrganisationForAcme;
import org.estatio.fixture.party.PersonForJohnDoe;

import static org.estatio.integtests.VT.ld;

public class PropertyForKal extends PropertyAbstract {

    public static final String PROPERTY_REFERENCE = "KAL";

    public static String unitReference(String suffix) {
        return PROPERTY_REFERENCE + "-" + suffix;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new OrganisationForAcme());
            executionContext.executeChild(this, new PersonForJohnDoe());
        }

        // exec
        Party owner = parties.findPartyByReference(OrganisationForAcme.PARTY_REFERENCE);
        Party manager = parties.findPartyByReference(PersonForJohnDoe.PARTY_REFERENCE);
        Country country = countries.findCountry("NLD");

        createPropertyAndUnits(
                PROPERTY_REFERENCE, "Kalvertoren", "Amsterdam", country, PropertyType.SHOPPING_CENTER, 40,
                ld(2003, 12, 1), ld(2003, 12, 1), owner, manager, "52.37597;4.90814",
                executionContext);
    }


}
