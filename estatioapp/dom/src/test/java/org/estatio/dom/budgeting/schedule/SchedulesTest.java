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

package org.estatio.dom.budgeting.schedule;

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
import org.estatio.dom.budgeting.ChargeForTesting;
import org.estatio.dom.budgeting.PropertyForTesting;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetForTesting;
import org.estatio.dom.charge.Charge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class SchedulesTest {

    FinderInteraction finderInteraction;

    Schedules schedules;

    @Before
    public void setup() {
        schedules = new Schedules() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Schedule> allInstances() {
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

    public static class FindByProperty extends SchedulesTest {

        @Test
        public void happyCase() {

            Property property = new PropertyForTesting();
            schedules.findScheduleByProperty(property);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Schedule.class));
            assertThat(finderInteraction.getQueryName(), is("findByProperty"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByBudget extends SchedulesTest {

        @Test
        public void happyCase() {

            Budget budget = new BudgetForTesting();
            schedules.findScheduleByBudget(budget);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Schedule.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudget"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByPropertyAndCharge extends SchedulesTest {

        @Test
        public void happyCase() {

            Property property = new PropertyForTesting();
            Charge charge = new ChargeForTesting();
            schedules.findScheduleByPropertyAndCharge(property, charge);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Schedule.class));
            assertThat(finderInteraction.getQueryName(), is("findByPropertyAndCharge"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }

    public static class FindUniqueSchedule extends SchedulesTest {

        @Test
        public void happyCase() {

            Property property = new PropertyForTesting();
            Charge charge = new ChargeForTesting();
            LocalDate startDate = new LocalDate();
            LocalDate endDate = new LocalDate();
            schedules.findUniqueSchedule(property, charge, startDate, endDate);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Schedule.class));
            assertThat(finderInteraction.getQueryName(), is("findByPropertyChargeAndDates"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object) startDate));
            assertThat(finderInteraction.getArgumentsByParameterName().get("endDate"), is((Object) endDate));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(4));
        }

    }

    public static class NewSchedule extends SchedulesTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        Schedules schedules1;

        @Before
        public void setup() {
            schedules1 = new Schedules(){
                @Override
                public List<Schedule> findScheduleByPropertyAndCharge(final Property property, final Charge charge) {
                    return Arrays.asList(new Schedule(new LocalDate(2011, 1, 1), new LocalDate(2012, 1, 1)));
                }
            };
            schedules1.setContainer(mockContainer);
        }

        @Test
        public void newSchedule() {

            //given
            Property property = new PropertyForTesting();
            Charge charge = new ChargeForTesting();
            Budget budget = new BudgetForTesting();
            LocalDate startDate = new LocalDate(2015,01,01);
            LocalDate endDate = new LocalDate(2015,12,31);
            Schedule.Status status = Schedule.Status.OPEN;
            final Schedule schedule = new Schedule();

            // expect
            context.checking(new Expectations(){
                {
                    oneOf(mockContainer).newTransientInstance(Schedule.class);
                    will(returnValue(schedule));
                    oneOf(mockContainer).persistIfNotAlready(schedule);
                }

            });

            //when
            Schedule newSchedule = schedules1.newSchedule(property, budget, startDate, endDate, charge, status);

            //then
            assertThat(newSchedule.getProperty(), is(property));
            assertThat(newSchedule.getStartDate(), is(startDate));
            assertThat(newSchedule.getEndDate(), is(endDate));
            assertThat(newSchedule.getCharge(), is(charge));
            assertThat(newSchedule.getBudget(), is(budget));
            assertThat(newSchedule.getStatus(), is(status));
        }

        @Test
        public void overlappingDates() {

            assertThat(schedules1.validateNewSchedule(null, null, new LocalDate(2011, 1, 1), new LocalDate(2011, 12, 31), null,null),
                    is("A new schedule cannot overlap an existing schedule for this charge."));
        }

        @Test
        public void wrongScheduleDates() {

            //given
            Property property = new PropertyForTesting();
            Charge charge = new ChargeForTesting();
            Budget budget = new BudgetForTesting();
            LocalDate startDate = new LocalDate(2015,12,31);
            LocalDate endDate = new LocalDate(2015,01,01);
            Schedule.Status status = Schedule.Status.OPEN;

            //when
            String validateSchedule = schedules1.validateNewSchedule(property, budget, startDate, endDate, charge, status);

            //then
            assertThat(validateSchedule, is("End date can not be before start date"));
        }


    }

}
