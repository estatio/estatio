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

package org.estatio.module.budgeting.dom.partioning;

import java.math.BigDecimal;

import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
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
                    .withFixture(pojos(KeyTable.class, KeyTable.class))
                    .withFixture(pojos(BudgetItem.class, BudgetItem.class))
                    .exercise(pojo);
        }

    }


    public static class UpdatePercentage extends PartitionItem_Test {

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

    }

}
