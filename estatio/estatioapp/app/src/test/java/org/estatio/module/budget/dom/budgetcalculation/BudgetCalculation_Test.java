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

package org.estatio.module.budget.dom.budgetcalculation;

import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

public class BudgetCalculation_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetCalculation pojo = new BudgetCalculation();
            newPojoTester()
                    .withFixture(pojos(PartitionItem.class, PartitionItem.class))
                    .withFixture(pojos(PartitioningTableItem.class, KeyItem.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .withFixture(pojos(Unit.class, Unit.class))
                    .withFixture(pojos(Budget.class, Budget.class))
                    .exercise(pojo);
        }

    }
}
