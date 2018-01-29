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

package org.estatio.module.budget.dom.budgetitem;

import java.math.BigDecimal;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.charge.dom.Charge;

public class BudgetItem_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetItem pojo = new BudgetItem();
            newPojoTester()
                    .withFixture(pojos(Budget.class, Budget.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .withFixture(pojos(KeyTable.class, KeyTable.class))
                    .exercise(pojo);
        }

    }

    public static class OtherTests extends BudgetItem_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        BudgetCalculationRepository mockBudgetCalculationRepository;

        @Mock
        BudgetItemValueRepository mockBudgetItemValueRepository;

        @Test
        public void update_or_create_budget_item_value_works_when_unassigned() throws Exception {

            // given
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.budgetCalculationRepository = mockBudgetCalculationRepository;
            budgetItem.budgetItemValueRepository = mockBudgetItemValueRepository;
            BudgetCalculation calculation = new BudgetCalculation();
            calculation.setStatus(Status.NEW);
            final BigDecimal value = new BigDecimal("100.00");
            final LocalDate date = new LocalDate(2018, 01, 01);

            // expect
            context.checking(new Expectations(){{
                oneOf(mockBudgetCalculationRepository).findByBudgetItemAndCalculationType(budgetItem, BudgetCalculationType.BUDGETED);
                will(returnValue(Arrays.asList(calculation)));
                oneOf(mockBudgetItemValueRepository).updateOrCreateBudgetItemValue(value, budgetItem, date, BudgetCalculationType.BUDGETED);
            }});

            // when
            budgetItem.updateOrCreateBudgetItemValue(value, date, BudgetCalculationType.BUDGETED);

        }

        @Test
        public void update_or_create_budget_item_value_works_when_assigned() throws Exception {

            // given
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.budgetCalculationRepository = mockBudgetCalculationRepository;
            BudgetCalculation calculation = new BudgetCalculation();
            calculation.setStatus(Status.ASSIGNED);

            // expect
            context.checking(new Expectations(){{
                oneOf(mockBudgetCalculationRepository).findByBudgetItemAndCalculationType(budgetItem, BudgetCalculationType.BUDGETED);
                will(returnValue(Arrays.asList(calculation)));
            }});

            // when
            budgetItem.updateOrCreateBudgetItemValue(new BigDecimal("100.00"), new LocalDate(2018,01,01), BudgetCalculationType.BUDGETED);

        }

    }

}
