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

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationRepository_Test {

    BudgetCalculationRepository budgetCalculationRepository;

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
                        final BudgetCalculationType calculationType
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
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.createBudgetCalculation(partitionItem, keyItem, value, null);

            //then
            assertThat(newBudgetCalculation.getPartitionItem()).isEqualTo(partitionItem);
            assertThat(newBudgetCalculation.getTableItem()).isEqualTo(keyItem);
            assertThat(newBudgetCalculation.getValue()).isEqualTo(value);
        }
    }

}
