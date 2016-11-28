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
package org.estatio.integtests.lease.breaks;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.integtests.VT;

import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakOption;
import org.estatio.dom.lease.breaks.BreakOptionRepository;
import org.estatio.dom.lease.breaks.BreakType;
import org.estatio.dom.lease.breaks.RollingBreakOption;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.EstatioFakeDataService;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseBuilder;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BreakOptionRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    BreakOptionRepository breakOptionRepository;

    @Inject
    LeaseRepository leaseRepository;

    public static class FindByLease extends BreakOptionRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                }
            });
        }

        @Test
        public void findByLease() throws Exception {
            // given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            List<BreakOption> result = breakOptionRepository.findByLease(lease);

            // then
            assertThat(result.size(), is(2));
        }
    }

    public static class NewBreakOption extends BreakOption_IntegTest {

        LeaseBuilder fs;
        @Inject
        EventRepository eventRepository;
        @Inject
        ClockService clockService;
        @Inject
        EstatioFakeDataService fakeDataService;

        @Before
        public void setup() {
            getFixtureClock().clear();
            getFixtureClock().setDate(2014, 7, 1);

            fs = new LeaseBuilder() {{
                setStartDate(VT.ld(2014, 4, 1));
                setDuration("P10y");
            }};
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, fs);
                }
            });
        }

        @Before
        public void tearDown() {
            getFixtureClock().reset();
        }

        @Test
        public void whenRolling() throws Exception {

            // given
            Lease lease = fs.getLease();
            Assertions.assertThat(lease.getBreakOptions()).isEmpty();

            // when
            final LocalDate breakDate = VT.ld(2014, 11, 1); // 4 months from now
            final String notificationPeriodStr = "3m";
            final BreakType breakType = BreakType.ROLLING;
            final BreakExerciseType breakExerciseType = fakeDataService.collections().anEnum(BreakExerciseType.class);
            final String description = fakeDataService.lorem().sentence();

            breakOptionRepository.newBreakOption(lease, breakDate, notificationPeriodStr, breakType, breakExerciseType, description);
            nextTransaction();

            // then
            Assertions.assertThat(lease.getBreakOptions()).hasSize(1);
            final BreakOption breakOption = lease.getBreakOptions().first();

            Assertions.assertThat(breakOption.getLease()).isEqualTo(lease);

            Assertions.assertThat(breakOption.getBreakDate()).isEqualTo(VT.ld(2014, 11, 1));
            Assertions.assertThat(breakOption.getNotificationPeriod()).isEqualTo("3m");
            Assertions.assertThat(breakOption.getType()).isEqualTo(BreakType.ROLLING);
            Assertions.assertThat(breakOption).isInstanceOf(RollingBreakOption.class);

            Assertions.assertThat(breakOption.getExerciseType()).isEqualTo(breakExerciseType);
            Assertions.assertThat(breakOption.getDescription()).isEqualTo(description);

            Assertions.assertThat(breakOption.getExerciseDate()).isEqualTo(VT.ld(2014, 8, 1)); // 3 months before break date
            Assertions.assertThat(breakOption.getCurrentBreakDate()).isEqualTo(breakDate);

            Assertions.assertThat(breakOption.getCalendarEvents()).hasSize(1);

            final List<Event> eventList = eventRepository.findBySource(breakOption);
            Assertions.assertThat(eventList).hasSize(1);
            final Event event = eventList.get(0);
            Assertions.assertThat(event.getDate()).isEqualTo(breakOption.getExerciseDate());
            Assertions.assertThat(event.getCalendarName()).isEqualTo("Rolling break exercise");
            Assertions.assertThat(event.getSource()).isEqualTo(breakOption);

            final Set<String> calendarNames = breakOption.getCalendarNames();
            Assertions.assertThat(calendarNames).containsExactly("Rolling break exercise");

        }
    }


}