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

import static org.estatio.integtests.VT.ld;

import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.party.OrganisationForHelloWorldIt;
import org.estatio.fixture.party.PersonForLucianoPavarottiIt;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForIt;

public class PropertyForGraIt extends PropertyAbstract {

    public static final String REF = "GRA";
    public static final String PARTY_REF_OWNER = OrganisationForHelloWorldIt.REF;
    public static final String PARTY_REF_MANAGER = PersonForLucianoPavarottiIt.REF;
    public static final String AT_PATH_COUNTRY = ApplicationTenancyForIt.PATH;

    public static String unitReference(final String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new OrganisationForHelloWorldIt());
            executionContext.executeChild(this, new PersonForLucianoPavarottiIt());
        }

        // exec
        final Party owner = parties.findPartyByReference(PARTY_REF_OWNER);
        final Party manager = parties.findPartyByReference(PARTY_REF_MANAGER);

        final Country italy = countries.findCountry(CountriesRefData.ITA);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "Centro Grande Punto", "Milano", italy, PropertyType.SHOPPING_CENTER,
                55, ld(2004, 5, 6), ld(2008, 6, 1), owner, manager,
                "45.5399865;9.3263305", executionContext);
    }

}
