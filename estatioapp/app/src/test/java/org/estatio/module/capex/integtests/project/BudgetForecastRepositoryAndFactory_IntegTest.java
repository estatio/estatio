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

import java.math.BigDecimal;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.project.BudgetForecast;
import org.estatio.module.capex.dom.project.BudgetForecastItem;
import org.estatio.module.capex.dom.project.BudgetForecastItemTerm;
import org.estatio.module.capex.dom.project.BudgetForecastRepositoryAndFactory;
import org.estatio.module.capex.dom.project.ForecastFrequency;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;

public class BudgetForecastRepositoryAndFactory_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new IncomingChargesItaXlsxFixture());
                executionContext.executeChild(this, Project_enum.RonProjectIt.builder());
            }
        });

        project = Project_enum.RonProjectIt.findUsing(serviceRegistry2);
        committedBudget = projectBudgetRepository.findOrCreate(project, 1);
        committedBudget.createItem(project.getItems().first(), budgetedAmountItem1);
        committedBudget.createItem(project.getItems().last(), budgetedAmountItem2);
        committedBudget.setCommittedOn(new LocalDate(2020,1,1));
    }

    Project project;
    ProjectBudget committedBudget;

    @Inject
    BudgetForecastRepositoryAndFactory budgetForecastRepositoryAndFactory;

    @Inject
    ProjectBudgetRepository projectBudgetRepository;

    final BigDecimal budgetedAmountItem1 = BigDecimal.valueOf(101.50);
    final BigDecimal budgetedAmountItem2 = BigDecimal.valueOf(222.50);

    @Test
    // NOTE: the trade off for having a 'factory' to create a forecast (hiding the creation of forecast items and terms)
    // is a more comprehensive integ test
    public void find_or_create_works() throws Exception {

        // given
        Assertions.assertThat(project.getItems()).hasSize(2);
        Assertions.assertThat(committedBudget.getItems()).hasSize(2);
        Assertions.assertThat(budgetForecastRepositoryAndFactory.listAll()).isEmpty();
        Assertions.assertThat(budgetForecastRepositoryAndFactory.findByProject(project)).isEmpty();

        // when
        final LocalDate forecastDate = new LocalDate(2020, 1, 1);
        final BudgetForecast forecast = budgetForecastRepositoryAndFactory.findOrCreate(project, forecastDate);

        // then
        Assertions.assertThat(budgetForecastRepositoryAndFactory.listAll()).hasSize(1);
        Assertions.assertThat(budgetForecastRepositoryAndFactory.findByProject(project)).contains(forecast);
        Assertions.assertThat(budgetForecastRepositoryAndFactory.findUnique(project, forecastDate)).isEqualTo(forecast);
        Assertions.assertThat(forecast.getFrequency()).isEqualTo(ForecastFrequency.QUARTERLY); // only implementation so far
        Assertions.assertThat(forecast.getDate()).isEqualTo(forecastDate);
        Assertions.assertThat(forecast.getDate()).isEqualTo(forecastDate);
        Assertions.assertThat(forecast.getItems()).hasSize(2);

        final BudgetForecastItem firstForecastItem = forecast.getItems().first();
        Assertions.assertThat(firstForecastItem.getForecast()).isEqualTo(forecast);
        Assertions.assertThat(firstForecastItem.getProjectItem()).isEqualTo(project.getItems().first());
        Assertions.assertThat(firstForecastItem.getAmount()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(firstForecastItem.getInvoicedAmountUntilForecastDate()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(firstForecastItem.getBudgetedAmountUntilForecastDate()).isEqualTo(BigDecimal.ZERO);

        final BudgetForecastItem lastForecastItem = forecast.getItems().last();
        Assertions.assertThat(lastForecastItem.getForecast()).isEqualTo(forecast);
        Assertions.assertThat(lastForecastItem.getProjectItem()).isEqualTo(project.getItems().last());
        Assertions.assertThat(lastForecastItem.getAmount()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(lastForecastItem.getInvoicedAmountUntilForecastDate()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(lastForecastItem.getBudgetedAmountUntilForecastDate()).isEqualTo(BigDecimal.ZERO);

        Assertions.assertThat(firstForecastItem.getTerms()).hasSize(1);
        final BudgetForecastItemTerm firstTermOfForecastItem1 = firstForecastItem.getTerms().first();
        Assertions.assertThat(firstTermOfForecastItem1.getForecastItem()).isEqualTo(firstForecastItem);
        Assertions.assertThat(firstTermOfForecastItem1.getStartDate()).isEqualTo(ForecastFrequency.QUARTERLY.getIntervalFor(forecastDate).startDate());
        Assertions.assertThat(firstTermOfForecastItem1.getEndDate()).isEqualTo(ForecastFrequency.QUARTERLY.getIntervalFor(forecastDate).endDate());
        Assertions.assertThat(firstTermOfForecastItem1.getAmount()).isEqualTo(BigDecimal.ZERO);

        Assertions.assertThat(lastForecastItem.getTerms()).hasSize(1);
        final BudgetForecastItemTerm firstTermOfForecastItem2 = lastForecastItem.getTerms().first();
        Assertions.assertThat(firstTermOfForecastItem2.getForecastItem()).isEqualTo(lastForecastItem);
        Assertions.assertThat(firstTermOfForecastItem2.getStartDate()).isEqualTo(ForecastFrequency.QUARTERLY.getIntervalFor(forecastDate).startDate());
        Assertions.assertThat(firstTermOfForecastItem2.getEndDate()).isEqualTo(ForecastFrequency.QUARTERLY.getIntervalFor(forecastDate).endDate());
        Assertions.assertThat(firstTermOfForecastItem2.getAmount()).isEqualTo(BigDecimal.ZERO);

    }

    @Test
    public void add_terms_works() throws Exception {

        // given
        final LocalDate forecastDate = new LocalDate(2020, 1, 1);
        final BudgetForecast forecast = budgetForecastRepositoryAndFactory.findOrCreate(project, forecastDate);
        assetNumberOfTerms(forecast, 1);

        // when
        budgetForecastRepositoryAndFactory.addTermsUntil(forecast, new LocalDate(2020,3,31));
        // then still
        assetNumberOfTerms(forecast, 1);

        // and when
        final LocalDate nextQuarterStart = new LocalDate(2020, 4, 1);
        budgetForecastRepositoryAndFactory.addTermsUntil(forecast, nextQuarterStart);
        // then
        assetNumberOfTerms(forecast, 2);
        final BudgetForecastItemTerm lastTermSample = forecast.getItems().first().getTerms().last();
        Assertions.assertThat(lastTermSample.getStartDate()).isEqualTo(nextQuarterStart);
        Assertions.assertThat(lastTermSample.getEndDate()).isEqualTo(nextQuarterStart.plusMonths(3).minusDays(1));

        // and when
        final LocalDate middleThirdQuarter = new LocalDate(2020, 8, 15);
        final LocalDate thirdQuarterStart = new LocalDate(2020, 7, 1);
        budgetForecastRepositoryAndFactory.addTermsUntil(forecast, middleThirdQuarter);
        // then
        assetNumberOfTerms(forecast, 3);
        final BudgetForecastItemTerm lastTermSample2 = forecast.getItems().first().getTerms().last();
        Assertions.assertThat(lastTermSample2.getStartDate()).isEqualTo(thirdQuarterStart);
        Assertions.assertThat(lastTermSample2.getEndDate()).isEqualTo(thirdQuarterStart.plusMonths(3).minusDays(1));
    }

    private void assetNumberOfTerms(final BudgetForecast forecast, final int numberOfTerms) {
        Lists.newArrayList(forecast.getItems()).forEach(fi->{
            Assertions.assertThat(fi.getTerms()).hasSize(numberOfTerms);
        });
    }

    @Inject ServiceRegistry2 serviceRegistry2;

}