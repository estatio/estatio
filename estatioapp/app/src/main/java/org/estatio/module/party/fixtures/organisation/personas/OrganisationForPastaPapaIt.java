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

import org.estatio.module.base.platform.fixturesupport.PersonaScriptAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.builders.OrganisationAndCommsBuilder;

import lombok.Getter;

public class OrganisationForPastaPapaIt extends PersonaScriptAbstract {

    public static final Organisation_enum data = Organisation_enum.PastaPapaItNl;

    public static final String REF = data.getRef();
    public static final String AT_PATH = data.getApplicationTenancy().getPath();

    @Getter
    private Organisation organisation;

    @Override
    protected void execute(ExecutionContext executionContext) {

        final OrganisationAndCommsBuilder organisationAndCommsBuilder = new OrganisationAndCommsBuilder();

        this.organisation = organisationAndCommsBuilder
                    .setAtPath(AT_PATH)
                    .setPartyName(data.name)
                    .setPartyReference(REF)
                    .setAddress1(null)
                    .setAddress2(null)
                    .setPostalCode(null)
                    .setCity(null)
                    .setStateReference(null)
                    .setCountryReference(null)
                    .setPhone(null)
                    .setFax(null)
                    .setEmailAddress(null)
                    .build(this, executionContext)
                    .getOrganisation();
    }
}
