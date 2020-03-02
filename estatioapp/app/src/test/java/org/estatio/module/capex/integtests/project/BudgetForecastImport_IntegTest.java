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
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.capex.app.ProjectMenu;
import org.estatio.module.capex.dom.project.BudgetCreationManager;
import org.estatio.module.capex.dom.project.BudgetForecast;
import org.estatio.module.capex.dom.project.BudgetForecastItem;
import org.estatio.module.capex.dom.project.BudgetForecastRepositoryAndFactory;
import org.estatio.module.capex.dom.project.ForecastCreationManager;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BudgetForecastImport_IntegTest extends CapexModuleIntegTestAbstract {

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

    LocalDate forecastDate;
    Project project;

    @Test
    public void project_budget_import_works() throws Exception {

        // given
        forecastDate = new LocalDate(2020, 4, 1);
        project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
        setupApproveAndCommitBudget();

        // when
        final ForecastCreationManager forecastCreationManager = wrap(project).createBudgetForecast(
                forecastDate);
        assertThat(budgetForecastRepositoryAndFactory.findByProject(project)).isEmpty();
        forecastCreationManager.getForecastLines(); //TODO: this is to create a first forecast, normally happens when called by UI; maybe 'initialize' otherwise ....
        transactionService.nextTransaction();
        assertThat(budgetForecastRepositoryAndFactory.findByProject(project)).isNotEmpty();

        final URL url2 = Resources.getResource(BudgetForecastImport_IntegTest.class, "ForecastITPR001.xlsx");
        byte[] bytes2;
        try {
            bytes2 = Resources.toByteArray(url2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheet2 = new Blob("ForecastITPR001.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes2);
        forecastCreationManager.upload(spreadSheet2);

        // then
        final BudgetForecast forecast = budgetForecastRepositoryAndFactory.findUnique(project, forecastDate);
        assertThat(forecast.getItems()).hasSize(2);
        final BudgetForecastItem firstForecastItem = forecast.getItems().first();
        assertThat(firstForecastItem.getTerms()).hasSize(11);
        assertThat(firstForecastItem.getTerms().first().getStartDate()).isEqualTo(forecastDate);
        assertThat(firstForecastItem.getTerms().first().getAmount()).isEqualTo(BigDecimal.valueOf(0.0));
        assertThat(firstForecastItem.getTerms().last().getStartDate()).isEqualTo(new LocalDate(2022,10,1));
        assertThat(firstForecastItem.getTerms().last().getAmount()).isEqualTo(BigDecimal.valueOf(4000.0));
        assertThat(firstForecastItem.getSumTerms()).isEqualTo(BigDecimal.valueOf(114000.0));
        assertThat(firstForecastItem.getForecastedAmountCovered()).isFalse();
        final BudgetForecastItem lastForecastItem = forecast.getItems().last();
        assertThat(lastForecastItem.getTerms()).hasSize(11);
        assertThat(lastForecastItem.getForecastedAmountCovered()).isTrue();

    }

    public void setupApproveAndCommitBudget(){

        final BudgetCreationManager manager = wrap(project).editOrCreateBudget();
        assertThat(projectBudgetRepository.findByProject(project)).isEmpty();

        manager.getBudgetLines(); //TODO: this is to create a first budget, normally happens when called by UI; maybe 'initialize' otherwise ....
        transactionService.nextTransaction();
        assertThat(projectBudgetRepository.findByProject(project)).isNotEmpty();

        final URL url = Resources.getResource(BudgetForecastImport_IntegTest.class, "BudgetITPR001.xlsx");
        byte[] bytes;
        try {
            bytes = Resources.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Blob spreadSheet = new Blob("BudgetITPR001.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);
        manager.upload(spreadSheet);
        project.approveBudget(new LocalDate(2000,1,1));
        project.commitBudget(forecastDate);

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

    @Inject BudgetForecastRepositoryAndFactory budgetForecastRepositoryAndFactory;

    @Inject RepositoryService repositoryService;

    @Inject ChargeRepository chargeRepository;

}