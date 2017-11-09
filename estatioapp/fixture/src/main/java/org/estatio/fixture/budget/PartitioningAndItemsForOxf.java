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

import org.estatio.module.asset.dom.Property;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.charge.fixtures.ChargeRefData;

public class PartitioningAndItemsForOxf extends PartitioningAndItemsAbstact {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
        executionContext.executeChild(this, new KeyTablesForOxf());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForOxfGb.REF);
        LocalDate startDate = BudgetsForOxf.BUDGET_2015_START_DATE;
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
        Charge incomingCharge1 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_1);
        Charge incomingCharge2 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_2);
        BudgetItem budgetItem1 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge1);
        BudgetItem budgetItem2 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge2);

        Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        KeyTable keyTable1 = keyTableRepository.findByBudgetAndName(budget, KeyTablesForOxf.NAME_BY_AREA);
        KeyTable keyTable2 = keyTableRepository.findByBudgetAndName(budget, KeyTablesForOxf.NAME_BY_COUNT);


        Partitioning partitioning = createPartitioning(budget, executionContext);

        createPartitionItem(partitioning, charge, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createPartitionItem(partitioning, charge, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createPartitionItem(partitioning, charge, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
    }

}
