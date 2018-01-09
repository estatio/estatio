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

import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budget.dom.budget.Budget;
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
        public void used_in_partition_items_works() throws Exception {

            // given
            Budget budget = new Budget();

            KeyTable keyTable = new KeyTable();
            keyTable.setBudget(budget);

            PartitionItem partitionItemUsingKeyTable = new PartitionItem();
            partitionItemUsingKeyTable.setKeyTable(keyTable);

            PartitionItem otherPartitionItem = new PartitionItem();

            Partitioning partitioning = new Partitioning();
            partitioning.getItems().add(partitionItemUsingKeyTable);
            partitioning.getItems().add(otherPartitionItem);
            assertThat(partitioning.getItems().size()).isEqualTo(2);

            budget.getPartitionings().add(partitioning);

            // when, then
            assertThat(keyTable.usedInPartitionItems().size()).isEqualTo(1);
            assertThat(keyTable.usedInPartitionItems()).contains(partitionItemUsingKeyTable);

        }

    }

}
