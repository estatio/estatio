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

package org.estatio.dom.budget;

import java.util.List;

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class BudgetsTest {

    FinderInteraction finderInteraction;

    Budgets budgets;

    @Before
    public void setup() {
        budgets = new Budgets() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
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

    public static class findByProperty extends BudgetsTest {

        @Test
        public void happyCase() {

            Property property = new PropertyForTesting();
            budgets.findByProperty(property);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Budget.class));
            assertThat(finderInteraction.getQueryName(), is("findByProperty"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class NewBudget extends BudgetsTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        Budgets budgets;

        @Before
        public void setup() {
            budgets = new Budgets();
            budgets.setContainer(mockContainer);
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
            Budget newBudget = budgets.newBudget(property, startDate, endDate);

            //then
            assertThat(newBudget.getProperty(), is(property));
            assertThat(newBudget.getStartDate(), is(startDate));
            assertThat(newBudget.getEndDate(), is(endDate));
        }

        @Test
        public void wrongBudgetDates() {

            //given
            Property property = new PropertyForTesting();
            LocalDate startDate = new LocalDate(2015,12,31);
            LocalDate endDate = new LocalDate(2015,01,01);

            //when
            String validateBudget = budgets.validateNewBudget(property, startDate, endDate);

            //then
            assertThat(validateBudget, is("End date can not be before start date"));
        }


    }

}
