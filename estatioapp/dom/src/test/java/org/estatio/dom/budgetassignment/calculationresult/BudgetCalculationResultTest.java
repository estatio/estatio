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

package org.estatio.dom.budgetassignment.calculationresult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.incode.module.base.dom.testing.AbstractBeanPropertiesTest;

import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideForMax;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValue;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetCalculationResult pojo = new BudgetCalculationResult();
            newPojoTester()
                    .withFixture(pojos(BudgetCalculationRun.class, BudgetCalculationRun.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .exercise(pojo);
        }

    }

    public static class CalculateTest extends BudgetCalculationResultTest {

        BudgetCalculationRun run = new BudgetCalculationRun();
        BudgetCalculationResult budgetCalculationResult;
        BudgetCalculation bc1 = new BudgetCalculation();
        BudgetCalculation bc2 = new BudgetCalculation();
        Charge incomingCharge1 = new Charge();
        Charge incomingCharge2 = new Charge();
        BudgetOverrideValue bOvVal1 = new BudgetOverrideValue();
        BudgetOverrideValue bOvVal2 = new BudgetOverrideValue();
        List<BudgetCalculation> budgetCalculations = new ArrayList<>();
        List<BudgetOverrideValue> budgetOverrideValues = new ArrayList<>();
        BudgetOverride budgetOverride = new BudgetOverrideForMax(); // or any other sub class of BudgetOverride
        BigDecimal valueCalculatedByBudget = new BigDecimal("100.00");
        BigDecimal valueUsingOverrides = new BigDecimal("99.99");

        @Before
        public void setUp(){
            bc1.setValue(new BigDecimal("75.00"));
            bc1.setIncomingCharge(incomingCharge1);
            budgetCalculations.add(bc1);

            bc2.setValue(new BigDecimal("25.00"));
            bc2.setIncomingCharge(incomingCharge2);
            budgetCalculations.add(bc2);
        }

        @Test
        public void calculateOverridingAllBudgetCalculations() {

            // given
            bOvVal1.setBudgetOverride(budgetOverride);
            bOvVal1.setValue(valueUsingOverrides);
            bOvVal1.setType(BudgetCalculationType.BUDGETED);
            budgetOverrideValues.add(bOvVal1);
            budgetCalculationResult = new BudgetCalculationResult(){
                @Override
                public List<BudgetCalculation> getBudgetCalculations(){
                    return budgetCalculations;
                }
                @Override
                public List<BudgetOverrideValue> getOverrideValues(){
                    return budgetOverrideValues;
                }
            };
            run.setType(BudgetCalculationType.BUDGETED);
            budgetCalculationResult.setBudgetCalculationRun(run);

            // when
            budgetCalculationResult.calculate();

            // then
            assertThat(budgetCalculationResult.getValue()).isEqualTo(valueUsingOverrides);
            assertThat(budgetCalculationResult.getShortfall()).isEqualTo(new BigDecimal("0.01"));
            assertThat(budgetCalculationResult.getShortfall()).isEqualTo(valueCalculatedByBudget.subtract(valueUsingOverrides));

        }

        @Test
        public void calculateOverridesOneBudgetCalculation() {

            // given
            budgetOverride.setIncomingCharge(incomingCharge1);
            bOvVal1.setBudgetOverride(budgetOverride);
            bOvVal1.setValue(new BigDecimal("74.99"));
            bOvVal1.setType(BudgetCalculationType.BUDGETED);
            budgetOverrideValues.add(bOvVal1);
            budgetCalculationResult = new BudgetCalculationResult(){
                @Override
                public List<BudgetCalculation> getBudgetCalculations(){
                    return budgetCalculations;
                }
                @Override
                public List<BudgetOverrideValue> getOverrideValues(){
                    return budgetOverrideValues;
                }
            };
            run.setType(BudgetCalculationType.BUDGETED);
            budgetCalculationResult.setBudgetCalculationRun(run);


            // when
            budgetCalculationResult.calculate();

            // then
            assertThat(budgetCalculationResult.getValue()).isEqualTo(valueUsingOverrides);
            assertThat(budgetCalculationResult.getShortfall()).isEqualTo(new BigDecimal("0.01"));
            assertThat(budgetCalculationResult.getShortfall()).isEqualTo(valueCalculatedByBudget.subtract(valueUsingOverrides));

        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void CalculateFailsForIncomingChargeNullAndMoreThanOneOverride() {

            // given
            budgetOverride.setIncomingCharge(incomingCharge1);
            bOvVal1.setBudgetOverride(budgetOverride);
            budgetOverrideValues.add(bOvVal1);

            BudgetOverrideForMax budgetOverride2 = new BudgetOverrideForMax();
            budgetOverride2.setIncomingCharge(null);
            bOvVal2.setBudgetOverride(budgetOverride2);
            budgetOverrideValues.add(bOvVal2);

            budgetCalculationResult = new BudgetCalculationResult(){
                @Override
                public List<BudgetCalculation> getBudgetCalculations(){
                    return budgetCalculations;
                }
                @Override
                public List<BudgetOverrideValue> getOverrideValues(){
                    return budgetOverrideValues;
                }
            };


            // then expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            // when
            budgetCalculationResult.calculate();

        }

        @Test
        public void CalculateFailsForTwoTimesSameIncomingCharge() {

            // given
            budgetOverride.setIncomingCharge(incomingCharge1);
            bOvVal1.setBudgetOverride(budgetOverride);
            bOvVal1.setValue(BigDecimal.ZERO);
            budgetOverrideValues.add(bOvVal1);

            BudgetOverrideForMax budgetOverride2 = new BudgetOverrideForMax();
            budgetOverride2.setIncomingCharge(incomingCharge1);
            bOvVal2.setBudgetOverride(budgetOverride2);
            bOvVal2.setValue(BigDecimal.ZERO);
            budgetOverrideValues.add(bOvVal2);

            budgetCalculationResult = new BudgetCalculationResult(){
                @Override
                public List<BudgetCalculation> getBudgetCalculations(){
                    return budgetCalculations;
                }
                @Override
                public List<BudgetOverrideValue> getOverrideValues(){
                    return budgetOverrideValues;
                }
            };


            // then expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            // when
            budgetCalculationResult.calculate();

        }

    }

}
