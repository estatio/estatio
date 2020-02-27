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

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = BudgetForecast.class, nature = NatureOfService.DOMAIN)
public class BudgetForecastRepositoryAndFactory extends UdoDomainRepositoryAndFactory<BudgetForecast> {

    /*
     * NOTE: since budget forecast, its items and terms are very tightly coupled, as an experiment, this repo acts as a factory as well
     */

    public BudgetForecastRepositoryAndFactory() {
        super(BudgetForecastRepositoryAndFactory.class, BudgetForecast.class);
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
        BudgetForecast forecast = null;
        try {
            if (project.getProjectBudget()==null) {
                throw new IllegalArgumentException(String.format("No project budget found for %s", project.getReference()));
            }
            final ProjectBudget latestCommittedBudget = project.getLatestCommittedBudget();
            if (latestCommittedBudget ==null){
                throw new IllegalArgumentException(String.format("Budget for %s is not committed", project.getReference()));
            }
            forecast = new BudgetForecast(project, date);
            forecast.setCreatedOn(getClockService().now());
            serviceRegistry2.injectServicesInto(forecast);
            repositoryService.persistAndFlush(forecast);

            for (ProjectBudgetItem item : latestCommittedBudget.getItems()){
                final BudgetForecastItem newForecastItem = findOrCreateItem(forecast, item.getProjectItem(), BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO);
                findOrCreateFirstTerm(newForecastItem);
            }
        } catch (IllegalArgumentException e) {
            messageService2.raiseError(e.getMessage());
        }
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

    public void addTermsUntil(final BudgetForecast forecast, final LocalDate date) {
        Lists.newArrayList(forecast.getItems()).forEach(fi->{
            addTermsUntil(fi, date);
        });
    }

    private void addTermsUntil(final BudgetForecastItem forecastItem, final LocalDate date){
        final LocalDate lastTermStartDate = forecastItem.getForecast().getFrequency().getStartDateFor(date);
        final BudgetForecastTerm term = findOrCreateFirstTerm(forecastItem);
        LocalDate d = term.getEndDate().plusDays(1);
        while (!d.isAfter(lastTermStartDate)){
            BudgetForecastTerm nextTerm = findOrCreateNext(forecastItem, d);
            d = nextTerm.getEndDate().plusDays(1);
        }
    }

    private BudgetForecastTerm findOrCreateNext(final BudgetForecastItem item, final LocalDate nextStartDate){
        BudgetForecastTerm term = findUniqueTerm(item, nextStartDate);
        if (term != null){
            return term;
        } else {
            final LocalDate previousStartDate = item.getForecast().getFrequency()
                    .getStartDateFor(nextStartDate.minusDays(1));
            BudgetForecastTerm previousIfAny = findUniqueTerm(item, previousStartDate);
            LocalDateInterval nextInterval = item.getForecast().getFrequency().getIntervalFor(nextStartDate);
            BudgetForecastTerm nextTerm = new BudgetForecastTerm();
            nextTerm.setForecastItem(item);
            nextTerm.setStartDate(nextInterval.startDate());
            nextTerm.setEndDate(nextInterval.endDate(AbstractInterval.IntervalEnding.INCLUDING_END_DATE));
            nextTerm.setAmount(BigDecimal.ZERO);
            serviceRegistry2.injectServicesInto(nextTerm);
            repositoryService.persistAndFlush(nextTerm);
            // the following is done after injecting serves and persisting in order for datanucleus to manage next/prev relation
            nextTerm.setPrevious(previousIfAny);
            if (previousIfAny != null) previousIfAny.setNext(nextTerm);
            return nextTerm;
        }
    }

    // SECTION forecast item

    private BudgetForecastItem findUniqueItem(final BudgetForecast forecast, final ProjectItem projectItem) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        BudgetForecastItem.class,
                        "findUnique",
                        "forecast", forecast,
                        "projectItem", projectItem));
    }

    private BudgetForecastItem createItem(
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

    private BudgetForecastItem findOrCreateItem(
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

    private BudgetForecastTerm findUniqueTerm(final BudgetForecastItem item, final LocalDate startDate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        BudgetForecastTerm.class,
                        "findUnique",
                        "forecastItem", item,
                        "startDate", startDate));
    }

    private BudgetForecastTerm findOrCreateFirstTerm(
            final BudgetForecastItem item) {
        final LocalDate forecastDate = item.getForecast().getDate();
        BudgetForecastTerm term = findUniqueTerm(item, forecastDate);
        if(term == null) {
            term = new BudgetForecastTerm(item, item.getForecast().getFrequency().getIntervalFor(forecastDate), BigDecimal.ZERO);
            serviceRegistry2.injectServicesInto(term);
            repositoryService.persistAndFlush(term);
        }
        return term;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    MessageService2 messageService2;
}
