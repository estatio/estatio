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

package org.estatio.dom.budgeting.keytable;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetForTesting;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class KeytableRepositoryTest {

    FinderInteraction finderInteraction;

    KeyTableRepository keyTableRepository;

    @Before
    public void setup() {
        keyTableRepository = new KeyTableRepository() {

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
            protected List<KeyTable> allInstances() {
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

    public static class FindByPropertyAndNameAndStartDate extends KeytableRepositoryTest {

        @Test
        public void happyCase() {

            Budget budget = new BudgetForTesting();
            String name = "KeyTableName";
            keyTableRepository.findByBudgetAndName(budget, name);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(KeyTable.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetAndName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is((Object) name));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }



    }

    public static class FindByBudget extends KeytableRepositoryTest {

        @Test
        public void happyCase() {

            Budget budget = new BudgetForTesting();
            keyTableRepository.findByBudget(budget);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(KeyTable.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudget"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }



    }


    public static class NewKeyTable extends KeytableRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        KeyTableRepository keyTableRepositoryRepo;

        @Before
        public void setup() {
            keyTableRepositoryRepo = new KeyTableRepository() {
                @Override
                public List<KeyTable> findByBudget(final Budget budget) {
                    return Arrays.asList(new KeyTable());
                }
            };
            keyTableRepositoryRepo.setContainer(mockContainer);
        }

        @Test
        public void newKeyTable() {

            //given
            Budget budget = new BudgetForTesting();
            final KeyTable keyTable = new KeyTable();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(KeyTable.class);
                    will(returnValue(keyTable));
                    oneOf(mockContainer).persistIfNotAlready(keyTable);
                }

            });

            //when
            KeyTable newKeyTable = keyTableRepositoryRepo.newKeyTable(
                    budget,
                    "new keyTable",
                    FoundationValueType.AREA,
                    KeyValueMethod.PERCENT,
                    6);

            //then
            assertThat(newKeyTable.getBudget(), is(budget));
            assertThat(newKeyTable.getName(), is("new keyTable"));
            assertThat(newKeyTable.getFoundationValueType(), is(FoundationValueType.AREA));
            assertThat(newKeyTable.getKeyValueMethod(), is(KeyValueMethod.PERCENT));
            assertThat(newKeyTable.getPrecision(), is(6));
        }

    }

}
