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

package org.estatio.dom.budgeting.budgetcalculation;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItemForTesting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BudgetCalculationRepositoryTest {

    FinderInteraction finderInteraction;

    BudgetCalculationRepository budgetCalculationRepository;

    @Before
    public void setup() {
        budgetCalculationRepository = new BudgetCalculationRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.UNIQUE_MATCH);
                return null;
            }

            @Override
            protected List<BudgetCalculation> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderInteraction.FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByBudgetItemAllocationAndKeyItemAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            KeyItem keyItem = new KeyItemForTesting();
            CalculationType calculationType = CalculationType.BUDGETED;
            budgetCalculationRepository.findByBudgetItemAllocationAndKeyItemAndCalculationType(budgetItemAllocation, keyItem, calculationType);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetCalculation.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetItemAllocationAndKeyItemAndCalculationType"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation"), is((Object) budgetItemAllocation));
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyItem"), is((Object) keyItem));
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType"), is((Object) calculationType));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }

    public static class FindByBudgetItemAllocationAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            CalculationType calculationType = CalculationType.BUDGETED;
            budgetCalculationRepository.findByBudgetItemAllocationAndCalculationType(budgetItemAllocation, calculationType);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetCalculation.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetItemAllocationAndCalculationType"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation"), is((Object) budgetItemAllocation));
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType"), is((Object) calculationType));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }

    public static class FindByBudgetItemAllocation extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            KeyItem keyItem = new KeyItemForTesting();
            budgetCalculationRepository.findByBudgetItemAllocation(budgetItemAllocation);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetCalculation.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetItemAllocation"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation"), is((Object) budgetItemAllocation));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class UpdateOrCreateWithoutExisting extends BudgetCalculationRepositoryTest {

        @Mock
        private DomainObjectContainer mockContainer;

        @Before
        public void setup() {
            budgetCalculationRepository = new BudgetCalculationRepository() {
                @Override
                public BudgetCalculation findByBudgetItemAllocationAndKeyItemAndCalculationType(
                        final BudgetItemAllocation budgetItemAllocation,
                        final KeyItem keyItem,
                        final CalculationType calculationType
                ) {
                    return null;
                }
            };
            budgetCalculationRepository.setContainer(mockContainer);
        }

        @Test
        public void updateOrCreateBudgetCalculation() {

            //given
            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            KeyItem keyItem = new KeyItem();
            BigDecimal value = new BigDecimal("100");
            BigDecimal sourceValue = new BigDecimal("1000");
            final BudgetCalculation budgetCalculation = new BudgetCalculation();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(BudgetCalculation.class);
                    will(returnValue(budgetCalculation));
                    oneOf(mockContainer).persist(budgetCalculation);
                }
            });

            //when
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.updateOrCreateBudgetCalculation(budgetItemAllocation, keyItem, value, sourceValue, null);

            //then
            Assertions.assertThat(newBudgetCalculation.getBudgetItemAllocation()).isEqualTo(budgetItemAllocation);
            Assertions.assertThat(newBudgetCalculation.getKeyItem()).isEqualTo(keyItem);
            Assertions.assertThat(newBudgetCalculation.getValue()).isEqualTo(value);
            Assertions.assertThat(newBudgetCalculation.getSourceValue()).isEqualTo(sourceValue);
        }
    }

    public static class UpdateOrCreateWithExisting extends BudgetCalculationRepositoryTest {

        @Mock
        private DomainObjectContainer mockContainer;

        private BudgetItemAllocation budgetItemAllocation;
        private KeyItem keyItem;
        private BigDecimal value;
        private BigDecimal sourceValue;

        @Before
        public void setup() {
            budgetItemAllocation = new BudgetItemAllocation();
            keyItem = new KeyItem();
            value = new BigDecimal("123");
            sourceValue = new BigDecimal("4567");

            budgetCalculationRepository = new BudgetCalculationRepository() {
                @Override
                public BudgetCalculation findByBudgetItemAllocationAndKeyItemAndCalculationType(
                        final BudgetItemAllocation budgetItemAllocation,
                        final KeyItem keyItem,
                        final CalculationType calculationType
                ) {
                    BudgetCalculation newCalculation = new BudgetCalculation();
                    newCalculation.setBudgetItemAllocation(budgetItemAllocation);
                    newCalculation.setKeyItem(keyItem);
                    newCalculation.setValue(value);
                    newCalculation.setSourceValue(sourceValue);
                    newCalculation.setCalculationType(calculationType);
                    return newCalculation;
                }
            };
            budgetCalculationRepository.setContainer(mockContainer);
        }

        @Test
        public void updateOrCreateBudgetCalculation() {

            //given
            Assertions.assertThat(
                    budgetCalculationRepository
                            .findByBudgetItemAllocationAndKeyItemAndCalculationType(budgetItemAllocation, keyItem, null)
                            .getValue())
                    .isEqualTo(value);
            Assertions.assertThat(
                    budgetCalculationRepository
                            .findByBudgetItemAllocationAndKeyItemAndCalculationType(budgetItemAllocation, keyItem, null)
                            .getSourceValue())
                    .isEqualTo(sourceValue);
            BigDecimal updatedValue = new BigDecimal("100");
            BigDecimal updatedSourceValue = new BigDecimal("1000");

            //when
            BudgetCalculation updatedBudgetCalculation = budgetCalculationRepository.updateOrCreateBudgetCalculation(budgetItemAllocation, keyItem, updatedValue, updatedSourceValue, null);

            //then
            Assertions.assertThat(updatedBudgetCalculation.getBudgetItemAllocation()).isEqualTo(budgetItemAllocation);
            Assertions.assertThat(updatedBudgetCalculation.getKeyItem()).isEqualTo(keyItem);
            Assertions.assertThat(updatedBudgetCalculation.getValue()).isEqualTo(updatedValue);
            Assertions.assertThat(updatedBudgetCalculation.getSourceValue()).isEqualTo(updatedSourceValue);
        }

    }

    public static class ResetAndUpdateOrCreateBudgetCalculations extends BudgetCalculationRepositoryTest {

        @Test
        public void illegalArgumentTest() {

        }

    }

}
