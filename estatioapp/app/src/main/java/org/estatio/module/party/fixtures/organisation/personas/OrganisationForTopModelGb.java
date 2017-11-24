/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License")"," you may not use this file except in compliance
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
import org.estatio.module.party.fixtures.organisation.builders.OrganisationCommsBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class OrganisationForTopModelGb extends PersonaScriptAbstract {

    public static final String REF = "TOPMODEL";
    public static final String AT_PATH = ApplicationTenancyForGb.PATH;
    @Getter @Setter(AccessLevel.PROTECTED)
    private Organisation organisation;

    @Override
    protected void execute(ExecutionContext executionContext) {

        final OrganisationAndCommsBuilder organisationAndCommsBuilder = new OrganisationAndCommsBuilder();
        Organisation organisation1 = organisationAndCommsBuilder
                    .setAtPath(AT_PATH)
                    .setPartyName("Topmodel Fashion")
                    .setPartyReference(REF)
                    .setAddress1("2 Top Road")
                    .setAddress2(null)
                    .setPostalCode("W2AXXX")
                    .setCity("London")
                    .setStateReference(null)
                    .setCountryReference("GBR")
                    .setPhone("+31202211333")
                    .setFax("+312022211399")
                    .setEmailAddress("info@topmodel.example.com")
                    .build(this, executionContext)
                    .getOrganisation();

        setOrganisation(organisation1);

        Organisation organisation = organisation1;

        final OrganisationCommsBuilder organisationCommsBuilder =
                new OrganisationCommsBuilder();
        organisationCommsBuilder
                .setOrganisation(organisation)
                .setAddress1("1 Circle Square")
                .setPostalCode("W2AXXX")
                .setCity("London")
                .setCountryReference("GBR")
                .build(this, executionContext);

//        createCommunicationChannels(
//                organisation,
//                "1 Circle Square",
//                null,
//                "W2AXXX",
//                "London",
//                null,
//                "GBR",
//                null,
//                null,
//                null,
//                executionContext);
    }
}
