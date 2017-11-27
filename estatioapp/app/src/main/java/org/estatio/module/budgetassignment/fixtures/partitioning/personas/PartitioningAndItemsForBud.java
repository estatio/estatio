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

package org.estatio.module.budgetassignment.fixtures.partitioning.personas;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForBudNl;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.fixtures.partitioning.PartitioningAndItemsAbstract;
import org.estatio.module.budgetassignment.fixtures.budget.personas.BudgetForBud;
import org.estatio.module.budgetassignment.fixtures.keytables.personas.KeyTablesForBud;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;

public class PartitioningAndItemsForBud extends PartitioningAndItemsAbstract {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new KeyTablesForBud());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyAndUnitsAndOwnerAndManagerForBudNl.REF);
        LocalDate startDate = BudgetForBud.BUDGET_2015_START_DATE;
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
        Charge incomingCharge1 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_1);
        Charge incomingCharge2 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_2);
        Charge incomingCharge3 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_3);
        BudgetItem budgetItem1 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge1);
        BudgetItem budgetItem2 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge2);
        BudgetItem budgetItem3 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge3);
        Charge invoiceCharge1 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE);
        Charge invoiceCharge2 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2);
        KeyTable keyTable1 = keyTableRepository.findByBudgetAndName(budget, KeyTablesForBud.NAME_BY_AREA);
        KeyTable keyTable2 = keyTableRepository.findByBudgetAndName(budget, KeyTablesForBud.NAME_BY_COUNT);

        Partitioning partitioning = createPartitioning(budget, executionContext);

        createPartitionItem(partitioning, invoiceCharge1, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createPartitionItem(partitioning, invoiceCharge1, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createPartitionItem(partitioning, invoiceCharge1, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
        createPartitionItem(partitioning, invoiceCharge2, keyTable1, budgetItem3, new BigDecimal(90), executionContext);
        createPartitionItem(partitioning, invoiceCharge1, keyTable2, budgetItem3, new BigDecimal(10), executionContext);
    }

}
