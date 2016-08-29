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

package org.estatio.dom.budgeting.budgetitem;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.ChargeForTesting;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetForTesting;
import org.estatio.dom.charge.Charge;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class BudgetItemRepositoryTest {

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

    public static class FindByBudget extends BudgetItemRepositoryTest {

        @Test
        public void happyCase() {

            Budget budget = new BudgetForTesting();
            budgetItemRepository.findByBudget(budget);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudget"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByBudgetAndCharge extends BudgetItemRepositoryTest {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            Charge charge = new Charge();
            budgetItemRepository.findByBudgetAndCharge(budget, charge);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetAndCharge"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }

    public static class FindByPropertyAndChargeAndStartDate extends BudgetItemRepositoryTest {

        @Test
        public void happyCase() {

            Property property = new Property();
            Charge charge = new Charge();
            LocalDate startDate = new LocalDate();
            budgetItemRepository.findByPropertyAndChargeAndStartDate(property, charge, startDate);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByPropertyAndChargeAndStartDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object) startDate));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }

    public static class ValidateNewBudgetItem extends BudgetItemRepositoryTest {

        @Test
        public void testValidateNewBudgetItem() {
            //given
            Budget budget = new BudgetForTesting();
            Charge charge = new ChargeForTesting();

            //when
            BigDecimal negativeValue = BigDecimal.valueOf(-0.01);
            BigDecimal zeroValue = BigDecimal.ZERO;
            BigDecimal positiveValue = BigDecimal.valueOf(0.01);
            //then
            assertThat(budgetItemRepository.validateNewBudgetItem(budget, negativeValue, charge), is("Value can't be negative"));
            assertThat(budgetItemRepository.validateNewBudgetItem(budget, zeroValue, charge), is(nullValue()));
            assertThat(budgetItemRepository.validateNewBudgetItem(budget, positiveValue, charge), is(nullValue()));

        }

    }

    public static class FindOrCreateBudgetItemCreatingNewItem extends BudgetItemRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        BudgetItemRepository budgetItemRepository1;

        @Before
        public void setup() {
            budgetItemRepository1 = new BudgetItemRepository() {
                @Override
                public BudgetItem findByBudgetAndCharge(final Budget budget, final Charge budgetItemCharge) {
                    return null;
                }
            };
            budgetItemRepository1.setContainer(mockContainer);
        }

        @Test
        public void findOrCreateXxxCreatingNewItem() throws Exception {

            final Budget budget = new Budget();
            final Charge charge = new Charge();
            final BigDecimal budgetedValue = new BigDecimal("10000.00");
            final BudgetItem budgetItem = new BudgetItem();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(BudgetItem.class);
                    will(returnValue(budgetItem));
                    oneOf(mockContainer).persistIfNotAlready(budgetItem);
                }

            });

            // when
            BudgetItem newBudgetItem = budgetItemRepository1.findOrCreateBudgetItem(budget, charge, budgetedValue);

            // then
            Assertions.assertThat(newBudgetItem.getBudget()).isEqualTo(budget);
            Assertions.assertThat(newBudgetItem.getCharge()).isEqualTo(charge);
            Assertions.assertThat(newBudgetItem.getBudgetedValue()).isEqualTo(budgetedValue);

        }

    }

    public static class UpdateOrCreateBudgetItemCreatingNewItem extends BudgetItemRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        BudgetItemRepository budgetItemRepository1;

        @Before
        public void setup() {
            budgetItemRepository1 = new BudgetItemRepository() {
                @Override
                public BudgetItem findByBudgetAndCharge(final Budget budget, final Charge budgetItemCharge) {
                    return null;
                }
            };
            budgetItemRepository1.setContainer(mockContainer);
        }

        @Test
        public void updateOrCreateXxxCreatingNewItem() throws Exception {

            final Budget budget = new Budget();
            final Charge charge = new Charge();
            final BigDecimal budgetedValue = new BigDecimal("10000.00");
            final BigDecimal auditedValue = new BigDecimal("10012.34");
            final BudgetItem budgetItem = new BudgetItem();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(BudgetItem.class);
                    will(returnValue(budgetItem));
                    oneOf(mockContainer).persistIfNotAlready(budgetItem);
                }

            });

            // when
            BudgetItem newBudgetItem = budgetItemRepository1.updateOrCreateBudgetItem(budget, charge, budgetedValue, auditedValue);

            // then
            Assertions.assertThat(newBudgetItem.getBudget()).isEqualTo(budget);
            Assertions.assertThat(newBudgetItem.getCharge()).isEqualTo(charge);
            Assertions.assertThat(newBudgetItem.getBudgetedValue()).isEqualTo(budgetedValue);
            Assertions.assertThat(newBudgetItem.getAuditedValue()).isEqualTo(auditedValue);

        }

    }

    public static class UpdateOrCreateBudgetItemUpdatingExistingItem extends BudgetItemRepositoryTest {

        BudgetItemRepository budgetItemRepository1;
        BudgetItem existingBudgetItem;
        Budget budget;
        Charge charge;

        @Before
        public void setup() {

            budget = new Budget();
            charge = new Charge();

            final BigDecimal existingBudgetedValue = new BigDecimal("10000.00");
            final BigDecimal existingAuditedValue = new BigDecimal("10012.34");

            existingBudgetItem = new BudgetItem();
            existingBudgetItem.setBudgetedValue(existingBudgetedValue);
            existingBudgetItem.setAuditedValue(existingAuditedValue);

            budgetItemRepository1 = new BudgetItemRepository() {
                @Override
                public BudgetItem findByBudgetAndCharge(final Budget budget, final Charge budgetItemCharge) {
                    return existingBudgetItem;
                }
            };

        }

        @Test
        public void updateOrCreateXxxUpdatingExistingItem() throws Exception {

            final BigDecimal updatedBudgetedValue = new BigDecimal("11111.00");
            final BigDecimal updatedAuditedValue = new BigDecimal("10099.99");

            // when
            BudgetItem updatedBudgetItem = budgetItemRepository1.updateOrCreateBudgetItem(budget, charge, updatedBudgetedValue, updatedAuditedValue);

            // then
            Assertions.assertThat(updatedBudgetItem.getBudgetedValue()).isEqualTo(updatedBudgetedValue);
            Assertions.assertThat(updatedBudgetItem.getAuditedValue()).isEqualTo(updatedAuditedValue);

        }

    }

}
