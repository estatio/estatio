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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = BudgetForPeriod.class, nature = NatureOfService.DOMAIN)
public class BudgetForPeriodRepository extends UdoDomainRepositoryAndFactory<BudgetForPeriod> {

    public BudgetForPeriodRepository() {
        super(BudgetForPeriodRepository.class, BudgetForPeriod.class);
    }

    @Programmatic
    public List<BudgetForPeriod> listAll() {
        return allInstances();
    }

    @Programmatic
    public List<BudgetForPeriod> findProject(final Project project) {
        return allMatches("findByProject", "project", project).stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    @Programmatic
    public BudgetForPeriod findUnique(final Project project, final LocalDate startDate) {
        return uniqueMatch("findByProjectAndStartDate", "project", project, "startDate", startDate);
    }

    @Programmatic
    public BudgetForPeriod create(
            final Project project,
            final BigDecimal amount,
            final LocalDate startDate,
            final LocalDate endDate) {

        BudgetForPeriod budget = repositoryService.instantiate(BudgetForPeriod.class);
        budget.setProject(project);
        budget.setAmount(amount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        repositoryService.persist(budget);

        return budget;
    }

    @Programmatic
    public BudgetForPeriod findOrCreate(
            final Project project,
            final BigDecimal amount,
            final LocalDate startDate,
            final LocalDate endDate) {
        BudgetForPeriod budget = findUnique(project, startDate);
        if(budget == null) {
            budget = create(project, amount, startDate, endDate);
        }
        return budget;
    }

    @Inject
    RepositoryService repositoryService;
}
