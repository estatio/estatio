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

package org.estatio.dom.budgeting.budgetcalculation;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationServiceTest {

    public static class Calculate extends BudgetCalculationServiceTest  {

        Budget budget;
        BudgetItem budgetItem;
        BudgetItemAllocation allocation;
        KeyTable keyTable;
        KeyItem keyItem1;
        KeyItem keyItem2;

        BudgetCalculationService service = new BudgetCalculationService();

        @Before
        public void setup() {

            budget = new Budget();
            budgetItem = new BudgetItem();

            keyTable = new KeyTable();
            keyTable.setKeyValueMethod(KeyValueMethod.PERCENT);
            keyTable.setPrecision(6);

            keyItem1 = new KeyItem();
            keyItem1.setValue(new BigDecimal("1.00"));
            keyItem1.setKeyTable(keyTable);
            keyTable.getItems().add(keyItem1);

            keyItem2 = new KeyItem();
            keyItem2.setValue(new BigDecimal("2.00"));
            keyItem2.setKeyTable(keyTable);
            keyTable.getItems().add(keyItem2);


            allocation = new BudgetItemAllocation();
            allocation.setBudgetItem(budgetItem);
            allocation.setKeyTable(keyTable);

            budgetItem.getBudgetItemAllocations().add(allocation);
            budget.getItems().add(budgetItem);

        }

        @Test
        public void calculate100Percent() {

            // given
            budgetItem.setBudgetedValue(new BigDecimal("1000.00"));
            allocation.setPercentage(new BigDecimal("100.00"));

            // when
            List<BudgetCalculationViewmodel> results = service.getCalculations(budget);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).getValue()).isEqualTo(new BigDecimal("333.333333"));
            assertThat(results.get(1).getValue()).isEqualTo(new BigDecimal("666.666667"));

        }

        @Test
        public void calculate99Percent() {

            // given
            budgetItem.setBudgetedValue(new BigDecimal("1000.00"));
            allocation.setPercentage(new BigDecimal("99.00"));

            // when
            List<BudgetCalculationViewmodel> results = service.getCalculations(budget);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).getValue()).isEqualTo(new BigDecimal("330.000000"));
            assertThat(results.get(1).getValue()).isEqualTo(new BigDecimal("660.000000"));

        }

        @Test
        public void budgetedValueIsZero() {

            // given
            budgetItem.setBudgetedValue(BigDecimal.ZERO);
            allocation.setPercentage(new BigDecimal("99.00"));

            // when
            List<BudgetCalculationViewmodel> results = service.getCalculations(budget);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).getValue()).isEqualTo(BigDecimal.ZERO);
            assertThat(results.get(1).getValue()).isEqualTo(BigDecimal.ZERO);

        }

        @Test
        public void PercentageIsZero() {

            // given
            budgetItem.setBudgetedValue(new BigDecimal("1000.00"));
            allocation.setPercentage(BigDecimal.ZERO);

            // when
            List<BudgetCalculationViewmodel> results = service.getCalculations(budget);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).getValue()).isEqualTo(BigDecimal.ZERO);
            assertThat(results.get(1).getValue()).isEqualTo(BigDecimal.ZERO);

        }

        @Test
        public void keySumKeyTableIsZero() {

            // given
            budgetItem.setBudgetedValue(new BigDecimal("1000.00"));
            allocation.setPercentage(new BigDecimal("99.00"));
            keyItem1.setValue(BigDecimal.ZERO);
            keyItem2.setValue(BigDecimal.ZERO);

            // when
            List<BudgetCalculationViewmodel> results = service.getCalculations(budget);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).getValue()).isEqualTo(BigDecimal.ZERO);
            assertThat(results.get(1).getValue()).isEqualTo(BigDecimal.ZERO);

        }

    }

}
