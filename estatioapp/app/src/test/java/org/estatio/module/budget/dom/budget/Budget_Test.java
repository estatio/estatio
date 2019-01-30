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

package org.estatio.module.budget.dom.budget;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;

import static org.assertj.core.api.Assertions.assertThat;

public class Budget_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final Budget pojo = new Budget();
            newPojoTester()
                    .withFixture(pojos(Property.class, Property.class))
                    .exercise(pojo);
        }

    }

    public static class TitleTest extends Budget_Test {

        @Test
        public void title() {
            // given
            final Property property = new Property();
            property.setName("Property");
            property.setReference("PROP");

            final Budget budget = new Budget();
            budget.setProperty(property);
            LocalDate startDate = new LocalDate(2015, 1, 1);
            LocalDate endDate = new LocalDate(2016, 1, 1);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);

            // when
            String budgetTitle = budget.title();

            // then
            assertThat(budgetTitle).isEqualTo(
                    property.getClass().getSimpleName()
                            + " [" + property.getReference() + "]"
                            + " > " + budget.getBudgetYear());
        }
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class StatusTest extends Budget_Test {

        @Mock BudgetCalculationRepository mockBudgetCalculationRepository;

        @Test
        public void status_new_when_no_assigned_calculations() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepository;

            // expect
            context.checking(new Expectations(){{
                oneOf(mockBudgetCalculationRepository).findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.ACTUAL, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED);
                will(returnValue(Arrays.asList()));
                oneOf(mockBudgetCalculationRepository).findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.BUDGETED, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED);
                will(returnValue(Arrays.asList()));
            }});

            // when, then
            assertThat(budget.getStatus()).isEqualTo(Status.NEW);

        }

        @Test
        public void status_assigned_when_assigned_budgeted_calculations() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepository;
            BudgetCalculation budgetedCalculation = new BudgetCalculation();

            // expect
            context.checking(new Expectations(){{
                oneOf(mockBudgetCalculationRepository).findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.ACTUAL, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED);
                will(returnValue(Arrays.asList()));
                oneOf(mockBudgetCalculationRepository).findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.BUDGETED, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED);
                will(returnValue(Arrays.asList(budgetedCalculation)));
            }});

            // when, then
            assertThat(budget.getStatus()).isEqualTo(Status.ASSIGNED);

        }

        @Test
        public void status_reconciled_when_assigned_audited_calculations() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepository;
            BudgetCalculation auditedCalculation = new BudgetCalculation();

            // expect
            context.checking(new Expectations(){{
                oneOf(mockBudgetCalculationRepository).findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.ACTUAL, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED);
                will(returnValue(Arrays.asList(auditedCalculation)));
            }});

            // when, then
            assertThat(budget.getStatus()).isEqualTo(Status.RECONCILED);

        }

    }

}
