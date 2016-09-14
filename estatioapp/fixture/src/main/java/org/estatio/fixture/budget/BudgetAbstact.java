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

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class BudgetAbstact extends FixtureScript {


    protected Budget createBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate,
            final ExecutionContext fixtureResults){
        Budget budget = budgetRepository.newBudget(property, startDate, endDate);

        return fixtureResults.addResult(this, budget);
    }

    protected void createBudgetItem(
            final Budget budget,
            final BigDecimal value,
            final Charge charge
    ){
        budgetItemRepository.newBudgetItem(budget, value, charge);
    }

    @Inject
    protected BudgetRepository budgetRepository;

    @Inject
    protected BudgetItemRepository budgetItemRepository;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected ChargeRepository chargeRepository;

}
