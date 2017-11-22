/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.module.capex.fixtures.project.personas;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;

public abstract class ProjectAbstract extends FixtureScript {

    protected Project createProject(
            final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal estimatedCost,
            final String atPath,
            final Project parent, final ExecutionContext fixtureResults) {
        Project project = projectRepository.create(reference, name, startDate, endDate, atPath, parent);
        return fixtureResults.addResult(this, project.getReference(), project);
    }

    // //////////////////////////////////////

  @Inject
    protected ProjectRepository projectRepository;
  
  @Inject
  	protected PartyRepository partyRepository;
  
}
