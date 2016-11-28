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

package org.estatio.dom.budgetassignment.override;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetOverride_UnitTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void testFixed() {
            final BudgetOverrideForFixed pojo = new BudgetOverrideForFixed();
            newPojoTester()
                    .withFixture(pojos(Lease.class, Lease.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .exercise(pojo);
        }

        @Test
        public void testMax() {
            final BudgetOverrideForMax pojo = new BudgetOverrideForMax();
            newPojoTester()
                    .withFixture(pojos(Lease.class, Lease.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .exercise(pojo);
        }

        @Test
        public void testFlatRate() {
            final BudgetOverrideForFlatRate pojo = new BudgetOverrideForFlatRate();
            newPojoTester()
                    .withFixture(pojos(Lease.class, Lease.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .exercise(pojo);
        }

    }

    LocalDate budgetStartDate;
    BigDecimal valueCalculatedByBudget;
    BudgetOverrideValueRepository budgetOverrideValueRepository;
    BudgetOverrideValue budgetOverrideValue;

    @Before
    public void setup() {
        budgetOverrideValue = new BudgetOverrideValue();
        budgetOverrideValueRepository = new BudgetOverrideValueRepository(){
            @Override
            public BudgetOverrideValue findOrCreateOverrideValue(
                    final BigDecimal value,
                    final BudgetOverride budgetOverride,
                    final BudgetCalculationType type){
                return budgetOverrideValue;
            }
        };
    }

    // generic behaviour of BudgetOverride#findOrCreateValues (independent of BudgetOverride#valueFor)
    // BudgetOverrideForMax can be replaced by any other subclass of BudgetOverride
    public static class CalculateTest extends BudgetOverride_UnitTest {

        BudgetOverrideForMax override;
        BudgetOverrideValue calculation;

        @Test
        public void calculateTest() {
            // given
            valueCalculatedByBudget = new BigDecimal("1000.00");
            calculation = new BudgetOverrideValue();
            override = new BudgetOverrideForMax(){
                @Override
                BigDecimal getCalculatedValueByBudget(final LocalDate budgetStartDate, final BudgetCalculationType type){
                    return valueCalculatedByBudget;
                }

                @Override BudgetOverrideValue valueFor(final LocalDate date, final BudgetCalculationType type) {
                    return calculation;
                }

            };
            override.budgetOverrideValueRepository = budgetOverrideValueRepository;
            budgetStartDate = new LocalDate(2015, 01, 01);

            // when no dates and no type set
            List<BudgetOverrideValue> calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(2);

            // and when startdate set on budgetStartDate
            override.setStartDate(budgetStartDate);
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(2);

            // and when enddate set on budgetStartDate
            override.setEndDate(budgetStartDate);
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(2);

            // and when startdate after budgetStartDate
            override.setStartDate(budgetStartDate.plusDays(1));
            override.setEndDate(null);
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(0);

            // and when enddate before budgetStartDate
            override.setStartDate(null);
            override.setEndDate(budgetStartDate.minusDays(1));
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(0);

            // and when type set
            override.setStartDate(null);
            override.setEndDate(null);
            override.setType(BudgetCalculationType.BUDGETED);
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(1);

            // and when
            override.setType(BudgetCalculationType.ACTUAL);
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(1);

        }

    }

    public static class CalculateForMaxSpecific extends BudgetOverride_UnitTest {

        BudgetOverrideForMax override;
        BigDecimal maxValue;

        @Test
        public void calculateTest() {
            // given
            valueCalculatedByBudget = new BigDecimal("1000.00");
            override = new BudgetOverrideForMax(){
                @Override
                BigDecimal getCalculatedValueByBudget(final LocalDate budgetStartDate, final BudgetCalculationType type){
                    return valueCalculatedByBudget;
                }
            };
            override.budgetOverrideValueRepository = budgetOverrideValueRepository;
            budgetStartDate = new LocalDate(2015, 01, 01);

            // when
            maxValue = new BigDecimal("1000.00");
            override.setMaxValue(maxValue);
            List<BudgetOverrideValue> calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(0);

            // and when
            maxValue = new BigDecimal("999.99");
            override.setMaxValue(maxValue);
            calculations = override.findOrCreateValues(budgetStartDate);

            // then
            assertThat(calculations.size()).isEqualTo(2);

        }

    }

    public static class TerminateOverride extends BudgetOverride_UnitTest {

        @Test
        public void test() {

            // given
            BudgetOverride budgetOverride = new BudgetOverrideForTesting();
            LocalDate terminationDate = new LocalDate();

            // when
            budgetOverride.terminate(terminationDate);

            // then
            assertThat(budgetOverride.getEndDate()).isEqualTo(terminationDate);

        }

        @Test
        public void validateTest() {

            // given
            BudgetOverride budgetOverride = new BudgetOverrideForTesting();
            LocalDate startDate = new LocalDate();
            budgetOverride.setStartDate(startDate);

            // when
            LocalDate terminationDate = startDate.minusDays(1);
            String invalidReason = budgetOverride.validateTerminate(terminationDate);

            // then
            assertThat(invalidReason).isEqualTo("The date of termination cannot be before the start date");

        }

    }

}
