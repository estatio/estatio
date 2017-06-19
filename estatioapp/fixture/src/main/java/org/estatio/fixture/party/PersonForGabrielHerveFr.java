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

import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForFr;

public class PersonForGabrielHerveFr extends FixtureScript {

    public static final String REF = "GHERVE";
    public static final String AT_PATH = ApplicationTenancyForFr.PATH;

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new OrganisationForYoukeaSe());

        getContainer().injectServicesInto(new PersonBuilder())
                    .setAtPath(AT_PATH)
                    .setReference(REF)
                    .setFirstName("Gabriel")
                    .setLastName("Herve")
                    .setPersonGenderType(PersonGenderType.FEMALE)
                    .addPartyRoleType(PartyRoleTypeEnum.COUNTRY_DIRECTOR)
                    .setSecurityUsername(REF.toLowerCase())
                .execute(executionContext);
    }

}
