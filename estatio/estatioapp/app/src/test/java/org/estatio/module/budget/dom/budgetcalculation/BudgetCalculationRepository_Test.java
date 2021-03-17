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

package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

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
            LocalDate calculationStartDate = new LocalDate();
            LocalDate calculationEndDate = new LocalDate();
            budgetCalculationRepository.findUnique(partitionItem, keyItem, calculationType, calculationStartDate, calculationEndDate);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetCalculation.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findUnique");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitionItem")).isEqualTo((Object) partitionItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("tableItem")).isEqualTo((Object) keyItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationType")).isEqualTo((Object) calculationType);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationStartDate")).isEqualTo((Object) calculationStartDate);
            assertThat(finderInteraction.getArgumentsByParameterName().get("calculationEndDate")).isEqualTo((Object) calculationEndDate);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(5);
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
        private FactoryService mockFactoryService;

        @Mock
        private RepositoryService mockRepositoryService;

        @Before
        public void setup() {
            budgetCalculationRepository = new BudgetCalculationRepository() {
                @Override
                public BudgetCalculation findUnique(
                        final PartitionItem partitionItem,
                        final PartitioningTableItem keyItem,
                        final BudgetCalculationType calculationType,
                        final LocalDate calculationStartDate,
                        final LocalDate calculationEndDate
                ) {
                    return null;
                }
            };
            budgetCalculationRepository.factoryService = mockFactoryService;
            budgetCalculationRepository.repositoryService = mockRepositoryService;
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
                    oneOf(mockFactoryService).instantiate(BudgetCalculation.class);
                    will(returnValue(budgetCalculation));
                    oneOf(mockRepositoryService).persist(budgetCalculation);
                }
            });

            //when
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.createBudgetCalculation(partitionItem, keyItem, value, null, null,null );

            //then
            assertThat(newBudgetCalculation.getPartitionItem()).isEqualTo(partitionItem);
            assertThat(newBudgetCalculation.getTableItem()).isEqualTo(keyItem);
            assertThat(newBudgetCalculation.getValue()).isEqualTo(value);
        }
    }

    public static class OtherTests extends BudgetCalculationRepository_Test {

        @Test
        public void createInMemBudgetCalculation_works() throws Exception {

            // given
            Charge invoiceCharge = new Charge();
            Charge incomingCharge = new Charge();

            Budget budget = new Budget();
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setBudget(budget);
            budgetItem.setCharge(incomingCharge);

            PartitionItem partitionItem = new PartitionItem();
            partitionItem.setBudgetItem(budgetItem);
            partitionItem.setCharge(invoiceCharge);

            Unit unit = new Unit();
            KeyItem tableItem = new KeyItem();
            tableItem.setUnit(unit);

            BigDecimal value = new BigDecimal("1234.5678");
            BudgetCalculationType calculationType = BudgetCalculationType.BUDGETED;
            LocalDate startDate = new LocalDate(2020,1,1);
            LocalDate endDate = new LocalDate(2020,10,15);

            // when
            final InMemBudgetCalculation inMemCalc = BudgetCalculationRepository
                    .createInMemBudgetCalculation(partitionItem, tableItem, value, calculationType, startDate, endDate);
            // then
            Assertions.assertThat(inMemCalc.getValue()).isEqualTo(value);
            Assertions.assertThat(inMemCalc.getCalculationStartDate()).isEqualTo(startDate);
            Assertions.assertThat(inMemCalc.getCalculationEndDate()).isEqualTo(endDate);
            Assertions.assertThat(inMemCalc.getPartitionItem()).isEqualTo(partitionItem);
            Assertions.assertThat(inMemCalc.getTableItem()).isEqualTo(tableItem);
            Assertions.assertThat(inMemCalc.getCalculationType()).isEqualTo(calculationType);
            Assertions.assertThat(inMemCalc.getBudget()).isEqualTo(budget);
            Assertions.assertThat(inMemCalc.getUnit()).isEqualTo(unit);
            Assertions.assertThat(inMemCalc.getInvoiceCharge()).isEqualTo(invoiceCharge);
            Assertions.assertThat(inMemCalc.getIncomingCharge()).isEqualTo(incomingCharge);

        }

    }

}
