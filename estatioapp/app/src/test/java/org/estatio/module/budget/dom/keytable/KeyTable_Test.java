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

package org.estatio.module.budget.dom.keytable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.Partitioning;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTable_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final KeyTable pojo = new KeyTable();
            newPojoTester()
                    .withFixture(pojos(Budget.class, Budget.class))
                    .exercise(pojo);
        }

    }

    public static class OtherTests extends KeyTable_Test {

        @Test
        public void used_in_partition_item_works() throws Exception {

            // given
            Budget budget = new Budget();

            KeyTable keyTable = new KeyTable();
            keyTable.setBudget(budget);

            PartitionItem partitionItemUsingKeyTable = new PartitionItem();
            partitionItemUsingKeyTable.setPartitioningTable(keyTable);

            PartitionItem otherPartitionItem = new PartitionItem();

            Partitioning partitioning = new Partitioning();
            partitioning.getItems().add(partitionItemUsingKeyTable);
            partitioning.getItems().add(otherPartitionItem);
            assertThat(partitioning.getItems().size()).isEqualTo(2);

            budget.getPartitionings().add(partitioning);

            // when, then
            assertThat(keyTable.usedInPartitionItem()).isTrue();

        }

        @Test
        public void is_immutable_reason_works() throws Exception {

            // given
            KeyTable keyTable = new KeyTable();
            Budget budget = new Budget();
            keyTable.setBudget(budget);

            // when
            budget.setStatus(Status.RECONCILED);
            // then
            assertThat(keyTable.isImmutableReason()).isEqualTo("The budget is reconciled");

            // when
            budget.setStatus(Status.ASSIGNED);
            // then
            assertThat(keyTable.isImmutableReason()).isNull();

            // when
            budget.setStatus(Status.ASSIGNED);
            Partitioning partioning = new Partitioning();
            partioning.setType(BudgetCalculationType.BUDGETED);
            PartitionItem partitionItem = new PartitionItem();
            partitionItem.setPartitioningTable(keyTable);
            partioning.getItems().add(partitionItem);
            budget.getPartitionings().add(partioning);
            // then
            assertThat(keyTable.isImmutableReason()).isEqualTo("The budget is assigned");


        }


        @Test
        public void calculateInMemFor_works() throws Exception {

            // given
            Budget budget = new Budget();
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setBudget(budget);

            final BigDecimal partitionItemValue = new BigDecimal("1234.56");
            final BudgetCalculationType budgetCalculationType = BudgetCalculationType.BUDGETED;
            final KeyTable keyTable = new KeyTable();
            keyTable.setKeyValueMethod(KeyValueMethod.PROMILLE);
            keyTable.setPrecision(6);

            KeyItem keyItem1 = new KeyItem();
            keyItem1.setValue(new BigDecimal("1.00"));
            keyTable.getItems().add(keyItem1);

            KeyItem keyItem2 = new KeyItem();
            keyItem2.setValue(new BigDecimal("2.00"));
            keyTable.getItems().add(keyItem2);

            PartitionItem partitionItem = new PartitionItem();
            partitionItem.setBudgetItem(budgetItem);

            LocalDate calcStartDate = new LocalDate(2019,1,1);
            LocalDate calcEndDate = new LocalDate(2019,12,31);

            // when
            final List<InMemBudgetCalculation> calculations = keyTable.calculateInMemFor(partitionItem, partitionItemValue, budgetCalculationType, calcStartDate, calcEndDate);

            // then
            Assertions.assertThat(calculations).hasSize(2);
            BigDecimal divider = keyTable.getKeyValueMethod().divider(keyTable);

            final InMemBudgetCalculation firstCalc = calculations.get(0);
            Assertions.assertThat(firstCalc.getValue()).isEqualTo(new BigDecimal("1.234560"));
            Assertions.assertThat(firstCalc.getValue()).isEqualTo(partitionItemValue.multiply(keyItem1.getValue())
                    .divide(divider, MathContext.DECIMAL64)
                    .setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP));
            Assertions.assertThat(firstCalc.getCalculationStartDate()).isEqualTo(calcStartDate);
            Assertions.assertThat(firstCalc.getCalculationEndDate()).isEqualTo(calcEndDate);
            Assertions.assertThat(firstCalc.getCalculationType()).isEqualTo(BudgetCalculationType.BUDGETED);
            Assertions.assertThat(firstCalc.getBudget()).isEqualTo(budget);
            Assertions.assertThat(firstCalc.getPartitionItem()).isEqualTo(partitionItem);
            Assertions.assertThat(firstCalc.getTableItem()).isEqualTo(keyItem1);
            Assertions.assertThat(firstCalc.getUnit()).isNull();
            Assertions.assertThat(firstCalc.getIncomingCharge()).isNull();
            Assertions.assertThat(firstCalc.getInvoiceCharge()).isNull();

            final InMemBudgetCalculation secondCalc = calculations.get(1);
            Assertions.assertThat(secondCalc.getValue()).isEqualTo(new BigDecimal("2.469120"));
            Assertions.assertThat(secondCalc.getValue()).isEqualTo(partitionItemValue.multiply(keyItem2.getValue())
                    .divide(divider, MathContext.DECIMAL64)
                    .setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP));
            Assertions.assertThat(secondCalc.getCalculationStartDate()).isEqualTo(calcStartDate);
            Assertions.assertThat(secondCalc.getCalculationEndDate()).isEqualTo(calcEndDate);
            Assertions.assertThat(secondCalc.getCalculationType()).isEqualTo(BudgetCalculationType.BUDGETED);
            Assertions.assertThat(secondCalc.getBudget()).isEqualTo(budget);
            Assertions.assertThat(secondCalc.getPartitionItem()).isEqualTo(partitionItem);
            Assertions.assertThat(secondCalc.getTableItem()).isEqualTo(keyItem2);
            Assertions.assertThat(secondCalc.getUnit()).isNull();
            Assertions.assertThat(secondCalc.getIncomingCharge()).isNull();
            Assertions.assertThat(secondCalc.getInvoiceCharge()).isNull();
        }

    }

}
