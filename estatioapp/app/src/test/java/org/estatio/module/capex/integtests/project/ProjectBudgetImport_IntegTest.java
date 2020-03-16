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

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.RecoverableException;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.capex.app.ProjectMenu;
import org.estatio.module.capex.dom.project.BudgetCreationManager;
import org.estatio.module.capex.dom.project.BudgetLineViewmodel;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetItem;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.charge.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.ChargeRepository;

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

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void project_budget_creation_and_amendment_works() throws Exception {

        // given

        final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
        final BudgetCreationManager managerV1 = wrap(project).createBudget();
        assertThat(projectBudgetRepository.findByProject(project)).isEmpty();

        managerV1.getBudgetLines(); //TODO: this is to create a first budget, normally happens when called by UI; maybe 'initialize' otherwise ....
        transactionService.nextTransaction();
        assertThat(projectBudgetRepository.findByProject(project)).isNotEmpty();

        final URL urlV1 = Resources.getResource(ProjectBudgetImport_IntegTest.class, "BudgetITPR001.xlsx");
        byte[] bytesV1;
        try {
            bytesV1 = Resources.toByteArray(urlV1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheetV1 = new Blob("BudgetITPR001.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytesV1);

        // when
        managerV1.upload(spreadSheetV1);

        // then
        assertThat(projectBudgetRepository.findByProject(project)).hasSize(1);
        final ProjectBudget budgetV1 = projectBudgetRepository.findByProject(project).get(0);
        assertThat(budgetV1.getBudgetVersion()).isEqualTo(1);
        assertThat(budgetV1.getApprovedBy()).isNull();
        assertThat(budgetV1.getApprovedOn()).isNull();
        assertThat(budgetV1.getCommittedBy()).isNull();
        assertThat(budgetV1.getCommittedOn()).isNull();
        assertThat(budgetV1.getItems()).hasSize(2);

        final ProjectBudgetItem firstBudgetItem = budgetV1.getItems().first();
        assertThat(firstBudgetItem.getAmount()).isEqualTo(BigDecimal.valueOf(123456.78));
        assertThat(firstBudgetItem.getProjectItem().getCharge().getReference()).isEqualTo("ITWT001");

        final ProjectBudgetItem lastBudgetItem = budgetV1.getItems().last();
        assertThat(lastBudgetItem.getAmount()).isEqualTo(BigDecimal.valueOf(9876543.21));
        assertThat(lastBudgetItem.getProjectItem().getCharge().getReference()).isEqualTo("ITWT005");

        // and when amending
        final LocalDate approvalAndCommittedDate = new LocalDate(2020, 4, 1);
        wrap(project).approveBudget(approvalAndCommittedDate);
        wrap(project).commitBudget(approvalAndCommittedDate);
        final BudgetCreationManager managerV2 = wrap(project).amendBudget();
        assertThat(projectBudgetRepository.findByProject(project)).hasSize(1); // inital budgetV1

        // then
        managerV2.getBudgetLines(); //TODO: this is to create a first budget, normally happens when called by UI; maybe 'initialize' otherwise ....
        transactionService.nextTransaction();
        assertThat(projectBudgetRepository.findByProject(project)).hasSize(2);
        final ProjectBudget budgetV2 = projectBudgetRepository.findUnique(project, 2);
        assertThat(budgetV2.getApprovedBy()).isNull();
        assertThat(budgetV2.getApprovedOn()).isNull();
        assertThat(budgetV2.getCommittedBy()).isNull();
        assertThat(budgetV2.getCommittedOn()).isNull();
        assertThat(budgetV2.getItems()).hasSize(2);
        assertThat(budgetV2.getItems().first().getProjectItem()).isEqualTo(budgetV1.getItems().first().getProjectItem());
        assertThat(budgetV2.getItems().first().getAmount()).isEqualTo(budgetV1.getItems().first().getAmount());
        assertThat(budgetV2.getItems().last().getProjectItem()).isEqualTo(budgetV1.getItems().last().getProjectItem());
        assertThat(budgetV2.getItems().last().getAmount()).isEqualTo(budgetV1.getItems().last().getAmount());
        assertThat(managerV2.getBudgetLines().size()).isEqualTo(2);
        final BudgetLineViewmodel firstVmLine = managerV2.getBudgetLines().get((0));
        assertThat(firstVmLine.getBudgetVersion()).isEqualTo(2);
        assertThat(firstVmLine.getAmount()).isEqualTo(budgetV1.getItems().first().getAmount());
        final BudgetLineViewmodel secondVmLine = managerV2.getBudgetLines().get((1));
        assertThat(secondVmLine.getBudgetVersion()).isEqualTo(2);
        assertThat(secondVmLine.getAmount()).isEqualTo(budgetV1.getItems().last().getAmount());

        // and when
        final URL urlV2 = Resources.getResource(ProjectBudgetImport_IntegTest.class, "BudgetITPR001V2.xlsx");
        byte[] bytesV2;
        try {
            bytesV2 = Resources.toByteArray(urlV2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheetV2 = new Blob("BudgetITPR001V2.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytesV2);
        managerV2.upload(spreadSheetV2);

        // then
        final ProjectBudgetItem firstBudgetV2Item = budgetV2.getItems().first();
        assertThat(firstBudgetV2Item.getAmount()).isEqualTo(BigDecimal.valueOf(123400.0));
        assertThat(firstBudgetV2Item.getProjectItem().getCharge().getReference()).isEqualTo("ITWT001");

        final ProjectBudgetItem lastBudgetV2Item = budgetV2.getItems().last();
        assertThat(lastBudgetV2Item.getAmount()).isEqualTo(BigDecimal.valueOf(9800000.0));
        assertThat(lastBudgetV2Item.getProjectItem().getCharge().getReference()).isEqualTo("ITWT005");

        // and when
        wrap(project).approveBudget(approvalAndCommittedDate);

        // then
        assertThat(budgetV2.getApprovedOn()).isEqualTo(approvalAndCommittedDate);
        assertThat(budgetV2.getCommittedOn()).isEqualTo(approvalAndCommittedDate);

        // and expect
        exception.expect(RecoverableException.class);
        exception.expectMessage("Budget for ITPR001 should have version 3");

        // when
        final BudgetCreationManager managerV3 = wrap(project).amendBudget();
        final URL urlV3E = Resources.getResource(ProjectBudgetImport_IntegTest.class, "BudgetITPR001V3.xlsx");
        byte[] bytesV3E;
        try {
            bytesV3E = Resources.toByteArray(urlV3E);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheetV3E = new Blob("BudgetITPR001V3.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytesV3E);
        managerV3.upload(spreadSheetV3E);

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