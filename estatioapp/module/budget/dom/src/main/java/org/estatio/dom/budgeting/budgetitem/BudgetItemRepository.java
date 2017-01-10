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
package org.estatio.dom.budgeting.budgetitem;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;

@DomainService(repositoryFor = BudgetItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class BudgetItemRepository extends UdoDomainRepositoryAndFactory<BudgetItem> {

    public BudgetItemRepository() {
        super(BudgetItemRepository.class, BudgetItem.class);
    }

    public BudgetItem newBudgetItem(
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge) {
        BudgetItem budgetItem = newBudgetItem(budget, charge);
        budgetItem.newValue(budgetedValue, budget.getStartDate(), BudgetCalculationType.BUDGETED);
        return budgetItem;
    }

    public String validateNewBudgetItem(
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge) {
        if (budgetedValue==null){return "Value cannot be empty";}
        return validateNewBudgetItem(budget, charge);
    }

    public BudgetItem newBudgetItem(
            final Budget budget,
            final Charge charge) {
        BudgetItem budgetItem = newTransientInstance();
        budgetItem.setBudget(budget);
        budgetItem.setCharge(charge);
        persistIfNotAlready(budgetItem);

        return budgetItem;
    }

    public String validateNewBudgetItem(
            final Budget budget,
            final Charge charge) {
        if (findByBudgetAndCharge(budget, charge)!=null) {
            return "There is already an item with this charge.";
        }
        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout()
    public List<BudgetItem> allBudgetItems() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<BudgetItem> findByBudget(final Budget budget) {
        return allMatches("findByBudget", "budget", budget);
    }

    @Programmatic
    public BudgetItem findByBudgetAndCharge(
            final Budget budget,
            final Charge charge
    ){
        return uniqueMatch("findByBudgetAndCharge", "budget", budget, "charge", charge);
    }

    @Programmatic
    public BudgetItem findByPropertyAndChargeAndStartDate(final Property property, final Charge charge, final LocalDate startDate) {
        return uniqueMatch("findByPropertyAndChargeAndStartDate", "property", property, "charge", charge, "startDate", startDate);
    }

    @Programmatic
    public BudgetItem findOrCreateBudgetItem(final Budget budget, final Charge budgetItemCharge) {
        BudgetItem budgetItem = findByBudgetAndCharge(budget, budgetItemCharge);
        if (budgetItem == null){
            return newBudgetItem(budget, budgetItemCharge);
        }
        return budgetItem;
    }

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    private BudgetItemValueRepository budgetItemValueRepository;

}
