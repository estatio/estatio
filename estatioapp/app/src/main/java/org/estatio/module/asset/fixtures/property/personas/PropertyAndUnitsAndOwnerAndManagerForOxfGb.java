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

import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.fixtures.PropertyAndUnitsAndOwnerAndManagerAbstract;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForGinoVannelliGb;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForTopModelGb;

import static org.incode.module.base.integtests.VT.ld;

public class PropertyAndUnitsAndOwnerAndManagerForOxfGb extends PropertyAndUnitsAndOwnerAndManagerAbstract {

    public static final Property_enum data = Property_enum.OxfGb;

    public static final String REF = data.getRef();
    public static final String PARTY_REF_OWNER = data.getOwner().getRef();
    public static final String PARTY_REF_MANAGER = data.getManager().getRef();
    public static final String AT_PATH_COUNTRY = data.getApplicationTenancy().getPath();

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new OrganisationForTopModelGb());
        executionContext.executeChild(this, new PersonAndRolesForGinoVannelliGb());

        // exec
        final Party owner = partyRepository.findPartyByReference(PARTY_REF_OWNER);
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);

        final Country greatBritain = Country_enum.GBR.findUsing(serviceRegistry);

        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "Oxford Super Mall", "Oxford", greatBritain, PropertyType.SHOPPING_CENTER,
                25, ld(1999, 1, 1), ld(2008, 6, 1), owner, manager,
                "51.74579;-1.24334", executionContext);
    }

}
