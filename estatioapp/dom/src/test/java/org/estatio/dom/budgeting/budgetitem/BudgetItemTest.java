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

package org.estatio.dom.budgeting.budgetitem;

import org.junit.Test;

import org.incode.module.base.dom.testing.AbstractBeanPropertiesTest;

import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;

public class BudgetItemTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetItem pojo = new BudgetItem();
            newPojoTester()
                    .withFixture(pojos(Currency.class, Currency.class))
                    .withFixture(pojos(Budget.class, Budget.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .withFixture(pojos(KeyTable.class, KeyTable.class))
                    .exercise(pojo);
        }

    }

}
