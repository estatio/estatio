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
package org.estatio.module.capex.integtests.project;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ProjectBudgetRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new IncomingChargesItaXlsxFixture());
                executionContext.executeChild(this, Project_enum.RonProjectIt.builder());
            }
        });
    }

    @Inject
    ProjectBudgetRepository projectBudgetRepository;

    @Test
    public void find_or_create_and_find_committed_works() throws Exception {

        // given
        Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry2);
        Assertions.assertThat(projectBudgetRepository.listAll()).isEmpty();
        Assertions.assertThat(projectBudgetRepository.findByProject(project)).isEmpty();

        // when
        final ProjectBudget budget = projectBudgetRepository.findOrCreate(project, 1);

        // then
        Assertions.assertThat(projectBudgetRepository.listAll()).hasSize(1);
        Assertions.assertThat(projectBudgetRepository.findByProject(project)).contains(budget);
        Assertions.assertThat(projectBudgetRepository.findUnique(project, 1)).isEqualTo(budget);
        Assertions.assertThat(projectBudgetRepository.findCommittedByProject(project)).isEmpty();

        // and when
        budget.setCommittedOn(new LocalDate(2020,1,1));
        // then
        Assertions.assertThat(projectBudgetRepository.findCommittedByProject(project)).hasSize(1);
        Assertions.assertThat(projectBudgetRepository.findCommittedByProject(project)).contains(budget);


    }

    @Inject ServiceRegistry2 serviceRegistry2;

}