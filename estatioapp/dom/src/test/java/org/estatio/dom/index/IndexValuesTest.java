/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.index;

import static org.apache.isis.core.commons.matchers.IsisMatchers.classEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

public class IndexValuesTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    QueryResultsCache mockQueryResultsCache;

    FinderInteraction finderInteraction;

    IndexValues indexValues;

    Index index;

    LocalDate startDate;

    @Before
    public void setup() {

        index = new Index();
        startDate = new LocalDate(2013, 4, 1);

        indexValues = new IndexValues() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<IndexValue> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };

        indexValues.queryResultsCache = mockQueryResultsCache;
    }

    public static class FindIndexValueByIndexAndStartDate extends IndexValuesTest {

        @Test
        public void happyCase() {

            // given
            context.checking(new Expectations() {
                {
                    oneOf(mockQueryResultsCache).execute(
                            with(any(Callable.class)),
                            with(classEqualTo(IndexValues.class)),
                            with(equalTo("findIndexValueByIndexAndStartDate")),
                            with(arrayOf(index, startDate)));
                    will(executeCallableAndReturn());
                }
            });

            // when
            indexValues.findIndexValueByIndexAndStartDate(index, startDate);

            // then
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), classEqualTo(IndexValue.class));
            assertThat(finderInteraction.getQueryName(), is("findByIndexAndStartDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("index"), is((Object) index));
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object) startDate));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

        private static Matcher<Object[]> arrayOf(final Object... elements) {
            return new TypeSafeMatcher<Object[]>() {
                @Override
                protected boolean matchesSafely(Object[] item) {
                    return Arrays.deepEquals(elements, item);
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("array of ").appendValueList("[", ",", "]", elements);
                }
            };
        }

        private static Action executeCallableAndReturn() {
            return new Action() {
                @Override
                public Object invoke(Invocation invocation) throws Throwable {
                    Callable<Object> callable = (Callable<Object>) invocation.getParameter(0);
                    return callable.call();
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("execute arg 0 as callable and return");
                }
            };
        }

    }

}
