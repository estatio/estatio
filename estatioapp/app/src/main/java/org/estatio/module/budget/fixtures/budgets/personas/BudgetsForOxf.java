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

package org.estatio.module.budget.fixtures.budgets.personas;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;

public class BudgetsForOxf extends FixtureScript {

    public static final LocalDate BUDGET_2015_START_DATE = Budget_enum.OxfBudget2015.getStartDate();
    public static final LocalDate BUDGET_2016_START_DATE = Budget_enum.OxfBudget2016.getStartDate();

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new BudgetForOxf2015());
        executionContext.executeChild(this, new BudgetForOxf2016());

    }
    
}
