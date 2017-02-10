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

import org.estatio.asset.dom.PropertyRepository;
import org.estatio.budget.dom.budget.Budget;
import org.estatio.budget.dom.budget.BudgetRepository;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.budget.dom.budgetitem.BudgetItem;
import org.estatio.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.budget.dom.keytable.KeyTable;
import org.estatio.budget.dom.keytable.KeyTableRepository;
import org.estatio.budget.dom.partioning.PartitionItem;
import org.estatio.budget.dom.partioning.PartitionItemRepository;
import org.estatio.budget.dom.partioning.Partitioning;
import org.estatio.budget.dom.partioning.PartitioningRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;

public abstract class PartitionItemAbstact extends FixtureScript {

    protected Partitioning createPartitioning(final Budget budget, final ExecutionContext executionContext){
        Partitioning partitioning = partitioningRepository.newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.BUDGETED);
        return executionContext.addResult(this, partitioning);
    }

    protected PartitionItem createPartitioningAndItem(
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
