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

import org.estatio.dom.asset.Properties;
import org.estatio.dom.budget.Budget;
import org.estatio.dom.budget.BudgetCostGroup;
import org.estatio.dom.budget.BudgetItem;
import org.estatio.dom.budget.BudgetItems;
import org.estatio.dom.budget.BudgetKeyTable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;
import org.estatio.fixture.EstatioFixtureScript;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class BudgetItemAbstact extends EstatioFixtureScript {


    protected BudgetItem createBudgetItem(
            final Budget budget,
            final BudgetKeyTable budgetKeyTable,
            final BigDecimal value,
            final Currency currency,
            final Charge charge,
            final BudgetCostGroup budgetCostGroup,
            final ExecutionContext fixtureResults
    ){
        BudgetItem budgetItem = budgetItems.newBudgetItem(
                budget,
                budgetKeyTable,
                value,
//                currency,
                charge,
                budgetCostGroup);
        return fixtureResults.addResult(this, budgetItem);
    }

    @Inject
    protected BudgetItems budgetItems;

    @Inject
    protected Properties properties;
}
