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

package org.estatio.dom.budgeting.allocation;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BudgetItemAllocationRepositoryTest {

    FinderInteraction finderInteraction;

    BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Before
    public void setup() {
        budgetItemAllocationRepository = new BudgetItemAllocationRepository() {

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
            protected List<BudgetItemAllocation> allInstances() {
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

    public static class FindByBudgetItem extends BudgetItemAllocationRepositoryTest {

        @Test
        public void happyCase() {

            BudgetItem budgetItem = new BudgetItem();
            budgetItemAllocationRepository.findByBudgetItem(budgetItem);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItemAllocation.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetItem"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem"), is((Object) budgetItem));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByKeyTable extends BudgetItemAllocationRepositoryTest {

        @Test
        public void happyCase() {

            KeyTable keyTable = new KeyTable();
            budgetItemAllocationRepository.findByKeyTable(keyTable);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItemAllocation.class));
            assertThat(finderInteraction.getQueryName(), is("findByKeyTable"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyTable"), is((Object) keyTable));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByBudgetItemAndKeyTable extends BudgetItemAllocationRepositoryTest {

        @Test
        public void happyCase() {

            Charge charge = new Charge();
            BudgetItem budgetItem = new BudgetItem();
            KeyTable keyTable = new KeyTable();
            budgetItemAllocationRepository.findByChargeAndBudgetItemAndKeyTable(charge, budgetItem, keyTable);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItemAllocation.class));
            assertThat(finderInteraction.getQueryName(), is("findByChargeAndBudgetItemAndKeyTable"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem"), is((Object) budgetItem));
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyTable"), is((Object) keyTable));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }

    public static class FindOrCreateBudgetItemAllocationNewAllocation extends BudgetItemAllocationRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        BudgetItemAllocationRepository budgetItemAllocationRepository1;

        @Before
        public void setup() {
            budgetItemAllocationRepository1 = new BudgetItemAllocationRepository() {
                @Override
                public BudgetItemAllocation findByChargeAndBudgetItemAndKeyTable(final Charge charge, final BudgetItem budgetItem, final KeyTable keyTable) {
                    return null;
                }
            };
            budgetItemAllocationRepository1.setContainer(mockContainer);
        }

        @Test
        public void findOrCreateXxxCreatingNewAllocation() throws Exception {

            final KeyTable keyTable = new KeyTable();
            final Charge charge = new Charge();
            final BudgetItem budgetItem = new BudgetItem();
            final BigDecimal percentage = new BigDecimal("100.000000");
            final BudgetItemAllocation budgetItemAllocation = new BudgetItemAllocation();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(BudgetItemAllocation.class);
                    will(returnValue(budgetItemAllocation));
                    oneOf(mockContainer).persistIfNotAlready(budgetItemAllocation);
                }

            });

            // when
            BudgetItemAllocation newBudgetItemAllocation = budgetItemAllocationRepository1.findOrCreateBudgetItemAllocation(budgetItem, charge, keyTable, percentage);

            // then
            Assertions.assertThat(newBudgetItemAllocation.getCharge()).isEqualTo(charge);
            Assertions.assertThat(newBudgetItemAllocation.getBudgetItem()).isEqualTo(budgetItem);
            Assertions.assertThat(newBudgetItemAllocation.getKeyTable()).isEqualTo(keyTable);
            Assertions.assertThat(newBudgetItemAllocation.getPercentage()).isEqualTo(percentage);

        }

    }

}
