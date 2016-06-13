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

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.charge.ChargeRefData;

public class BudgetsForOxf extends BudgetAbstact {

    public static final LocalDate BUDGET_2015_START_DATE = new LocalDate(2015, 01, 01);
    public static final LocalDate BUDGET_2016_START_DATE = new LocalDate(2016, 01, 01);

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new EstatioBaseLineFixture());
            executionContext.executeChild(this, new PropertyForOxfGb());
            executionContext.executeChild(this, new ChargeRefData());
        }

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

        createBudget(executionContext, property, BigDecimal.valueOf(30000.55), BigDecimal.valueOf(40000.35), BUDGET_2015_START_DATE);
       createBudget(executionContext, property, BigDecimal.valueOf(30500.99), BigDecimal.valueOf(40600.01), BUDGET_2016_START_DATE);
    }

    private void createBudget(final ExecutionContext executionContext, final Property property, final BigDecimal value1, final BigDecimal value2, final LocalDate budgetStartDate) {
        Budget newBudget1 = createBudget(
                property,
                budgetStartDate,
                budgetStartDate.plusYears(1).minusDays(1),
                executionContext);

        createBudgetItem(newBudget1,value1, chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET1));
        createBudgetItem(newBudget1,value2, chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET2));
    }

}
