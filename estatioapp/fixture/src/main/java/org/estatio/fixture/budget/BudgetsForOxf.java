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
import org.estatio.dom.charge.Charge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.charge.ChargeRefData;

public class BudgetsForOxf extends BudgetAbstact {

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
        final BigDecimal VALUE1 = BigDecimal.valueOf(30000.55);
        final BigDecimal VALUE2 = BigDecimal.valueOf(40000.35);
        final BigDecimal VALUE3 = BigDecimal.valueOf(30500.99);
        final BigDecimal VALUE4 = BigDecimal.valueOf(40600.01);
        final Charge charge1 = chargesRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET1);
        final Charge charge2 = chargesRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET2);

        Budget newBudget1 = createBudget(
                property,
                new LocalDate(2015, 01, 01),
                new LocalDate(2015, 12, 31),
                executionContext);

        createBudgetItem(newBudget1,VALUE1,charge1);
        createBudgetItem(newBudget1,VALUE2,charge2);

        Budget newBudget2 = createBudget(
                property,
                new LocalDate(2016, 01, 01),
                new LocalDate(2016, 12, 31),
                executionContext);

        createBudgetItem(newBudget2,VALUE3,charge1);
        createBudgetItem(newBudget2,VALUE4,charge2);
    }

}
