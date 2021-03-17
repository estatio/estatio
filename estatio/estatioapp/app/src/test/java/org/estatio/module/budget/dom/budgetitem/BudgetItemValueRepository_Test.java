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

package org.estatio.module.budget.dom.budgetitem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetItemValueRepository_Test {

    public static class NewBudgetItemValue extends BudgetItemValueRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private RepositoryService mockRepositoryService;

        BudgetItemValueRepository budgetItemValueRepository;

        @Before
        public void setup() {
            budgetItemValueRepository = new BudgetItemValueRepository();
            budgetItemValueRepository.repositoryService = mockRepositoryService;
        }

        @Test
        public void newBudgetItemValue() throws Exception {

            final BudgetItem budgetItem = new BudgetItem();
            final BigDecimal value = new BigDecimal("10000.00");
            final LocalDate date = new LocalDate(2016,01,01);
            final BudgetCalculationType type = BudgetCalculationType.BUDGETED;

            final BudgetItemValue budgetItemValue = new BudgetItemValue(budgetItem, date, type, value);

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).persistAndFlush(with(any(BudgetItemValue.class)));
                    will(returnValue(budgetItemValue));
                }

            });

            // when
            BudgetItemValue newBudgetItemValue = budgetItemValueRepository.createBudgetItemValue(budgetItem, value, date, type);

            // then
            assertThat(newBudgetItemValue.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(newBudgetItemValue.getValue()).isEqualTo(value);
            assertThat(newBudgetItemValue.getDate()).isEqualTo(date);
            assertThat(newBudgetItemValue.getType()).isEqualTo(type);

        }

        @Test
        public void validateNewBudgetItemValue() throws Exception {

            // given
            final BudgetItemValue budgetItemValue = new BudgetItemValue();
            final BudgetItem budgetItem = new BudgetItem();
            final BigDecimal value = null;
            final LocalDate date = new LocalDate(2016,01,01);
            final BudgetCalculationType type = BudgetCalculationType.BUDGETED;

            // when

            budgetItemValueRepository = new BudgetItemValueRepository() {
                @Override
                public List<BudgetItemValue> findByBudgetItemAndType(final BudgetItem budgetItem, final BudgetCalculationType type) {
                    return Arrays.asList();
                }
            };

            // then
            assertThat(budgetItemValueRepository.validateCreateBudgetItemValue(budgetItem, null, date, type)).isEqualTo("Value cannot be empty");

            // and when
            budgetItemValueRepository = new BudgetItemValueRepository() {
                @Override
                public List<BudgetItemValue> findByBudgetItemAndType(final BudgetItem budgetItem, final BudgetCalculationType type) {
                    return Arrays.asList(budgetItemValue);
                }
            };

            // then
            assertThat(budgetItemValueRepository.validateCreateBudgetItemValue(budgetItem, null, date, type)).isEqualTo("Only one value of type BUDGETED is allowed");
        }
    }

}
