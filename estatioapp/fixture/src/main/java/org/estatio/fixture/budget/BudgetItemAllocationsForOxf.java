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

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.charge.ChargeRefData;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class BudgetItemAllocationsForOxf extends BudgetItemAllocationAbstact {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new EstatioBaseLineFixture());
            executionContext.executeChild(this, new PropertyForOxfGb());
            executionContext.executeChild(this, new ChargeRefData());
            executionContext.executeChild(this, new KeyTablesForOxf());
            executionContext.executeChild(this, new BudgetsForOxf());
        }

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        LocalDate startDate = new LocalDate(2015, 01, 01);
        LocalDate endDate = new LocalDate(2015, 12, 31);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
        BudgetItem budgetItem1 = budget.getItems().first();
        BudgetItem budgetItem2 = budget.getItems().last();
        Charge charge = chargesRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        KeyTable keyTable1 = keyTableRepository.findByBudget(budget).get(0);
        KeyTable keyTable2 = keyTableRepository.findByBudget(budget).get(1);

        createBudgetItemAllocation(charge, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createBudgetItemAllocation(charge, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createBudgetItemAllocation(charge, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
    }

}
