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
package org.estatio.module.application.fixtures.person.personas;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForMnsFr;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForVivFr;
import org.estatio.module.party.dom.PersonGenderType;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFr;

public class PersonAndRolesForFifineLacroixFr extends FixtureScript {

    public static final String REF = "FLACROIX";
    public static final String AT_PATH = ApplicationTenancyForFr.PATH;

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForVivFr());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForMnsFr());

        getContainer().injectServicesInto(new PersonAndRolesBuilder())
                    .setAtPath(AT_PATH)
                    .setReference(REF)
                    .setFirstName("Fifine")
                    .setLastName("Lacroix")
                    .setPersonGenderType(PersonGenderType.FEMALE)
                    .addFixedAssetRole(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, PropertyAndOwnerAndManagerForVivFr.REF)
                    .addFixedAssetRole(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, PropertyAndOwnerAndManagerForMnsFr.REF)
                    .addPartyRoleType(FixedAssetRoleTypeEnum.PROPERTY_MANAGER) // implied at the country level
                    .setSecurityUsername(REF.toLowerCase())
                .execute(executionContext);
    }

}
