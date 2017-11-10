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
package org.estatio.module.asset.fixtures.property.personas;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixture.CountriesRefData;

import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForAgnethaFaltskogSe;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.fixtures.PropertyAndOwnerAndManagerAbstract;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSe;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldSe;

import static org.incode.module.base.integtests.VT.ld;

public class PropertyAndOwnerAndManagerForHanSe extends PropertyAndOwnerAndManagerAbstract {

    public static final String REF = "HAN";
    public static final String PARTY_REF_OWNER = OrganisationForHelloWorldSe.REF;
    public static final String PARTY_REF_MANAGER = PersonAndRolesForAgnethaFaltskogSe.REF;
    public static final String AT_PATH_COUNTRY = ApplicationTenancyForSe.PATH;

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldSe());
        executionContext.executeChild(this, new PersonAndRolesForAgnethaFaltskogSe());

        // exec
        Party owner = partyRepository.findPartyByReference(PARTY_REF_OWNER);
        Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);

        Country sweden = countryRepository.findCountry(CountriesRefData.SWE);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "Handla Center", "Malmo", sweden, PropertyType.SHOPPING_CENTER,
                5, ld(2004, 5, 6), ld(2008, 6, 1), owner, manager,
                "56.4916209;13.0074661", executionContext);
    }

}
