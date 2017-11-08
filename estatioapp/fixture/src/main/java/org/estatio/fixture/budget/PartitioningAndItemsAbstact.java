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

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.PropertyRepository;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budget.BudgetRepository;
import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
import org.estatio.module.budgeting.dom.keytable.KeyTableRepository;
import org.estatio.module.budgeting.dom.partioning.PartitionItem;
import org.estatio.module.budgeting.dom.partioning.PartitionItemRepository;
import org.estatio.module.budgeting.dom.partioning.Partitioning;
import org.estatio.module.budgeting.dom.partioning.PartitioningRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

public abstract class PartitioningAndItemsAbstact extends FixtureScript {

    protected Partitioning createPartitioning(final Budget budget, final ExecutionContext executionContext){
        Partitioning partitioning = partitioningRepository.newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.BUDGETED);
        return executionContext.addResult(this, partitioning);
    }

    protected PartitionItem createPartitionItem(
            final Partitioning partitioning,
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage,
            final ExecutionContext fixtureResults
    ){
        PartitionItem partitionItem = partitionItemRepository.newPartitionItem(partitioning, charge,keyTable,budgetItem, percentage);
        return fixtureResults.addResult(this, partitionItem);
    }

    @Inject
    protected PartitioningRepository partitioningRepository;

    @Inject
    protected PartitionItemRepository partitionItemRepository;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected BudgetRepository budgetRepository;

    @Inject
    protected BudgetItemRepository budgetItemRepository;

    @Inject
    protected ChargeRepository chargeRepository;

    @Inject
    protected KeyTableRepository keyTableRepository;

}
