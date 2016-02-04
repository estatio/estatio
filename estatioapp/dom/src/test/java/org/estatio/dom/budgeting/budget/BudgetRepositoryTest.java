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

package org.estatio.dom.budgeting.budget;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.PropertyForTesting;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
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
public class BudgetRepositoryTest {

    FinderInteraction finderInteraction;

    BudgetRepository budgetRepository;

    @Before
    public void setup() {
        budgetRepository = new BudgetRepository() {

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
            protected List<Budget> allInstances() {
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

    public static class FindByProperty extends BudgetRepositoryTest {

        @Test
        public void happyCase() {

            Property property = new PropertyForTesting();
            budgetRepository.findByProperty(property);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Budget.class));
            assertThat(finderInteraction.getQueryName(), is("findByProperty"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByPropertyAndDates extends BudgetRepositoryTest {

        @Test
        public void happyCase() {

            Property property = new PropertyForTesting();
            LocalDate startDate = new LocalDate();
            budgetRepository.findByPropertyAndStartDate(property, startDate);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Budget.class));
            assertThat(finderInteraction.getQueryName(), is("findByPropertyAndStartDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object) startDate));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }

    public static class NewBudget extends BudgetRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        BudgetRepository budgetRepository;

        @Before
        public void setup() {
            budgetRepository = new BudgetRepository(){
                @Override
                public List<Budget> findByProperty(final Property property) {
                    return Arrays.asList(new Budget(new LocalDate(2011, 1, 1), new LocalDate(2012, 1, 1)));
                }
            };
            budgetRepository.setContainer(mockContainer);
        }

        @Test
        public void newBudget() {

            //given
            Property property = new PropertyForTesting();
            LocalDate startDate = new LocalDate(2015,01,01);
            LocalDate endDate = new LocalDate(2015,12,31);
            final Budget budget = new Budget();

            // expect
            context.checking(new Expectations(){
                {
                    oneOf(mockContainer).newTransientInstance(Budget.class);
                    will(returnValue(budget));
                    oneOf(mockContainer).persistIfNotAlready(budget);
                }

            });

            //when
            Budget newBudget = budgetRepository.newBudget(property, startDate, endDate);

            //then
            assertThat(newBudget.getProperty(), is(property));
            assertThat(newBudget.getStartDate(), is(startDate));
            assertThat(newBudget.getEndDate(), is(endDate));

        }

        @Test
        public void overlappingDates() {

            assertThat(budgetRepository.validateNewBudget(null, new LocalDate(2011,1,1), new LocalDate(2011,12,31)),
                    is("A budget cannot overlap an existing budget."));
        }

        @Test
        public void wrongBudgetDates() {

            //given
            Property property = new PropertyForTesting();
            LocalDate startDate = new LocalDate(2015,12,31);
            LocalDate endDate = new LocalDate(2015,01,01);

            //when
            String validateBudget = budgetRepository.validateNewBudget(property, startDate, endDate);

            //then
            assertThat(validateBudget, is("End date can not be before start date"));

        }

        @Test
        public void emptyStartDate() {

            //given
            Property property = new PropertyForTesting();
            LocalDate startDate = null;
            LocalDate endDate = new LocalDate(2015,01,01);

            //when
            String validateBudget = budgetRepository.validateNewBudget(property, startDate, endDate);

            //then
            assertThat(validateBudget, is("Start date is mandatory"));

        }

    }

}
