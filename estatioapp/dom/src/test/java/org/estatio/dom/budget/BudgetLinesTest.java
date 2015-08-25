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

import java.math.BigDecimal;
import java.util.List;

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class BudgetLinesTest {

    FinderInteraction finderInteraction;

    BudgetLines budgetLines;

    @Before
    public void setup() {
        budgetLines = new BudgetLines() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<BudgetLine> allInstances() {
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

    public static class findByProperty extends BudgetLinesTest {

        @Test
        public void happyCase() {

            BudgetItem budgetItem = new BudgetItemForTesting();
            budgetLines.findByBudgetItem(budgetItem);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetLine.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetItem"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem"), is((Object) budgetItem));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class NewBudgetLine extends BudgetLinesTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        BudgetLines budgetLines1;

        @Before
        public void setup() {
            budgetLines1 = new BudgetLines();
            budgetLines1.setContainer(mockContainer);
        }

        @Test
        public void newBudgetLine() {

            //given
            BudgetItem budgetItem = new BudgetItemForTesting();
            BudgetKeyItem budgetKeyItem = new BudgetKeyItemForTesting();
            BigDecimal value = BigDecimal.ONE;
            final BudgetLine budgetLine = new BudgetLine();

            // expect
            context.checking(new Expectations(){
                {
                    oneOf(mockContainer).newTransientInstance(BudgetLine.class);
                    will(returnValue(budgetLine));

                    oneOf(mockContainer).persistIfNotAlready(budgetLine);
                }

            });

            //when
            BudgetLine newBudgetLine = budgetLines1.newBudgetLine(value, budgetItem, budgetKeyItem);

            //then
            assertThat(newBudgetLine.getValue(), is(value));
            assertThat(newBudgetLine.getBudgetItem(), is(budgetItem));
            assertThat(newBudgetLine.getBudgetKeyItem(), is(budgetKeyItem));
        }


    }

}
