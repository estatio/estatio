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
package org.estatio.fixture.property;

import static org.estatio.integtests.VT.ld;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.Projects;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.party.PersonForGinoVannelli;
import org.estatio.fixture.party.PersonForLinusTorvalds;

public class ProjectFixture extends EstatioFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        createProject(
                "2014ACEU",
                "ApacheCon EU 2014",
                ld("2014-11-17"),
                ld("2014-11-21"),
                null,
                parties.findPartyByReference(PersonForLinusTorvalds.PARTY_REFERENCE),
                executionContext);
        createProject(
                "2015ACNA",
                "ApacheCon NA 2015",
                ld("2015-04-13"),
                ld("2015-04-17"),
                null,
                parties.findPartyByReference(PersonForLinusTorvalds.PARTY_REFERENCE),
                executionContext);
        createProject(
                "2015OXFREF",
                "Oxford refurbishment",
                ld("2015-06-01"),
                null,
                properties.findPropertyByReference(PropertyForKal.PROPERTY_REFERENCE),
                parties.findPartyByReference(PersonForGinoVannelli.PARTY_REFERENCE),
                executionContext);
    }

    private void createProject(
            String reference,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Property property,
            Party party,
            ExecutionContext executionContext) {
        Project project = projects.newProject(reference, name, startDate, endDate, property, party);
        executionContext.add(this, reference, project);
    }

    // //////////////////////////////////////

    @Inject
    private Projects projects;

    @Inject
    private Parties parties;

    @Inject
    private Properties properties;

}
