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

package org.estatio.module.budget.dom.partioning;

import java.math.BigDecimal;

import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitionItem_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final PartitionItem pojo = new PartitionItem();
            newPojoTester()
                    .withFixture(pojos(Partitioning.class, Partitioning.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .withFixture(pojos(PartitioningTable.class, KeyTable.class))
                    .withFixture(pojos(BudgetItem.class, BudgetItem.class))
                    .exercise(pojo);
        }

    }


    public static class OtherTests extends PartitionItem_Test {

        PartitionItem partitionItem = new PartitionItem();

        @Test
        public void validate() {

            //given
            partitionItem.setPercentage(new BigDecimal(100));

            //when then
            assertThat(partitionItem.validateUpdatePercentage(BigDecimal.valueOf(100.01))).isEqualTo("percentage should be in range 0 - 100");
            assertThat(partitionItem.validateUpdatePercentage(BigDecimal.valueOf(-0.01))).isEqualTo("percentage should be in range 0 - 100");
            assertThat(partitionItem.validateUpdatePercentage(new BigDecimal(100))).isNull();
            assertThat(partitionItem.validateUpdatePercentage(new BigDecimal(0))).isNull();
        }

        @Test
        public void precisionTest() throws Exception {

            //given
            final BigDecimal percentage = new BigDecimal("100");
            //when
            partitionItem.updatePercentage(percentage);
            //then
            assertThat(partitionItem.getPercentage()).isEqualTo(percentage.setScale(6));
            assertThat(partitionItem.getPercentage()).isEqualTo(new BigDecimal("100.000000"));

        }

        @Test
        public void percentageOf_works() throws Exception {

            assertPercentageOf(new BigDecimal("123.45"), new BigDecimal("10.000000"), new BigDecimal("12.34500000"));
            assertPercentageOf(new BigDecimal("123.45"), new BigDecimal("10.100000"), new BigDecimal("12.46845000"));
            assertPercentageOf(new BigDecimal("123.45"), new BigDecimal("10.000001"), new BigDecimal("12.3450012345"));
            assertPercentageOf(new BigDecimal("100.00"), new BigDecimal("80.000000"), new BigDecimal("80.00000000"));

        }

        void assertPercentageOf(final BigDecimal value, final BigDecimal percentage, final BigDecimal expectedResult) {
            assertThat(partitionItem.percentageOf(value, percentage)).isEqualTo(expectedResult);
        }

        @Test
        public void percentage_used_for_budgeted_value() throws Exception {
            // given
            BudgetItem budgetItem = new BudgetItem() {
                @Override
                public BigDecimal getBudgetedValue(){
                    return new BigDecimal("100.00");
                }
            };
            partitionItem.setBudgetItem(budgetItem);
            // when
            final BigDecimal percentage = new BigDecimal("80.000000");
            partitionItem.setPercentage(percentage);
            // then
            assertThat(partitionItem.getBudgetedValue()).isEqualTo(partitionItem.percentageOf(budgetItem.getBudgetedValue(), percentage));
            assertThat(partitionItem.getBudgetedValue()).isEqualTo(new BigDecimal("80.00000000"));
        }

        @Test
        public void fixed_budgeted_amount_overrides_percentage_for_budgeted_value() throws Exception {

            // given
            BudgetItem budgetItem = new BudgetItem() {
                @Override
                public BigDecimal getBudgetedValue(){
                    return new BigDecimal("100.00");
                }
            };
            partitionItem.setBudgetItem(budgetItem);

            // when
            BigDecimal fixedBudgetedAmount = new BigDecimal("123.45");
            partitionItem.setFixedBudgetedAmount(fixedBudgetedAmount);
            // then
            assertThat(partitionItem.getBudgetedValue()).isEqualTo(fixedBudgetedAmount);

        }

        @Test
        public void percentage_used_for_audited_value() throws Exception {
            // given
            BudgetItem budgetItem = new BudgetItem() {
                @Override
                public BigDecimal getAuditedValue(){
                    return new BigDecimal("100.00");
                }
            };
            partitionItem.setBudgetItem(budgetItem);
            // when
            final BigDecimal percentage = new BigDecimal("80.000000");
            partitionItem.setPercentage(percentage);
            // then
            assertThat(partitionItem.getAuditedValue()).isEqualTo(partitionItem.percentageOf(budgetItem.getAuditedValue(), percentage));
            assertThat(partitionItem.getAuditedValue()).isEqualTo(new BigDecimal("80.00000000"));
        }

        @Test
        public void fixed_audited_amount_overrides_percentage_for_audited_value() throws Exception {
            // given
            BudgetItem budgetItem = new BudgetItem() {
                @Override
                public BigDecimal getAuditedValue(){
                    return new BigDecimal("100.23");
                }
            };
            partitionItem.setBudgetItem(budgetItem);

            // when
            BigDecimal fixedAuditedAmount = new BigDecimal("123.45");
            partitionItem.setFixedAuditedAmount(fixedAuditedAmount);

            // then
            assertThat(partitionItem.getAuditedValue()).isEqualTo(fixedAuditedAmount);
        }

        @Test
        public void fixed_audited_amount_overrides_percentage_for_audited_value_even_when_budget_item_has_audited_value_null() throws Exception {
            // given
            BudgetItem budgetItem = new BudgetItem() {
                @Override
                public BigDecimal getAuditedValue(){
                    return null;
                }
            };
            partitionItem.setBudgetItem(budgetItem);

            // when
            BigDecimal fixedAuditedAmount = new BigDecimal("123.45");
            partitionItem.setFixedAuditedAmount(fixedAuditedAmount);

            // then still
            assertThat(partitionItem.getAuditedValue()).isEqualTo(fixedAuditedAmount);
        }

    }

}
