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

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.capex.dom.project.BudgetForecast;
import org.estatio.module.capex.dom.project.BudgetForecastItem;
import org.estatio.module.capex.dom.project.BudgetForecastItemTerm;
import org.estatio.module.capex.dom.project.BudgetForecastRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;

public class BudgetForecastRepository_IntegTest extends CapexModuleIntegTestAbstract {

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
    BudgetForecastRepository budgetForecastRepository;

    @Test
    public void find_or_create_works() throws Exception {

        // given
        Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry2);
        Assertions.assertThat(budgetForecastRepository.listAll()).isEmpty();
        Assertions.assertThat(budgetForecastRepository.findByProject(project)).isEmpty();

        // when
        final LocalDate date = new LocalDate(2020, 1, 1);
        final BudgetForecast forecast = budgetForecastRepository.findOrCreate(project, date);

        // then
        Assertions.assertThat(budgetForecastRepository.listAll()).hasSize(1);
        Assertions.assertThat(budgetForecastRepository.findByProject(project)).contains(forecast);
        Assertions.assertThat(budgetForecastRepository.findUnique(project, date)).isEqualTo(forecast);

    }

    final BigDecimal amount = BigDecimal.valueOf(123.00);
    final BigDecimal budgetedAmount = BigDecimal.valueOf(101.50);
    final BigDecimal invoicedAmount = BigDecimal.valueOf(99.99);

    @Test
    public void find_or_create_forecast_item_works() throws Exception {

        // given
        Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry2);
        final ProjectItem projectItem = project.getItems().first();
        Assertions.assertThat(projectItem).isNotNull();

        final LocalDate date = new LocalDate(2020, 1, 1);
        final BudgetForecast forecast = budgetForecastRepository.findOrCreate(project, date);
        Assertions.assertThat(forecast).isNotNull();

        Assertions.assertThat(budgetForecastRepository.allForecastItems()).isEmpty();

        // when
        final BudgetForecastItem forecastItem = budgetForecastRepository
                .findOrCreateItem(forecast, projectItem, amount, budgetedAmount, invoicedAmount);

        // then
        Assertions.assertThat(budgetForecastRepository.allForecastItems()).hasSize(1);
        Assertions.assertThat(budgetForecastRepository.findUniqueItem(forecast, projectItem)).isEqualTo(forecastItem);
        Assertions.assertThat(forecastItem.getAmount()).isEqualTo(amount);
        Assertions.assertThat(forecastItem.getBudgetedAmountUntilForecastDate()).isEqualTo(budgetedAmount);
        Assertions.assertThat(forecastItem.getInvoicedAmountUntilForecastDate()).isEqualTo(invoicedAmount);

    }

    @Test
    public void find_or_create_forecast_item_term_works() throws Exception {

        // given
        Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry2);
        final ProjectItem projectItem = project.getItems().first();
        Assertions.assertThat(projectItem).isNotNull();

        final LocalDate date = new LocalDate(2020, 1, 1);
        final BudgetForecast forecast = budgetForecastRepository.findOrCreate(project, date);
        Assertions.assertThat(forecast).isNotNull();

        Assertions.assertThat(budgetForecastRepository.allForecastTerms()).isEmpty();

        final BudgetForecastItem forecastItem = budgetForecastRepository
                .findOrCreateItem(forecast, projectItem, amount, budgetedAmount, invoicedAmount);

        Assertions.assertThat(forecastItem).isNotNull();

        // when
        final LocalDate startDate = new LocalDate(2020, 4, 1);
        final LocalDate endDate = new LocalDate(2020, 6, 30);
        LocalDateInterval interval = LocalDateInterval.including(startDate, endDate);
        final BigDecimal termAmount = BigDecimal.valueOf(1.23);
        final BudgetForecastItemTerm term = budgetForecastRepository
                .findOrCreateTerm(forecastItem, interval, termAmount);

        // then
        Assertions.assertThat(budgetForecastRepository.allForecastTerms()).hasSize(1);
        Assertions.assertThat(budgetForecastRepository.findUniqueTerm(forecastItem, startDate)).isEqualTo(term);
        Assertions.assertThat(term.getEndDate()).isEqualTo(endDate);
        Assertions.assertThat(term.getAmount()).isEqualTo(termAmount);

    }

    @Inject ServiceRegistry2 serviceRegistry2;

}