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

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;
import org.estatio.module.budget.fixtures.partitioning.PartitioningAndItemsAbstract;
import org.estatio.module.budget.fixtures.keytables.personas.KeyTablesForBud;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

public class PartitioningAndItemsForBudBudget2015 extends PartitioningAndItemsAbstract {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new KeyTablesForBud());


        // exec
        Budget budget = Budget_enum.BudBudget2015.findUsing(serviceRegistry);

        Charge incomingCharge1 = Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry);
        Charge incomingCharge2 = Charge_enum.NlIncomingCharge2.findUsing(serviceRegistry);
        Charge incomingCharge3 = Charge_enum.NlIncomingCharge3.findUsing(serviceRegistry);
        BudgetItem budgetItem1 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge1);
        BudgetItem budgetItem2 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge2);
        BudgetItem budgetItem3 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge3);
        Charge invoiceCharge1 = Charge_enum.NlServiceCharge.findUsing(serviceRegistry);
        Charge invoiceCharge2 = Charge_enum.NlServiceCharge2.findUsing(serviceRegistry);
        KeyTable keyTable1 = keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Area.getName());
        KeyTable keyTable2 = keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Count.getName());

        Partitioning partitioning = createPartitioning(budget, executionContext);

        createPartitionItem(partitioning, invoiceCharge1, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createPartitionItem(partitioning, invoiceCharge1, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createPartitionItem(partitioning, invoiceCharge1, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
        createPartitionItem(partitioning, invoiceCharge2, keyTable1, budgetItem3, new BigDecimal(90), executionContext);
        createPartitionItem(partitioning, invoiceCharge1, keyTable2, budgetItem3, new BigDecimal(10), executionContext);
    }

}
