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

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetItem;
import org.estatio.module.capex.dom.project.ProjectBudgetItemRepository;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.fixtures.charge.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;

public class ProjectBudgetItemRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new IncomingChargesItaXlsxFixture());
                executionContext.executeChild(this, Project_enum.RonProjectIt);
            }
        });
    }

    @Inject
    ProjectBudgetRepository projectBudgetRepository;

    @Inject
    ProjectBudgetItemRepository projectBudgetItemRepository;

    @Test
    public void find_or_create_works() throws Exception {

        // given
        Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry2);
        final ProjectItem projectItem = project.getItems().first();
        Assertions.assertThat(projectItem).isNotNull();

        // when
        // NOTE: implicitly we are testing ProjectBudgetItemRepository#findOrCreate already here as well ....
        final ProjectBudget budget = projectBudgetRepository.findOrCreate(project, 1);


        // when
        final ProjectBudgetItem budgetItem = projectBudgetItemRepository
                .findOrCreate(budget, projectItem);

        // then
        Assertions.assertThat(projectBudgetItemRepository.listAll()).hasSize(2);
        Assertions.assertThat(projectBudgetItemRepository.findUnique(budget, projectItem)).isEqualTo(budgetItem);

    }

    @Inject ServiceRegistry2 serviceRegistry2;

}