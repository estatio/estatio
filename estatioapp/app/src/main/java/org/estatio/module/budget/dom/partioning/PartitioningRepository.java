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
package org.estatio.module.budget.dom.partioning;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;

@DomainService(repositoryFor = PartitionItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class PartitioningRepository extends UdoDomainRepositoryAndFactory<Partitioning> {

    public PartitioningRepository() {
        super(PartitioningRepository.class, Partitioning.class);
    }

    public Partitioning newPartitioning(
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final BudgetCalculationType type) {
        Partitioning partitioning = newTransientInstance(Partitioning.class);
        partitioning.setBudget(budget);
        partitioning.setStartDate(startDate);
        partitioning.setEndDate(endDate);
        partitioning.setType(type);
        persistIfNotAlready(partitioning);
        return partitioning;
    }

    public String validateNewPartitioning(
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final BudgetCalculationType type){
        if (findUnique(budget, type, startDate)!=null){
            return "This partitioning already exists";
        }
        if (type == BudgetCalculationType.BUDGETED && findByBudgetAndType(budget, BudgetCalculationType.BUDGETED).size() > 0){
            return "Only one partitioning of type BUDGETED is supported";
        }
        return null;
    }

    public Partitioning findUnique(final Budget budget, final BudgetCalculationType type, final LocalDate startDate){
        return uniqueMatch("findUnique", "budget", budget, "type", type, "startDate", startDate);
    }

    public List<Partitioning> findByBudgetAndType(final Budget budget, final BudgetCalculationType type){
        return allMatches("findByBudgetAndType", "budget", budget, "type", type);
    }

    public List<Partitioning> allPartitionings() {
        return allInstances();
    }


}
