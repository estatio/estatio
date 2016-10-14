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

import java.math.BigDecimal;

import org.junit.Test;

import org.incode.module.base.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.budgeting.ChargeForTesting;
import org.estatio.dom.budgeting.CurrencyForTesting;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetForTesting;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableForTesting;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;

import static org.assertj.core.api.Assertions.assertThat;

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
                    .withFixture(pojos(KeyTable.class, KeyTableForTesting.class))
                    .exercise(pojo);
        }

    }

    public static class ChangeBudgetedvalue {

        @Test
        public void testValidateChange() {
            final BudgetItem budgetItem = new BudgetItemForTesting();
            assertThat(budgetItem.validateChangeBudgetedValue(BigDecimal.valueOf(-0.01))).isEqualTo("Value should be a positive non zero value");
            assertThat(budgetItem.validateChangeBudgetedValue(new BigDecimal(0))).isEqualTo("Value should be a positive non zero value");
            assertThat(budgetItem.validateChangeBudgetedValue(BigDecimal.valueOf(0.01))).isNull();
        }

    }

    public static class DefaultBudgetedvalue {

        @Test
        public void testValidateChange() {
            //given
            final BudgetItem budgetItem = new BudgetItemForTesting();
            final BigDecimal anyValueWillDo = new BigDecimal(10);
            //when
            budgetItem.setBudgetedValue(BigDecimal.valueOf(1000.00));
            //then
            assertThat(budgetItem.default0ChangeBudgetedValue(anyValueWillDo)).isEqualTo(BigDecimal.valueOf(1000.00));
        }

    }

    public static class ChangeAuditedvalue {

        @Test
        public void testValidateChange() {
            final BudgetItem budgetItem = new BudgetItemForTesting();
            assertThat(budgetItem.validateChangeAuditedValue(BigDecimal.valueOf(-0.01))).isEqualTo("Value can't be negative");
            assertThat(budgetItem.validateChangeAuditedValue(new BigDecimal(0))).isNull();
            assertThat(budgetItem.validateChangeAuditedValue(BigDecimal.valueOf(0.01))).isNull();
        }

    }

}
