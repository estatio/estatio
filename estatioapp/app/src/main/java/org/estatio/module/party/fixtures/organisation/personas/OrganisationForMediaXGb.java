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
package org.estatio.module.party.fixtures.organisation.personas;

import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGb;
import org.estatio.module.base.platform.fixturesupport.PersonaScriptAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.builders.OrganisationAndCommsBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class OrganisationForMediaXGb extends PersonaScriptAbstract {

    public static final String REF = "MEDIAX_GB";
    public static final String AT_PATH = ApplicationTenancyForGb.PATH;
    @Getter @Setter(AccessLevel.PROTECTED)
    private Organisation organisation;

    @Override
    protected void execute(ExecutionContext executionContext) {

        final OrganisationAndCommsBuilder organisationAndCommsBuilder = new OrganisationAndCommsBuilder();

        Organisation organisation = organisationAndCommsBuilder
                    .setAtPath(AT_PATH)
                    .setPartyName("Media Electronics (GB)")
                    .setPartyReference(REF)
                    .setAddress1("85 High St")
                    .setAddress2(null)
                    .setPostalCode("EN11 8TL")
                    .setCity("Hoddesdon")
                    .setStateReference(null)
                    .setCountryReference("GBR")
                    .setPhone("+442079897676")
                    .setFax("+442079897677")
                    .setEmailAddress("info@mediax.example.com")
                    .build(this, executionContext)
                    .getOrganisation();

        setOrganisation(organisation);

    }

}
