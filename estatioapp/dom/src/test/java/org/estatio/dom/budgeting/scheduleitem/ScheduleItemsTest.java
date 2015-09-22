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

package org.estatio.dom.budgeting.scheduleitem;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.schedule.Schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class ScheduleItemsTest {

    FinderInteraction finderInteraction;

    ScheduleItems scheduleItems;

    @Before
    public void setup() {
        scheduleItems = new ScheduleItems() {

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
            protected List<ScheduleItem> allInstances() {
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

    public static class FindBySchedule extends ScheduleItemsTest {

        @Test
        public void happyCase() {

            Schedule schedule = new Schedule();
            scheduleItems.findBySchedule(schedule);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(ScheduleItem.class));
            assertThat(finderInteraction.getQueryName(), is("findBySchedule"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("schedule"), is((Object) schedule));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByBudgetItem extends ScheduleItemsTest {

        @Test
        public void happyCase() {

            BudgetItem budgetItem = new BudgetItem();
            scheduleItems.findByBudgetItem(budgetItem);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(ScheduleItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetItem"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem"), is((Object) budgetItem));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByKeyTable extends ScheduleItemsTest {

        @Test
        public void happyCase() {

            KeyTable keyTable = new KeyTable();
            scheduleItems.findByKeyTable(keyTable);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(ScheduleItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByKeyTable"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyTable"), is((Object) keyTable));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByScheduleAndBudgetItemAndKeyTable extends ScheduleItemsTest {

        @Test
        public void happyCase() {

            Schedule schedule = new Schedule();
            BudgetItem budgetItem = new BudgetItem();
            KeyTable keyTable = new KeyTable();
            scheduleItems.findByScheduleAndBudgetItemAndKeyTable(schedule, budgetItem, keyTable);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(ScheduleItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByScheduleAndBudgetItemAndKeyTable"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("schedule"), is((Object) schedule));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem"), is((Object) budgetItem));
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyTable"), is((Object) keyTable));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }


}
