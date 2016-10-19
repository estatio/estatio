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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.FinderInteraction;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItemForTesting;

import static org.assertj.core.api.Assertions.assertThat;

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
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            BudgetCalculationStatus status = BudgetCalculationStatus.TEMPORARY;
            budgetCalculationRepository.findUnique(budgetItemAllocation, keyItem, status, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findUnique");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation")).isEqualTo((Object) budgetItemAllocation);
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyItem")).isEqualTo((Object) keyItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(4);
        }

    }

    public static class FindByBudgetItemAllocationAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            budgetCalculationRepository.findByBudgetItemAllocationAndCalculationType(budgetItemAllocation, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetItemAllocationAndCalculationType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation")).isEqualTo((Object) budgetItemAllocation);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class FindByBudgetItemAllocationAndStatus extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            BudgetCalculationStatus status = BudgetCalculationStatus.TEMPORARY;
            budgetCalculationRepository.findByBudgetItemAllocationAndStatus(budgetItemAllocation, status);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetItemAllocationAndStatus");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation")).isEqualTo((Object) budgetItemAllocation);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class FindByBudgetItemAllocationAndStatusAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            BudgetCalculationStatus status = BudgetCalculationStatus.TEMPORARY;
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            budgetCalculationRepository.findByBudgetItemAllocationAndStatusAndCalculationType(budgetItemAllocation, status, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetItemAllocationAndStatusAndCalculationType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation")).isEqualTo((Object) budgetItemAllocation);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }

    }

    public static class FindByBudgetItemAllocation extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();
            KeyItem keyItem = new KeyItemForTesting();
            budgetCalculationRepository.findByBudgetItemAllocation(budgetItemAllocation);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetItemAllocation");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItemAllocation")).isEqualTo((Object) budgetItemAllocation);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
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
                public BudgetCalculation findUnique(
                        final BudgetItemAllocation budgetItemAllocation,
                        final KeyItem keyItem,
                        final BudgetCalculationStatus calculationStatus,
                        final BudgetCalculationType calculationType
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
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.updateOrCreateTemporaryBudgetCalculation(budgetItemAllocation, keyItem, value, null);

            //then
            assertThat(newBudgetCalculation.getBudgetItemAllocation()).isEqualTo(budgetItemAllocation);
            assertThat(newBudgetCalculation.getKeyItem()).isEqualTo(keyItem);
            assertThat(newBudgetCalculation.getValue()).isEqualTo(value);
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
                public BudgetCalculation findUnique(
                        final BudgetItemAllocation budgetItemAllocation,
                        final KeyItem keyItem,
                        final BudgetCalculationStatus calculationStatus,
                        final BudgetCalculationType calculationType
                ) {
                    BudgetCalculation newCalculation = new BudgetCalculation();
                    newCalculation.setBudgetItemAllocation(budgetItemAllocation);
                    newCalculation.setKeyItem(keyItem);
                    newCalculation.setValue(value);
                    newCalculation.setCalculationType(calculationType);
                    return newCalculation;
                }
            };
            budgetCalculationRepository.setContainer(mockContainer);
        }

        @Test
        public void updateOrCreateBudgetCalculation() {

            //given
            assertThat(
                    budgetCalculationRepository
                            .findUnique(budgetItemAllocation, keyItem, null, null)
                            .getValue())
                    .isEqualTo(value);
            BigDecimal updatedValue = new BigDecimal("100");

            //when
            BudgetCalculation updatedBudgetCalculation = budgetCalculationRepository.updateOrCreateTemporaryBudgetCalculation(budgetItemAllocation, keyItem, updatedValue, null);

            //then
            assertThat(updatedBudgetCalculation.getBudgetItemAllocation()).isEqualTo(budgetItemAllocation);
            assertThat(updatedBudgetCalculation.getKeyItem()).isEqualTo(keyItem);
            assertThat(updatedBudgetCalculation.getValue()).isEqualTo(updatedValue);
        }

    }

    public static class ResetAndUpdateOrCreateBudgetCalculations extends BudgetCalculationRepositoryTest {

        @Test
        public void illegalArgumentTest() {

        }

    }

}
