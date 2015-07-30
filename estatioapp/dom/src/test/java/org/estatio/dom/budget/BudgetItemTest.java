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

package org.estatio.dom.budget;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;

/**
 * Created by jodo on 22/04/15.
 */
public class BudgetItemTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetItem pojo = new BudgetItem();
            newPojoTester()
                    .withFixture(pojos(Currency.class, CurrencyForTesting.class))
                    .withFixture(pojos(Budget.class, BudgetForTesting.class))
                    .withFixture(pojos(Charge.class, ChargeForTesting.class))
                    .withFixture(pojos(BudgetKeyTable.class, BudgetKeyTableForTesting.class))
                    .exercise(pojo);
        }

    }

}
