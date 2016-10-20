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

package org.estatio.dom.budgeting.allocation;

import java.math.BigDecimal;

import org.junit.Test;

import org.incode.module.base.dom.testing.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.ChargeForTesting;
import org.estatio.dom.budgeting.PropertyForTesting;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemForTesting;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableForTesting;
import org.estatio.dom.charge.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetItemAllocationTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetItemAllocation pojo = new BudgetItemAllocation();
            newPojoTester()
                    .withFixture(pojos(Property.class, PropertyForTesting.class))
                    .withFixture(pojos(Charge.class, ChargeForTesting.class))
                    .withFixture(pojos(KeyTable.class, KeyTableForTesting.class))
                    .withFixture(pojos(BudgetItem.class, BudgetItemForTesting.class))
                    .exercise(pojo);
        }

    }

    public static class UpdatePercentage extends BudgetItemAllocationTest {

        BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();

        @Test
        public void validate() {

            //given
            budgetItemAllocation.setPercentage(new BigDecimal(100));

            //when then
            assertThat(budgetItemAllocation.validateUpdatePercentage(BigDecimal.valueOf(100.01))).isEqualTo("percentage should be in range 0 - 100");
            assertThat(budgetItemAllocation.validateUpdatePercentage(BigDecimal.valueOf(-0.01))).isEqualTo("percentage should be in range 0 - 100");
            assertThat(budgetItemAllocation.validateUpdatePercentage(new BigDecimal(100))).isNull();
            assertThat(budgetItemAllocation.validateUpdatePercentage(new BigDecimal(0))).isNull();
        }

        @Test
        public void precisionTest() throws Exception {

            //given
            final BigDecimal percentage = new BigDecimal("100");
            //when
            budgetItemAllocation.updatePercentage(percentage);
            //then
            assertThat(budgetItemAllocation.getPercentage()).isEqualTo(percentage.setScale(6));
            assertThat(budgetItemAllocation.getPercentage()).isEqualTo(new BigDecimal("100.000000"));

        }

    }

}
