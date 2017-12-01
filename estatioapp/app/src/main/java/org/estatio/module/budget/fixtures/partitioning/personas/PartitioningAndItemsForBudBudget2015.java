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

import org.estatio.module.budget.fixtures.partitioning.PartitioningAndItemsAbstract;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;

public class PartitioningAndItemsForBudBudget2015 extends PartitioningAndItemsAbstract {

    public static Partitioning_enum data = Partitioning_enum.BudPartitioning2015;

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChildT(this, data.toBuilderScript());
        
    }
//    @Override
//    protected void execute(ExecutionContext executionContext) {
//
//        // prereqs
//        executionContext.executeChildT(this, KeyTable_enum.Bud2015Area.toBuilderScript());
//        executionContext.executeChildT(this, KeyTable_enum.Bud2015Count.toBuilderScript());
//
//
//        // exec
//        Budget budget = Budget_enum.BudBudget2015.findUsing(serviceRegistry);
//
//        Partitioning partitioning = createPartitioning(budget, executionContext);
//
//        createPartitionItem(
//                partitioning,
//                Charge_enum.NlServiceCharge.findUsing(serviceRegistry),
//                keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Area.getName()),
//                budgetItemRepository.findByBudgetAndCharge(budget, Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry)),
//                bd(100),
//                executionContext);
//
//        createPartitionItem(
//                partitioning,
//                Charge_enum.NlServiceCharge.findUsing(serviceRegistry),
//                keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Area.getName()),
//                budgetItemRepository.findByBudgetAndCharge(budget, Charge_enum.NlIncomingCharge2.findUsing(serviceRegistry)),
//                bd(80),
//                executionContext);
//
//        createPartitionItem(
//                partitioning,
//                Charge_enum.NlServiceCharge.findUsing(serviceRegistry),
//                keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Count.getName()),
//                budgetItemRepository.findByBudgetAndCharge(budget, Charge_enum.NlIncomingCharge2.findUsing(serviceRegistry)),
//                bd(20),
//                executionContext);
//
//        createPartitionItem(
//                partitioning,
//                Charge_enum.NlServiceCharge2.findUsing(serviceRegistry),
//                keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Area.getName()),
//                budgetItemRepository.findByBudgetAndCharge(budget, Charge_enum.NlIncomingCharge3.findUsing(serviceRegistry)),
//                bd(90),
//                executionContext);
//
//        createPartitionItem(
//                partitioning, Charge_enum.NlServiceCharge.findUsing(serviceRegistry),
//                keyTableRepository.findByBudgetAndName(budget, KeyTable_enum.Bud2015Count.getName()),
//                budgetItemRepository.findByBudgetAndCharge(budget, Charge_enum.NlIncomingCharge3.findUsing(serviceRegistry)),
//                bd(10),
//                executionContext);
//    }

}
