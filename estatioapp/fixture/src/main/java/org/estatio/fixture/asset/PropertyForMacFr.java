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

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixture.CountriesRefData;

import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.party.Party;
import org.estatio.fixture.party.OrganisationForHelloWorldFr;
import org.estatio.fixture.party.PersonForJeanneDarcFr;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForFr;

import static org.incode.module.base.integtests.VT.ld;

public class PropertyForMacFr extends PropertyAbstract {

    public static final String REF = "MAC";
    public static final String PARTY_REF_OWNER = OrganisationForHelloWorldFr.REF;
    public static final String PARTY_REF_MANAGER = PersonForJeanneDarcFr.REF;
    public static final String AT_PATH_COUNTRY = ApplicationTenancyForFr.PATH;

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldFr());
        executionContext.executeChild(this, new PersonForJeanneDarcFr());

        // exec
        Party owner = partyRepository.findPartyByReference(PARTY_REF_OWNER);
        Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);

        Country france = countryRepository.findCountry(CountriesRefData.FRA);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "Macro", "Paris", france, PropertyType.SHOPPING_CENTER,
                5, ld(2009, 9, 9), ld(2009, 12, 9), owner, manager,
                "48.745649;2.37957", executionContext);
    }

}
