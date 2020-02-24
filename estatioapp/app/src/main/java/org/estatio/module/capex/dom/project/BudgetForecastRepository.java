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
package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = BudgetForecast.class, nature = NatureOfService.DOMAIN)
public class BudgetForecastRepository extends UdoDomainRepositoryAndFactory<BudgetForecast> {

    /*
     * NOTE: since budget forecast, its items and terms are very tightly coupled, as an experiment, this repo handles them all
     */

    public BudgetForecastRepository() {
        super(BudgetForecastRepository.class, BudgetForecast.class);
    }

    public List<BudgetForecast> listAll() {
        return allInstances();
    }

    public BudgetForecast findUnique(final Project project, final LocalDate date) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        BudgetForecast.class,
                        "findUnique",
                        "project", project,
                        "date", date));
    }

    public List<BudgetForecast> findByProject(final Project project) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        BudgetForecast.class,
                        "findByProject",
                        "project", project));
    }

    public BudgetForecast create(
            final Project project,
            final LocalDate date) {
        BudgetForecast forecast = new BudgetForecast(project, date);
        serviceRegistry2.injectServicesInto(forecast);
        repositoryService.persistAndFlush(forecast);
        return forecast;
    }

    public BudgetForecast findOrCreate(
            final Project project,
            final LocalDate date) {
        BudgetForecast forecast = findUnique(project, date);
        if(forecast == null) {
            forecast = create(project, date);
        }
        return forecast;
    }

    // SECTION forecast item

    public List<BudgetForecastItem> allForecastItems(){
        return repositoryService.allInstances(BudgetForecastItem.class);
    }

    public BudgetForecastItem findUniqueItem(final BudgetForecast forecast, final ProjectItem projectItem) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        BudgetForecastItem.class,
                        "findUnique",
                        "forecast", forecast,
                        "projectItem", projectItem));
    }

    public BudgetForecastItem createItem(
            final BudgetForecast forecast,
            final ProjectItem projectItem,
            final BigDecimal amount,
            final BigDecimal budgetedAmount,
            final BigDecimal invoicedAmount) {
        BudgetForecastItem item = new BudgetForecastItem(forecast, projectItem, amount, budgetedAmount, invoicedAmount);
        serviceRegistry2.injectServicesInto(item);
        repositoryService.persistAndFlush(item);
        return item;
    }

    public BudgetForecastItem findOrCreateItem(
            final BudgetForecast forecast,
            final ProjectItem projectItem,
            final BigDecimal amount,
            final BigDecimal budgetedAmount,
            final BigDecimal invoicedAmount) {
        BudgetForecastItem item = findUniqueItem(forecast, projectItem);
        if(item == null) {
            item = createItem(forecast, projectItem, amount, budgetedAmount, invoicedAmount);
        }
        return item;
    }

    // SECTION forecast term

    public List<BudgetForecastItemTerm> allForecastTerms(){
        return repositoryService.allInstances(BudgetForecastItemTerm.class);
    }

    public BudgetForecastItemTerm findUniqueTerm(final BudgetForecastItem item, final LocalDate startDate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        BudgetForecastItemTerm.class,
                        "findUnique",
                        "forecastItem", item,
                        "startDate", startDate));
    }

    public BudgetForecastItemTerm createTerm(
            final BudgetForecastItem item,
            final LocalDateInterval interval,
            final BigDecimal amount) {
        BudgetForecastItemTerm term = new BudgetForecastItemTerm(item, interval, amount);
        serviceRegistry2.injectServicesInto(term);
        repositoryService.persistAndFlush(term);
        return term;
    }

    public BudgetForecastItemTerm findOrCreateTerm(
            final BudgetForecastItem item,
            final LocalDateInterval interval,
            final BigDecimal amount) {
        BudgetForecastItemTerm term = findUniqueTerm(item, interval.startDate());
        if(term == null) {
            term = createTerm(item, interval, amount);
        }
        return term;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
