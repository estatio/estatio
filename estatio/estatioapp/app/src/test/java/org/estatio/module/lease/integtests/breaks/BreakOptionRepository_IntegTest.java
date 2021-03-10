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
package org.estatio.module.lease.integtests.breaks;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.base.integtests.VT;

import org.estatio.module.event.dom.Event;
import org.estatio.module.event.dom.EventRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.BreakExerciseType;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.breaks.BreakType;
import org.estatio.module.lease.dom.breaks.RollingBreakOption;
import org.estatio.module.lease.fixtures.breakoptions.enums.BreakOption_enum;
import org.estatio.module.lease.fixtures.lease.builders.LeaseBuilderLEGACY;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BreakOptionRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    BreakOptionRepository breakOptionRepository;

    @Inject
    LeaseRepository leaseRepository;

    public static class FindByLease extends BreakOptionRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext ec) {
                    ec.executeChildren(this,
                            BreakOption_enum.OxfTopModel001Gb_FIXED,
                            BreakOption_enum.OxfTopModel001Gb_ROLLING);
                }
            });
        }

        @Test
        public void findByLease() throws Exception {
            // given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

            // when
            List<BreakOption> result = breakOptionRepository.findByLease(lease);

            // then
            assertThat(result.size(), is(2));
        }
    }

    public static class NewBreakOption extends BreakOption_IntegTest {

        LeaseBuilderLEGACY fs;
        @Inject
        EventRepository eventRepository;
        @Inject
        ClockService clockService;
        @Inject
        FakeDataService fakeDataService;

        @Before
        public void setup() {

            setFixtureClockDate(2014, 7, 1);

            fs = new LeaseBuilderLEGACY() {{
                setStartDate(VT.ld(2014, 4, 1));
                setDuration("P10y");
            }};
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, fs);
                }
            });

        }

        @Test
        public void whenRolling() throws Exception {

            // given
            Lease lease = fs.getLease();
            Assertions.assertThat(lease.getBreakOptions()).isEmpty();

            LocalDate currentDate = clockService.now();

            // when
            final LocalDate breakDate = currentDate.plusMonths(4);
            final String notificationPeriodStr = "3m";
            final BreakType breakType = BreakType.ROLLING;
            final BreakExerciseType breakExerciseType = fakeDataService.enums().anyOf(BreakExerciseType.class);
            final String description = fakeDataService.lorem().sentence();

            breakOptionRepository.newBreakOption(lease, breakDate, notificationPeriodStr, breakType, breakExerciseType, description);
            transactionService.nextTransaction();

            // then
            Assertions.assertThat(lease.getBreakOptions()).hasSize(1);
            final BreakOption breakOption = lease.getBreakOptions().first();

            Assertions.assertThat(breakOption.getLease()).isEqualTo(lease);

            Assertions.assertThat(breakOption.getBreakDate()).isEqualTo(breakDate);
            Assertions.assertThat(breakOption.getNotificationPeriod()).isEqualTo("3m");
            Assertions.assertThat(breakOption.getType()).isEqualTo(BreakType.ROLLING);
            Assertions.assertThat(breakOption).isInstanceOf(RollingBreakOption.class);

            Assertions.assertThat(breakOption.getExerciseType()).isEqualTo(breakExerciseType);
            Assertions.assertThat(breakOption.getDescription()).isEqualTo(description);

            Assertions.assertThat(breakOption.getExerciseDate()).isEqualTo(breakDate.minusMonths(3));

            // and given, meaning that...
            currentDate = clockService.now();
            LocalDate exerciseDate = breakOption.getExerciseDate();
            Assertions.assertThat(currentDate).isLessThan(exerciseDate);

            // then also
            Assertions.assertThat(breakOption.getCurrentBreakDate()).isEqualTo(exerciseDate.plusMonths(2));

            Assertions.assertThat(breakOption.getCalendarEvents()).hasSize(1);

            final List<Event> eventList = eventRepository.findBySource(breakOption);
            Assertions.assertThat(eventList).hasSize(1);
            final Event event = eventList.get(0);
            Assertions.assertThat(event.getDate()).isEqualTo(breakOption.getExerciseDate());
            Assertions.assertThat(event.getCalendarName()).isEqualTo("Rolling break exercise");
            Assertions.assertThat(event.getSource()).isEqualTo(breakOption);

            final Set<String> calendarNames = breakOption.getCalendarNames();
            Assertions.assertThat(calendarNames).containsExactly("Rolling break exercise");

            // and when
            final LocalDate exerciseDatePlus1 = breakOption.getExerciseDate().plusMonths(1);
            setFixtureClockDate(exerciseDatePlus1);

            // meaning that...
            currentDate = clockService.now();
            exerciseDate = breakOption.getExerciseDate();
            Assertions.assertThat(currentDate).isGreaterThan(exerciseDate);

            // then

            // this fails if run after 11pm... timezone issues
            // Assertions.assertThat(breakOption.getCurrentBreakDate()).isEqualTo(currentDate.plusMonths(2));

            Assertions.assertThat(breakOption.getCurrentBreakDate())
                    .isBetween(currentDate.plusMonths(2).minusDays(1), currentDate.plusMonths(2));

        }
    }


}