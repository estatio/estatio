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
import org.incode.module.country.dom.impl.Country;
import org.estatio.dom.party.Party;
import org.incode.module.country.fixture.CountriesRefData;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNl;

import static org.incode.module.base.integtests.VT.ld;

public class PropertyForBudNl extends PropertyAbstract {

    public static final String REF = "BUD";
    public static final String PARTY_REF_OWNER = OrganisationForAcmeNl.REF;
    public static final String PARTY_REF_MANAGER = PersonForJohnDoeNl.REF;
    public static final String AT_PATH_COUNTRY = ApplicationTenancyForNl.PATH;

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForAcmeNl());
        executionContext.executeChild(this, new PersonForJohnDoeNl());

        // exec
        final Party owner = partyRepository.findPartyByReference(PARTY_REF_OWNER);
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);

        final Country netherlands = countryRepository.findCountry(CountriesRefData.NLD);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "BudgetToren", "Amsterdam", netherlands, PropertyType.SHOPPING_CENTER,
                6, ld(2003, 12, 1), ld(2003, 12, 1), owner, manager,
                "52.37597;4.90814", executionContext);
    }

}
