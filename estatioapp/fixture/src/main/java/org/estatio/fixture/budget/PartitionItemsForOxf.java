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
import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.dom.charge.Charge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.charge.ChargeRefData;

public class PartitionItemsForOxf extends PartitionItemAbstact {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new PropertyForOxfGb());
        executionContext.executeChild(this, new KeyTablesForOxf());
        executionContext.executeChild(this, new PartitioningForOxf());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        LocalDate startDate = new LocalDate(2015, 01, 01);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
        BudgetItem budgetItem1 = budget.getItems().first();
        BudgetItem budgetItem2 = budget.getItems().last();
        Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        final List<KeyTable> keyTables = keyTableRepository.findByBudget(budget);
        KeyTable keyTable1 = keyTables.get(0);
        KeyTable keyTable2 = keyTables.get(1);
        Partitioning partitioning = budget.getPartitionings().first();

        createPartitioningAndItem(partitioning, charge, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createPartitioningAndItem(partitioning, charge, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createPartitioningAndItem(partitioning, charge, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
    }

}
