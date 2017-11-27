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
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnDoeNl;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;

import static org.incode.module.base.integtests.VT.ld;

public class PropertyAndUnitsAndOwnerAndManagerForBudNl extends PropertyAndUnitsAndOwnerAndManagerAbstract {

    public static final Property_enum data = Property_enum.BudNl;

    public static final String REF = data.getRef();
    public static final String PARTY_REF_OWNER = data.getOwner().getRef();
    public static final String PARTY_REF_MANAGER = data.getManager().getRef();
    public static final String AT_PATH_COUNTRY = data.getApplicationTenancy().getPath();

    public static String unitReference(String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForAcmeNl());
        executionContext.executeChild(this, new PersonAndRolesForJohnDoeNl());

        // exec
        final Party owner = partyRepository.findPartyByReference(PARTY_REF_OWNER);
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);

        final Country netherlands = Country_enum.NLD.findUsing(serviceRegistry);
        createPropertyAndUnits(
                AT_PATH_COUNTRY,
                REF, "BudgetToren", "Amsterdam", netherlands, PropertyType.SHOPPING_CENTER,
                7, ld(2003, 12, 1), ld(2003, 12, 1), owner, manager,
                "52.37597;4.90814", executionContext);
    }

}
