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

    FinderInteraction finderInteraction;

    BudgetItemRepository budgetItemRepository;

    @Before
    public void setup() {
        budgetItemRepository = new BudgetItemRepository() {

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
            protected List<BudgetItem> allInstances() {
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

    public static class FindByBudgetAndCharge extends BudgetItemRepository_Test {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            Charge charge = new Charge();
            budgetItemRepository.findByBudgetAndCharge(budget, charge);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(BudgetItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetAndCharge");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget")).isEqualTo((Object) budget);
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge")).isEqualTo((Object) charge);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

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
