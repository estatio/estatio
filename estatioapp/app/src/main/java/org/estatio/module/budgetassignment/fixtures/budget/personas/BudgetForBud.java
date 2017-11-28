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

package org.estatio.module.budgetassignment.fixtures.budget.personas;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForBudNl;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.fixtures.budgets.BudgetAbstract;
import org.estatio.module.budgetassignment.fixtures.overrides.personas.BudgetOverridesForBud;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;
import org.estatio.module.lease.fixtures.lease.LeasesForBudNl;

public class BudgetForBud extends BudgetAbstract {

    public static final LocalDate BUDGET_2015_START_DATE = new LocalDate(2015, 01, 01);
    public static final LocalDate BUDGET_2015_END_DATE = BUDGET_2015_START_DATE.plusYears(1).minusDays(1);

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeasesForBudNl());
        executionContext.executeChild(this, new BudgetOverridesForBud());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyAndUnitsAndOwnerAndManagerForBudNl.REF);

        createBudget(executionContext, property, BigDecimal.valueOf(10000.00), BigDecimal.valueOf(20000.00), BigDecimal.valueOf(30000.00), BUDGET_2015_START_DATE);
    }

    private void createBudget(final ExecutionContext executionContext, final Property property, final BigDecimal value1, final BigDecimal value2, final BigDecimal value3, final LocalDate budgetStartDate) {
        Budget newBudget = createBudget(
                property,
                budgetStartDate,
                budgetStartDate.plusYears(1).minusDays(1),
                executionContext);

        BudgetItem item1 = createBudgetItem(newBudget,chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_1));
        BudgetItem item2 = createBudgetItem(newBudget,chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_2));
        BudgetItem item3 = createBudgetItem(newBudget,chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_3));

        createBudgetItemValue(item1, value1, budgetStartDate, BudgetCalculationType.BUDGETED);
        createBudgetItemValue(item2, value2, budgetStartDate, BudgetCalculationType.BUDGETED);
        createBudgetItemValue(item3, value3, budgetStartDate, BudgetCalculationType.BUDGETED);
    }

}
