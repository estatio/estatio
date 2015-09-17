/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.fixture.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.dom.budgeting.budgetitem.BudgetItems;
import org.estatio.dom.charge.Charge;
import org.estatio.fixture.EstatioFixtureScript;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class BudgetAbstact extends EstatioFixtureScript {


    protected Budget createBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal value,
            final Charge charge,
            final ExecutionContext fixtureResults){
        Budget budget = budgets.newBudget(property, startDate, endDate);
        createBudgetItem(budget, value, charge);

        return fixtureResults.addResult(this, budget);
    }

    private void createBudgetItem(
            final Budget budget,
            final BigDecimal value,
            final Charge charge
    ){
        budgetItems.newBudgetItem(budget, value, charge);

    }

    @Inject
    protected Budgets budgets;

    @Inject
    protected BudgetItems budgetItems;

    @Inject
    PropertyRepository propertyRepository;
    @Inject
    protected PropertyMenu propertyMenu;
}
