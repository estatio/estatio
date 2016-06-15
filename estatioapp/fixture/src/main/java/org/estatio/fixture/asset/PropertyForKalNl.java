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
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNl;

import static org.estatio.integtests.VT.ld;

public class PropertyForKalNl extends PropertyAbstract {

    public static final String REF = "KAL";
    public static final String PARTY_REF_OWNER = OrganisationForAcmeNl.REF;
    public static final String PARTY_REF_MANAGER = PersonForJohnDoeNl.REF;
    public static final String AT_PATH_COUNTRY = ApplicationTenancyForNl.PATH;

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new OrganisationForAcmeNl());
            executionContext.executeChild(this, new PersonForJohnDoeNl());
        }

        // exec
        final Party owner = parties.findPartyByReference(PARTY_REF_OWNER);
        final Party manager = parties.findPartyByReference(PARTY_REF_MANAGER);

        final Country netherlands = countryRepository.findCountry(CountriesRefData.NLD);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "Kalvertoren", "Amsterdam", netherlands, PropertyType.SHOPPING_CENTER,
                40, ld(2003, 12, 1), ld(2003, 12, 1), owner, manager,
                "52.37597;4.90814", executionContext);
    }


}
