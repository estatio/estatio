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
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTables;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(repositoryFor = BudgetItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetItems extends UdoDomainRepositoryAndFactory<BudgetItem> {

    public BudgetItems() {
        super(BudgetItems.class, BudgetItem.class);
    }

    // //////////////////////////////////////

    public BudgetItem newBudgetItem(
            final Budget budget,
            final BigDecimal value,
            final Charge charge) {
        BudgetItem budgetItem = newTransientInstance();
        budgetItem.setBudget(budget);
        budgetItem.setValue(value);
        budgetItem.setCharge(charge);

        persistIfNotAlready(budgetItem);

        return budgetItem;
    }


    public String validateNewBudgetItem(
            final Budget budget,
            final BigDecimal value,
            final Charge charge) {
        if (value.equals(new BigDecimal(0))) {
            return "Value can't be zero";
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

    @Inject
    BudgetKeyTables budgetKeyTables;

    @Inject
    Currencies currencies;

    @Inject
    Budgets budgets;
}
