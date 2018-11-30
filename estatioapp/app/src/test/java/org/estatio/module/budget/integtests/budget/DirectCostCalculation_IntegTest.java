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
package org.estatio.module.budget.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.DirectCostTable_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;
import org.estatio.module.budgetassignment.contributions.Budget_Calculate;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectCostCalculation_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    DirectCostTableRepository directCostTableRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Partitioning_enum.OxfDirectPartitioning2015.builder());
            }
        });
    }

    @Test
    public void direct_cost_table_fixture_works() throws Exception {

        // given
        final Budget budget = Budget_enum.OxfDirectCostBudget2015.findUsing(serviceRegistry);
        final String directTableName = DirectCostTable_enum.Oxf2015Direct.getName();
        DirectCostTable table = directCostTableRepository.findOrCreateDirectCostTable(budget, directTableName);
        assertThat(table.getItems()).isNotEmpty();
        assertThat(table.getItems()).hasSize(25);
        assertThat(budget.getDirectCostTables()).hasSize(1);
        assertThat(budget.getPartitioningForBudgeting().getItems()).hasSize(1);
        final PartitionItem firstItem = budget.getPartitioningForBudgeting().getItems().first();
        assertThat(firstItem.getPartitioningTable()).isSameAs(table);
        assertThat(firstItem.getPercentage()).isEqualTo(new BigDecimal("100.000000"));

    }

    @Test
    @Ignore
    public void direct_cost_calculation_test() throws Exception {

        // given
        final Budget budget = Budget_enum.OxfDirectCostBudget2015.findUsing(serviceRegistry);
        // when
        wrap(mixin(Budget_Calculate.class, budget)).calculate(false);
        // then
        List<BudgetCalculation> calculations = budgetCalculationRepository.allBudgetCalculations();

    }

    @Inject BudgetCalculationRepository budgetCalculationRepository;

}