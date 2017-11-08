package org.estatio.app.menus.budget;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.asset.Property;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budget.BudgetRepository;

import static org.assertj.core.api.Assertions.assertThat;

/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
public class BudgetMenu_Test {

    public static class Choices1FindBudget extends BudgetMenu_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        @Mock
        BudgetRepository mockBudgetRepository;
        BudgetMenu budgetMenu;

        @Before
        public void setUp() throws Exception {
            budgetMenu = new BudgetMenu();
            budgetMenu.budgetRepository = mockBudgetRepository;
        }

        @Test
        public void happyCase() throws Exception {
            final Property property = new Property();
            property.setName("Property");

            final Budget budget = new Budget();
            budget.setProperty(property);

            final List<Budget> budgetList = new ArrayList<>();
            budgetList.add(budget);

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockBudgetRepository).findByProperty(property);
                    will(returnValue(budgetList));
                }
            });

            // when
            final List<Budget> budgets = budgetMenu.choices1FindBudget(property, null);
            assertThat(budgets).hasSize(1);
            assertThat(budgets.get(0)).isEqualTo(budget);
        }

    }
}