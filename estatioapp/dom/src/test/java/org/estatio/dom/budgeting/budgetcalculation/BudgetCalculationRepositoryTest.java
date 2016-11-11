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

import org.incode.module.base.dom.testing.FinderInteraction;

import org.estatio.dom.budgeting.partioning.PartitionItem;
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

    public static class FindByPartitionItemAndKeyItemAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            KeyItem keyItem = new KeyItemForTesting();
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            BudgetCalculationStatus status = BudgetCalculationStatus.TEMPORARY;
            budgetCalculationRepository.findUnique(partitionItem, keyItem, status, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findUnique");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyItem")).isEqualTo((Object) keyItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(4);
        }

    }

    public static class FindByPartitionItemAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            budgetCalculationRepository.findByPartitionItemAndCalculationType(partitionItem, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByPartitionItemAndCalculationType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class FindByPartitionItemAndStatus extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            BudgetCalculationStatus status = BudgetCalculationStatus.TEMPORARY;
            budgetCalculationRepository.findByPartitionItemAndStatus(partitionItem, status);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByPartitionItemAndStatus");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class FindByPartitionItemAndStatusAndCalculationType extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            BudgetCalculationStatus status = BudgetCalculationStatus.TEMPORARY;
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            budgetCalculationRepository.findByPartitionItemAndStatusAndCalculationType(partitionItem, status, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByPartitionItemAndStatusAndCalculationType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }

    }

    public static class FindByPartitionItem extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            KeyItem keyItem = new KeyItemForTesting();
            budgetCalculationRepository.findByPartitionItem(partitionItem);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByPartitionItem");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
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
                        final PartitionItem partitionItem,
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
            PartitionItem partitionItem = new PartitionItem();
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
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.updateOrCreateTemporaryBudgetCalculation(partitionItem, keyItem, value, null);

            //then
            assertThat(newBudgetCalculation.getPartitionItem()).isEqualTo(partitionItem);
            assertThat(newBudgetCalculation.getKeyItem()).isEqualTo(keyItem);
            assertThat(newBudgetCalculation.getValue()).isEqualTo(value);
        }
    }

    public static class UpdateOrCreateWithExisting extends BudgetCalculationRepositoryTest {

        @Mock
        private DomainObjectContainer mockContainer;

        private PartitionItem partitionItem;
        private KeyItem keyItem;
        private BigDecimal value;
        private BigDecimal sourceValue;

        @Before
        public void setup() {
            partitionItem = new PartitionItem();
            keyItem = new KeyItem();
            value = new BigDecimal("123");
            sourceValue = new BigDecimal("4567");

            budgetCalculationRepository = new BudgetCalculationRepository() {
                @Override
                public BudgetCalculation findUnique(
                        final PartitionItem partitionItem,
                        final KeyItem keyItem,
                        final BudgetCalculationStatus calculationStatus,
                        final BudgetCalculationType calculationType
                ) {
                    BudgetCalculation newCalculation = new BudgetCalculation();
                    newCalculation.setPartitionItem(partitionItem);
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
                            .findUnique(partitionItem, keyItem, null, null)
                            .getValue())
                    .isEqualTo(value);
            BigDecimal updatedValue = new BigDecimal("100");

            //when
            BudgetCalculation updatedBudgetCalculation = budgetCalculationRepository.updateOrCreateTemporaryBudgetCalculation(partitionItem, keyItem, updatedValue, null);

            //then
            assertThat(updatedBudgetCalculation.getPartitionItem()).isEqualTo(partitionItem);
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
