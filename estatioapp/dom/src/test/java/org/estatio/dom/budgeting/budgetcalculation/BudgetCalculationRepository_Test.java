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

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.charge.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationRepository_Test {

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

    public static class FindByPartitionItemAndKeyItemAndCalculationType extends BudgetCalculationRepository_Test {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            KeyItem keyItem = new KeyItem();
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            budgetCalculationRepository.findUnique(partitionItem, keyItem, calculationType);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findUnique");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyItem")).isEqualTo((Object) keyItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }

    }

    public static class FindByBudgetAndStatus extends BudgetCalculationRepository_Test {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            Status status = Status.ASSIGNED;
            budgetCalculationRepository.findByBudgetAndStatus(budget, status);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetAndStatus");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget")).isEqualTo((Object) budget);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) status);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class FindByPartitionItemAndCalculationType extends BudgetCalculationRepository_Test {

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

    public static class FindByPartitionItem extends BudgetCalculationRepository_Test {

        @Test
        public void happyCase() {

            PartitionItem partitionItem = new PartitionItem();
            KeyItem keyItem = new KeyItem();
            budgetCalculationRepository.findByPartitionItem(partitionItem);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByPartitionItem");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class FindByBudgetAndInvoiceChargeAndType extends BudgetCalculationRepository_Test {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            Charge invoiceCharge = new Charge();
            Unit unit = new Unit();
            budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(budget, unit, invoiceCharge, BudgetCalculationType.BUDGETED);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetAndUnitAndInvoiceChargeAndType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget")).isEqualTo((Object) budget);
            assertThat(finderInteraction.getArgumentsByParameterName().get("unit")).isEqualTo((Object) unit);
            assertThat(finderInteraction.getArgumentsByParameterName().get("invoiceCharge")).isEqualTo((Object) invoiceCharge);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo((Object) BudgetCalculationType.BUDGETED);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(4);
        }

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class UpdateOrCreateWithoutExisting extends BudgetCalculationRepository_Test {

        @Mock
        private DomainObjectContainer mockContainer;

        @Before
        public void setup() {
            budgetCalculationRepository = new BudgetCalculationRepository() {
                @Override
                public BudgetCalculation findUnique(
                        final PartitionItem partitionItem,
                        final KeyItem keyItem,
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
            Budget budget = new Budget();
            Charge charge = new Charge();
            BudgetItem budgetItem = new BudgetItem(){
                @Override
                public Charge getCharge(){
                    return charge;
                }
            };
            PartitionItem partitionItem = new PartitionItem(){
                @Override
                public Budget getBudget(){
                    return budget;
                }

                @Override
                public BudgetItem getBudgetItem(){
                    return budgetItem;
                }
            };
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
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.createBudgetCalculation(partitionItem, keyItem, value, null);

            //then
            assertThat(newBudgetCalculation.getPartitionItem()).isEqualTo(partitionItem);
            assertThat(newBudgetCalculation.getKeyItem()).isEqualTo(keyItem);
            assertThat(newBudgetCalculation.getValue()).isEqualTo(value);
        }
    }

}
