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

package org.estatio.module.budget.fixtures.partitioning.builders;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.dom.partioning.PartitioningRepository;
import org.estatio.module.charge.dom.Charge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"budget", "startDate", "endDate"}, callSuper = false)
@ToString(of={"budget", "startDate", "endDate"})
@Accessors(chain = true)
public class PartitioningBuilder extends BuilderScriptAbstract<Partitioning,PartitioningBuilder> {


    @Getter @Setter
    Budget budget;
    @Getter @Setter
    LocalDate startDate;
    @Getter @Setter
    LocalDate endDate;
    @Getter @Setter
    BudgetCalculationType budgetCalculationType;
    @Getter @Setter
    List<ItemSpec> itemSpec = Lists.newArrayList();

    @AllArgsConstructor
    @Data
    public static class ItemSpec {
        private final Charge itemCharge;
        private final KeyTable keyTable;
        private final Charge charge;
        private final BigDecimal percentage;
        private final BigDecimal fixedBudgetedAmount;
        private final BigDecimal fixedAuditedAmount;
    }

    @Getter
    private Partitioning object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("budget", executionContext, Budget.class);

        defaultParam("startDate", executionContext, budget.getStartDate());
        defaultParam("endDate", executionContext, budget.getEndDate());
        defaultParam("budgetCalculationType", executionContext, BudgetCalculationType.BUDGETED);

        Partitioning partitioning =
                partitioningRepository.newPartitioning(budget, startDate, endDate, budgetCalculationType);

        executionContext.addResult(this, partitioning);

        for (ItemSpec spec : itemSpec) {
            final Charge itemCharge = spec.itemCharge;
            final BudgetItem budgetItem = budget.findByCharge(itemCharge);

            PartitionItem partitionItem =
                    partitionItemRepository.newPartitionItem(
                            partitioning, spec.charge, spec.keyTable, budgetItem, spec.percentage, spec.fixedBudgetedAmount, spec.fixedAuditedAmount);

            executionContext.addResult(this, partitionItem);
        }

        object = partitioning;
    }

    @Inject
    PartitioningRepository partitioningRepository;

    @Inject
    PartitionItemRepository partitionItemRepository;

}
