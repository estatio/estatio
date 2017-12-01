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

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;
import org.estatio.module.budget.fixtures.partitioning.PartitioningAndItemsAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

import static org.incode.module.base.integtests.VT.bd;

public class PartitioningAndItemsForOxfBudget2015 extends PartitioningAndItemsAbstract {

    public static final KeyTable_enum keyTable1_d = KeyTable_enum.Oxf2015Area;
    public static final KeyTable_enum keyTable2_d = KeyTable_enum.Oxf2015Count;

    public static final Budget_enum budget_d = Budget_enum.OxfBudget2015;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, keyTable1_d.toBuilderScript());
        executionContext.executeChild(this, keyTable2_d.toBuilderScript());

        // all this stuff set up by prereqs
        Budget budget = budget_d.findUsing(serviceRegistry);

        Charge incomingCharge1 = budget_d.getCharge1_d().findUsing(serviceRegistry);
        Charge incomingCharge2 = budget_d.getCharge2_d().findUsing(serviceRegistry);

        BudgetItem budgetItem1 = budget.findByCharge(incomingCharge1);
        BudgetItem budgetItem2 = budget.findByCharge(incomingCharge2);

        KeyTable keyTable1 = KeyTable_enum.Oxf2015Area.findUsing(serviceRegistry);
        KeyTable keyTable2 = KeyTable_enum.Oxf2015Count.findUsing(serviceRegistry);


        Charge charge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);


        Partitioning partitioning = createPartitioning(budget, executionContext);

        createPartitionItem(partitioning, charge, keyTable1, budgetItem1, bd(100), executionContext);
        createPartitionItem(partitioning, charge, keyTable1, budgetItem2, bd(80), executionContext);
        createPartitionItem(partitioning, charge, keyTable2, budgetItem2, bd(20), executionContext);
    }

}
