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

package org.estatio.module.budget.fixtures.partitioning.personas;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;
import org.estatio.module.budget.fixtures.partitioning.PartitioningAndItemsAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;

public class PartitioningAndItemsForOxf extends PartitioningAndItemsAbstract {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.toFixtureScript());
        executionContext.executeChild(this, KeyTable_enum.Oxf2015Area.toFixtureScript());
        executionContext.executeChild(this, KeyTable_enum.Oxf2015Count.toFixtureScript());

        // exec
        Property property = propertyRepository.findPropertyByReference(
                Property_enum.OxfGb.getRef());
        LocalDate startDate = Budget_enum.OxfBudget2015.getStartDate();
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
        Charge incomingCharge1 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_1);
        Charge incomingCharge2 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_2);
        BudgetItem budgetItem1 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge1);
        BudgetItem budgetItem2 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge2);

        Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        KeyTable keyTable1 = keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Oxf2015Area.getName());
        KeyTable keyTable2 = keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Oxf2015Count.getName());


        Partitioning partitioning = createPartitioning(budget, executionContext);

        createPartitionItem(partitioning, charge, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createPartitionItem(partitioning, charge, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createPartitionItem(partitioning, charge, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
    }

}
