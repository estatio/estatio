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
import org.estatio.fixture.asset.PropertyForBudNl;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeasesForBudNl;

public class BudgetForBud extends BudgetAbstact {

    public static final LocalDate BUDGET_2015_START_DATE = new LocalDate(2015, 01, 01);

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new LeasesForBudNl());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForBudNl.REF);

        createBudget(executionContext, property, BigDecimal.valueOf(10000.00), BigDecimal.valueOf(20000.00), BigDecimal.valueOf(30000.00), BUDGET_2015_START_DATE);
    }

    private void createBudget(final ExecutionContext executionContext, final Property property, final BigDecimal value1, final BigDecimal value2, final BigDecimal value3, final LocalDate budgetStartDate) {
        Budget newBudget = createBudget(
                property,
                budgetStartDate,
                budgetStartDate.plusYears(1).minusDays(1),
                executionContext);

        createBudgetItem(newBudget,value1, chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_1));
        createBudgetItem(newBudget,value2, chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_2));
        createBudgetItem(newBudget,value3, chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_3));
    }

}
