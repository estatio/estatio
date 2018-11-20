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
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectCostTable_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    DirectCostTableRepository directCostTableRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());
            }
        });
    }

    @Test
    public void generate_items_works() throws Exception {

        // given
        final Budget budget = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        final String directTableName = "direct";
        DirectCostTable table = directCostTableRepository.findOrCreateDirectCostTable(budget, directTableName);
        assertThat(table.getItems()).isEmpty();

        // when
        wrap(table).generateItems();
        // then
        assertThat(table.getItems()).hasSize(25);
        Lists.newArrayList(table.getItems()).forEach(item->{
            assertThat(item.getBudgetedValue()).isEqualTo(BigDecimal.ZERO);
            assertThat(item.getAuditedValue()).isNull();
        });

        // and when
        final DirectCost firstItem = table.getItems().first();
        firstItem.setBudgetedValue(new BigDecimal("1000.00"));
        wrap(table).generateItems();

        // then
        assertThat(table.getItems().first()).isNotSameAs(firstItem);
        assertThat(table.getItems()).hasSize(25);
        Lists.newArrayList(table.getItems()).forEach(item->{
            assertThat(item.getBudgetedValue()).isEqualTo(BigDecimal.ZERO);
            assertThat(item.getAuditedValue()).isNull();
        });
    }

    @Test
    public void create_copy_works() throws Exception {
        // given
        final Budget budget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        final String directTableName = "direct";
        DirectCostTable table = directCostTableRepository.findOrCreateDirectCostTable(budget2015, directTableName);
        table.generateItems();
        final BigDecimal budgetedValue = new BigDecimal("1234.56");
        table.getItems().first().setBudgetedValue(budgetedValue);
        table.getItems().first().setAuditedValue(new BigDecimal("1111.11"));

        // when
        final Budget budget2016 = Budget_enum.OxfBudget2016.findUsing(serviceRegistry);
        assertThat(budget2016.getDirectCostTables()).isEmpty();

        table.createCopyFor(budget2016);
        transactionService.nextTransaction();

        // then
        assertThat(budget2016.getDirectCostTables()).hasSize(1);
        final SortedSet<DirectCost> items = budget2016.getDirectCostTables().first().getItems();
        assertThat(items).hasSize(25);
        assertThat(items.first().getBudgetedValue()).isEqualTo(budgetedValue);
        assertThat(items.first().getAuditedValue()).isNull();

    }



}