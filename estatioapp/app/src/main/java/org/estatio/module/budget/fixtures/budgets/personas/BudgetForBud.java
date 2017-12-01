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

import org.estatio.module.budget.fixtures.budgets.BudgetAbstract;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;

public class BudgetForBud extends BudgetAbstract {

    public static final Budget_enum data = Budget_enum.BudBudget2015;

    public static final LocalDate BUDGET_2015_START_DATE = Budget_enum.BudBudget2015.getStartDate();
    public static final LocalDate BUDGET_2015_END_DATE =  Budget_enum.BudBudget2015.getStartDate().plusYears(1).minusDays(1);

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChildT(this, data.toBuilderScript());
    }

//    @Override
//    protected void execute(ExecutionContext executionContext) {
//
//        // prereqs
//        executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudHello003Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudDago004Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudHello006Nl.toBuilderScript());
//
//        // exec
//        Property property = Property_enum.BudNl.findUsing(serviceRegistry);
//
//        createBudget(executionContext, property, bd(10000.00), bd(20000.00), bd(30000.00), BUDGET_2015_START_DATE);
//    }
//
//    private void createBudget(final ExecutionContext executionContext, final Property property, final BigDecimal value1, final BigDecimal value2, final BigDecimal value3, final LocalDate budgetStartDate) {
//        Budget newBudget = createBudget(
//                property,
//                budgetStartDate,
//                budgetStartDate.plusYears(1).minusDays(1),
//                executionContext);
//
//        BudgetItem item1 = createBudgetItem(newBudget, Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry));
//        BudgetItem item2 = createBudgetItem(newBudget, Charge_enum.NlIncomingCharge2.findUsing(serviceRegistry));
//        BudgetItem item3 = createBudgetItem(newBudget, Charge_enum.NlIncomingCharge3.findUsing(serviceRegistry));
//
//        createBudgetItemValue(item1, value1, budgetStartDate, BudgetCalculationType.BUDGETED);
//        createBudgetItemValue(item2, value2, budgetStartDate, BudgetCalculationType.BUDGETED);
//        createBudgetItemValue(item3, value3, budgetStartDate, BudgetCalculationType.BUDGETED);
//    }

}
