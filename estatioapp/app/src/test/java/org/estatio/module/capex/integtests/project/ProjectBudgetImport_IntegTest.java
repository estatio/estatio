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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.app.ProjectMenu;
import org.estatio.module.capex.dom.project.BudgetCreationManager;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetItem;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.imports.ProjectImportManager;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.EstatioChargeModule;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProjectBudgetImport_IntegTest extends CapexModuleIntegTestAbstract {

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

    @Test
    public void project_budget_import_works() throws Exception {

        // given

        final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
        final BudgetCreationManager manager = wrap(project).editOrCreateBudget();
        assertThat(projectBudgetRepository.findByProject(project)).isEmpty();

        manager.getBudgetLines(); //TODO: this is to create a first budget, normally happens when called by UI; maybe 'initialize' otherwise ....
        transactionService.nextTransaction();
        assertThat(projectBudgetRepository.findByProject(project)).isNotEmpty();

        final URL url = Resources.getResource(ProjectBudgetImport_IntegTest.class, "BudgetITPR001.xlsx");
        byte[] bytes;
        try {
            bytes = Resources.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheet = new Blob("BudgetITPR001.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);


        // when
        manager.upload(spreadSheet);

        // then
        assertThat(projectBudgetRepository.findByProject(project)).hasSize(1);
        final ProjectBudget budget = projectBudgetRepository.findByProject(project).get(0);
        assertThat(budget.getBudgetVersion()).isEqualTo(1);
        assertThat(budget.getApprovedBy()).isNull();
        assertThat(budget.getApprovedOn()).isNull();
        assertThat(budget.getCommittedBy()).isNull();
        assertThat(budget.getCommittedOn()).isNull();
        assertThat(budget.getItems()).hasSize(2);

        final ProjectBudgetItem firstBudgetItem = budget.getItems().first();
        assertThat(firstBudgetItem.getAmount()).isEqualTo(BigDecimal.valueOf(123456.78));
        assertThat(firstBudgetItem.getProjectItem().getCharge().getReference()).isEqualTo("ITWT001");

        final ProjectBudgetItem lastBudgetItem = budget.getItems().last();
        assertThat(lastBudgetItem.getAmount()).isEqualTo(BigDecimal.valueOf(9876543.21));
        assertThat(lastBudgetItem.getProjectItem().getCharge().getReference()).isEqualTo("ITWT005");

    }

    @After
    public void cleanRefData(){
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                //TODO: is this needed? Then also delete budget
//                projectRepository.listAll().forEach(p->p.delete());
//                transactionService.flushTransaction();
//                executionContext.executeChild(this, new EstatioChargeModule().getRefDataTeardown());
            }
        });
    }

    @Inject ProjectMenu projectMenu;

    @Inject ProjectRepository projectRepository;

    @Inject ProjectBudgetRepository projectBudgetRepository;


    @Inject RepositoryService repositoryService;

    @Inject ChargeRepository chargeRepository;

}