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
import org.estatio.module.budget.dom.budgetcalculation.Status;

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

    @Mock BudgetCalculationRepository mockBudgetCalculationRepo;

    public static class OtherTests extends Budget_Test {

        @Test
        public void isAssignedForBudgeted_when_assigned_calculation_found_works() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepo;

            BudgetCalculation calculation = new BudgetCalculation();
            calculation.setCalculationType(BudgetCalculationType.BUDGETED);
            calculation.setStatus(Status.ASSIGNED);

            // expect
            context.checking(new Expectations(){{
                allowing(mockBudgetCalculationRepo).findByBudget(budget);
                will(returnValue(Arrays.asList(calculation)));
            }});

            // when // then
            assertThat(budget.isAssignedForType(BudgetCalculationType.BUDGETED)).isTrue();
            assertThat(budget.isAssigned()).isTrue();

        }

        @Test
        public void isAssignedForBudgeted_when_no_assigned_calculation_found_works() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepo;

            BudgetCalculation calculation = new BudgetCalculation();
            calculation.setCalculationType(BudgetCalculationType.BUDGETED);
            calculation.setStatus(Status.NEW);

            // expect
            context.checking(new Expectations(){{
                allowing(mockBudgetCalculationRepo).findByBudget(budget);
                will(returnValue(Arrays.asList(calculation)));
            }});

            // when // then
            assertThat(budget.isAssignedForType(BudgetCalculationType.BUDGETED)).isFalse();
            assertThat(budget.isAssigned()).isFalse();

        }

        @Test
        public void isAssignedForActual_works() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepo;

            BudgetCalculation calculation = new BudgetCalculation();
            calculation.setCalculationType(BudgetCalculationType.ACTUAL);
            calculation.setStatus(Status.ASSIGNED);

            // expect
            context.checking(new Expectations(){{
                allowing(mockBudgetCalculationRepo).findByBudget(budget);
                will(returnValue(Arrays.asList(calculation)));
            }});

            // when // then
            assertThat(budget.isAssignedForType(BudgetCalculationType.ACTUAL)).isTrue();
            assertThat(budget.isAssigned()).isTrue();

        }

        @Test
        public void isAssignedForActual_when_no_assigned_calculation_found_works() throws Exception {

            // given
            Budget budget = new Budget();
            budget.budgetCalculationRepository = mockBudgetCalculationRepo;

            BudgetCalculation calculation = new BudgetCalculation();
            calculation.setCalculationType(BudgetCalculationType.ACTUAL);
            calculation.setStatus(Status.NEW);

            // expect
            context.checking(new Expectations(){{
                allowing(mockBudgetCalculationRepo).findByBudget(budget);
                will(returnValue(Arrays.asList(calculation)));
            }});

            // when // then
            assertThat(budget.isAssignedForType(BudgetCalculationType.ACTUAL)).isFalse();
            assertThat(budget.isAssigned()).isFalse();

        }


    }

}
