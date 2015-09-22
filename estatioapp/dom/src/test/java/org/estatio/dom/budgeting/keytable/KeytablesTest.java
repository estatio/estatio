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

import java.util.Arrays;
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
import org.estatio.dom.budgeting.PropertyForTesting;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class KeytablesTest {

    FinderInteraction finderInteraction;

    KeyTables keyTables;

    @Before
    public void setup() {
        keyTables = new KeyTables() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
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

    public static class findByName extends KeytablesTest {

        @Test
        public void happyCase() {

            keyTables.findByName("name");

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(KeyTable.class));
            assertThat(finderInteraction.getQueryName(), is("findKeyTableByName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is((Object) "name"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

        @Test
         public void anotherHappyCase() {

            Property property = new PropertyForTesting();
            keyTables.findByProperty(property);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(KeyTable.class));
            assertThat(finderInteraction.getQueryName(), is("findByProperty"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class NewKeyTable extends KeytablesTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        KeyTables keyTablesRepo;

        @Before
        public void setup() {
            keyTablesRepo = new KeyTables() {
                @Override
                public List<KeyTable> findByProperty(final Property property) {
                    return Arrays.asList(new KeyTable(new LocalDate(2011, 1, 1), new LocalDate(2012, 1, 1)));
                }
            };
            keyTablesRepo.setContainer(mockContainer);
        }

        @Test
        public void newKeyTable() {

            //given
            Property property = new PropertyForTesting();
            LocalDate startDate = new LocalDate(2015, 01, 01);
            LocalDate endDate = new LocalDate(2015, 12, 31);
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
            KeyTable newKeyTable = keyTablesRepo.newKeyTable(
                    property,
                    "new keyTable",
                    startDate,
                    endDate,
                    FoundationValueType.AREA,
                    KeyValueMethod.PERCENT,
                    6);

            //then
            assertThat(newKeyTable.getProperty(), is(property));
            assertThat(newKeyTable.getStartDate(), is(startDate));
            assertThat(newKeyTable.getEndDate(), is(endDate));
            assertThat(newKeyTable.getName(), is("new keyTable"));
            assertThat(newKeyTable.getFoundationValueType(), is(FoundationValueType.AREA));
            assertThat(newKeyTable.getKeyValueMethod(), is(KeyValueMethod.PERCENT));
            assertThat(newKeyTable.getNumberOfDigits(), is(6));
        }

        @Test
        public void validateNewKeyTable() {

            assertThat(keyTablesRepo.validateNewKeyTable(null, null, new LocalDate(2011, 1, 1), new LocalDate(2010, 12, 31), null, null, null),
                    is("End date can not be before start date"));
            assertThat(keyTablesRepo.validateNewKeyTable(null, null, new LocalDate(2011, 1, 1), new LocalDate(2011, 1, 1), null, null, null),
                    is(nullValue()));

        }
    }

}
