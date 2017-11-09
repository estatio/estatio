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

import org.estatio.module.party.dom.PersonGenderType;
import org.estatio.module.party.dom.relationship.PartyRelationshipTypeEnum;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForIt;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForPastaPapaIt;

public class PersonAndRolesForLucianoPavarottiIt extends FixtureScript {

    public static final String REF = "LPAVAROTTI";
    public static final String AT_PATH = ApplicationTenancyForIt.PATH;
    public static final String PARTY_REF_FROM = OrganisationForPastaPapaIt.REF;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForPastaPapaIt());

        getContainer().injectServicesInto(new PersonAndRolesBuilder())
                    .setAtPath(AT_PATH)
                    .setReference(REF)
                    .setInitials("L")
                    .setFirstName("Luciano")
                    .setLastName("Pavarotti")
                    .setPersonGenderType(PersonGenderType.MALE)
                    .setFromPartyStr(PARTY_REF_FROM)
                    .setRelationshipType(PartyRelationshipTypeEnum.CONTACT.fromTitle())
                .execute(executionContext);
    }
}
