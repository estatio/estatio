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
package org.estatio.fixture.party;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGb;

/**
 * Property manager for OXF
 */
public class PersonForJonathanPropertyManagerGb extends FixtureScript {

    public static final String REF = "JRICE";
    public static final String SECURITY_USERNAME = REF.toLowerCase();
    public static final String AT_PATH = ApplicationTenancyForGb.PATH;

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new PropertyForOxfGb());

        getContainer().injectServicesInto(new PersonBuilder())
                    .setAtPath(AT_PATH)
                    .setReference(REF)
                    .setFirstName("Jonathan")
                    .setLastName("Rice")
                    .setPersonGenderType(PersonGenderType.MALE)
                    .addFixedAssetRole(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, PropertyForOxfGb.REF)
                    .addPartyRoleType(FixedAssetRoleTypeEnum.PROPERTY_MANAGER) // implied
                    .setSecurityUsername(REF.toLowerCase())
                .execute(executionContext);
    }

}
