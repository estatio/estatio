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

import java.util.List;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.poi.ss.formula.functions.T;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetItemRepository_Test {

    public static class FindOrCreateBudgetItemCreatingNewItem extends BudgetItemRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private RepositoryService mockRepositoryService;

        BudgetItemRepository budgetItemRepository1;

        @Before
        public void setup() {
            budgetItemRepository1 = new BudgetItemRepository() {
                @Override
                public BudgetItem findByBudgetAndCharge(final Budget budget, final Charge budgetItemCharge) {
                    return null;
                }
            };
            budgetItemRepository1.repositoryService = mockRepositoryService;
        }

        @Test
        public void findOrCreateNewItem() throws Exception {

            final Budget budget = new Budget();
            final Charge charge = new Charge();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).persistAndFlush(with(any(BudgetItem.class)));
                    will(returnValue(new BudgetItem(budget, charge)));
                }
            });

            // when
            BudgetItem newBudgetItem = budgetItemRepository1.findOrCreateBudgetItem(budget, charge);

            // then
            assertThat(newBudgetItem.getBudget()).isEqualTo(budget);
            assertThat(newBudgetItem.getCharge()).isEqualTo(charge);

        }

    }

}
