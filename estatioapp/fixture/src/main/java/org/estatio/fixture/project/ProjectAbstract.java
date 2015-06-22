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
package org.estatio.fixture.project;

import static org.estatio.integtests.VT.ld;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.estatio.dom.currency.Currency;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.project.BusinessCase;
import org.estatio.dom.project.BusinessCases;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.Programs;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectPhase;
import org.estatio.dom.project.ProjectRoleType;
import org.estatio.dom.project.ProjectRoles;
import org.estatio.dom.project.Projects;
import org.estatio.fixture.EstatioFixtureScript;
import org.joda.time.LocalDate;

/**
 * Sets up the {@link org.estatio.dom.project.Program} 
 */
public abstract class ProjectAbstract extends EstatioFixtureScript {

    protected Project createProject(
            final String reference, 
            final String name, 
            final LocalDate startDate,
            final LocalDate endDate,
            final Currency currency,
            final BigDecimal estimatedCost,
            final ProjectPhase projectPhase,
            final Program program,
            final Party executive,
            final Party manager,
            final ExecutionContext fixtureResults) {
        Project project = projects.newProject(reference, name, startDate, endDate, currency, estimatedCost, projectPhase, program);
        projectRoles.createRole(project, ProjectRoleType.PROJECT_EXECUTIVE, executive, ld(1999, 1, 1), ld(2000, 1, 1));
        projectRoles.createRole(project, ProjectRoleType.PROJECT_MANAGER, manager, ld(1999, 7, 1), ld(2000, 1, 1));
        BusinessCase updatedCase = businesscases.newBusinessCase(project, "Updated business case description for " + project.getName(), ld(1999,8,1), ld(1999,1,1), ld(1999,2,1), 2);
        businesscases.newBusinessCase(project, "Business case description for " + project.getName(), ld(1999, 7, 1), ld(1999, 1, 1), null, 1, updatedCase);
        return fixtureResults.addResult(this, project.getReference(), project);
    }

    // //////////////////////////////////////

  @Inject
    protected Projects projects;
  
  @Inject
  	protected Parties parties;
  
  @Inject
  protected Programs programs;
  
  @Inject 
  protected ProjectRoles projectRoles;
  
  @Inject
  protected BusinessCases businesscases;
}
