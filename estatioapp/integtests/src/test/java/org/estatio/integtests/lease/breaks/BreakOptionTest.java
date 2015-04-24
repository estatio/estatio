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
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.event.Events;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakOption;
import org.estatio.dom.lease.breaks.BreakOptions;
import org.estatio.dom.lease.breaks.BreakType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BreakOptionTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    @Inject
    Events events;

    @Inject
    BreakOptions breakOptions;

    public static class Change extends BreakOptionTest {

        Lease lease;
        BreakOption breakOption;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                }
            });

            lease = leases.findLeaseByReference(LeaseBreakOptionsForOxfTopModel001.LEASE_REF);

            assertThat(breakOptions.allBreakOptions().size(), is(2));
            final List<BreakOption> breakOptionList = breakOptions.findByLease(lease);
            assertThat(breakOptionList.size(), is(2));
            breakOption = breakOptionList.get(0);
        }

        @Test
        public void happyCase() throws Exception {

            // given
            assertThat(breakOption.getType(), is(BreakType.FIXED));
            assertThat(breakOption.getExerciseType(), is(BreakExerciseType.MUTUAL));

            // when
            breakOption.change(BreakType.ROLLING, BreakExerciseType.TENANT, "Something");

            // then
            assertThat(breakOption.getType(), is(BreakType.ROLLING));
            assertThat(breakOption.getExerciseType(), is(BreakExerciseType.TENANT));

        }
    }

    public static class ChangeDates extends BreakOptionTest {

        Lease lease;
        BreakOption breakOption;

        @Before
        public void setup() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                }
            });

            lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);

            assertThat(breakOptions.allBreakOptions().size(), is(2));
            final List<BreakOption> breakOptionList = breakOptions.findByLease(lease);
            assertThat(breakOptionList.size(), is(2));
            breakOption = breakOptionList.get(0);
        }

        @Test
        public void happyCase() throws Exception {
            // given
            assertThat(breakOption.getType(), is(BreakType.FIXED));
            assertThat(breakOption.getExerciseType(), is(BreakExerciseType.MUTUAL));
            assertThat(breakOption.getBreakDate(), is(lease.getStartDate().plusYears(5)));
            assertThat(breakOption.getExerciseDate(), is(lease.getStartDate().plusYears(5).minusMonths(6)));
            assertThat(events.allEvents().size(), is(3));

            // when
            breakOption.changeDates(lease.getStartDate().plusYears(2), lease.getStartDate().plusYears(2).minusMonths(6));

            // then
            assertThat(breakOption.getBreakDate(), is(lease.getStartDate().plusYears(2)));
            assertThat(breakOption.getExerciseDate(), is(lease.getStartDate().plusYears(2).minusMonths(6)));
            assertThat(events.allEvents().size(), is(3));
        }
    }

}