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
package org.estatio.dom.budgeting.budget;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import java.util.List;

@DomainService(repositoryFor = Budget.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class BudgetRepository extends UdoDomainRepositoryAndFactory<Budget> {

    public BudgetRepository() {
        super(BudgetRepository.class, Budget.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Budget newBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {
        Budget budget = newTransientInstance();
        budget.setProperty(property);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        persistIfNotAlready(budget);

        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {

        if (startDate == null) {
            return "Start date is mandatory";
        }

        if (!new LocalDateInterval(startDate, endDate).isValid()) {
            return "End date can not be before start date";
        }

        for (Budget budget : this.findByProperty(property)) {
            if (budget.getInterval().overlaps(new LocalDateInterval(startDate, endDate))) {
                return "A budget cannot overlap an existing budget.";
            }
        }

        return null;
    }

    @Programmatic
    public Budget findOrCreateBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {

        if (findByPropertyAndStartDate(property, startDate)!= null){
            return findByPropertyAndStartDate(property, startDate);
        } else {
            return newBudget(property,startDate,endDate);
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Budget> allBudgets() {
        return allInstances();
    }

    @Programmatic
    public List<Budget> findByProperty(Property property){
        return allMatches("findByProperty", "property", property);
    }

    /*
    returns first budget of a property with date between budget start and end
     */
    @Programmatic
    public Budget findByPropertyAndDate(Property property, LocalDate date){
        List<Budget> allBudgetsOfProperty = allMatches("findByProperty", "property", property);
        for (Budget budget : allBudgetsOfProperty) {
            LocalDateInterval budgetInterval = new LocalDateInterval(budget.getStartDate(), budget.getEndDate());
            if (budgetInterval.contains(date)) {
                return budget;
            }
        }
        return null;
    }

    @Programmatic
    public Budget findByPropertyAndStartDate(Property property, LocalDate startDate){
        return uniqueMatch("findByPropertyAndStartDate", "property", property, "startDate", startDate);
    }
}
