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
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGb;

public class PropertyForOxfGb extends PropertyAbstract {

    public static final String REF = "OXF";
    public static final String PARTY_REF_MANAGER = PersonForGinoVannelliGb.REF;
    public static final String PARTY_REF_OWNER = OrganisationForHelloWorldGb.REF;
    public static final String AT_PATH_COUNTRY = ApplicationTenancyForGb.PATH;

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new OrganisationForHelloWorldGb());
            executionContext.executeChild(this, new OrganisationForTopModelGb());
            executionContext.executeChild(this, new PersonForGinoVannelliGb());
        }

        // exec
        final Party owner = parties.findPartyByReference(PARTY_REF_OWNER);
        final Party manager = parties.findPartyByReference(PARTY_REF_MANAGER);

        final Country greatBritain = countries.findCountry(CountriesRefData.GBR);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "Oxford Super Mall", "Oxford", greatBritain, PropertyType.SHOPPING_CENTER,
                25, ld(1999, 1, 1), ld(2008, 6, 1), owner, manager,
                "51.74579;-1.24334", executionContext);
    }

}
