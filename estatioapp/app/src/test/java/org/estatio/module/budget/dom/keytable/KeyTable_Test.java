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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
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

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock BudgetCalculationRepository mockBudgetCalculationRepository;

        @Test
        public void calculateFor_works() throws Exception {

            // given
            final BigDecimal partitionItemValue = new BigDecimal("1234.56");
            final BudgetCalculationType budgetCalculationType = BudgetCalculationType.BUDGETED;
            final KeyTable keyTable = new KeyTable();
            keyTable.budgetCalculationRepository = mockBudgetCalculationRepository;
            keyTable.setKeyValueMethod(KeyValueMethod.PROMILLE);
            keyTable.setPrecision(6);

            KeyItem keyItem1 = new KeyItem();
            keyItem1.setValue(new BigDecimal("1.00"));
            keyTable.getItems().add(keyItem1);

            KeyItem keyItem2 = new KeyItem();
            keyItem2.setValue(new BigDecimal("2.00"));
            keyTable.getItems().add(keyItem2);

            PartitionItem partitionItem = new PartitionItem();

            LocalDate calcStartDate = new LocalDate(2019,1,1);
            LocalDate calcEndDate = new LocalDate(2019,12,31);

            // expect
            context.checking(new Expectations(){{
                oneOf(mockBudgetCalculationRepository).findOrCreateBudgetCalculation(
                        partitionItem
                        , keyItem1
                        , new BigDecimal("1.234560")
                        , BudgetCalculationType.BUDGETED
                        , calcStartDate
                        , calcEndDate);
                oneOf(mockBudgetCalculationRepository).findOrCreateBudgetCalculation(
                        partitionItem
                        , keyItem2
                        , new BigDecimal("2.469120")
                        , BudgetCalculationType.BUDGETED
                        , calcStartDate
                        , calcEndDate);
            }});

            // when
            keyTable.calculateFor(partitionItem, partitionItemValue, budgetCalculationType, calcStartDate, calcEndDate);

        }

    }

}
