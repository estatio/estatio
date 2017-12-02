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

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.event.dom.EventRepository;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.BreakExerciseType;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.breaks.BreakType;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BreakOption_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    BreakOptionRepository breakOptionRepository;

    public static class Change extends BreakOption_IntegTest {

        Lease lease;
        BreakOption breakOption;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                }
            });

            lease = leaseRepository.findLeaseByReference(LeaseBreakOptionsForOxfTopModel001.LEASE_REF);

            final List<BreakOption> breakOptions = breakOptionRepository.allBreakOptions();
            assertThat(breakOptions.size(), is(2));
            final List<BreakOption> breakOptionList = breakOptionRepository.findByLease(lease);
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

    public static class ChangeDates extends BreakOption_IntegTest {

        Lease lease;

        @Before
        public void setup() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });

            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);


        }

        @Test
        public void happyCase() throws Exception {
            // given
            final LocalDate breakDate = this.lease.getStartDate().plusYears(1);
            final BreakOption breakOption = breakOptionRepository.newBreakOption(
                    this.lease,
                    breakDate,
                    "5m",
                    BreakType.FIXED,
                    BreakExerciseType.MUTUAL,
                    "IntegTest");

            // when
            final LocalDate newBreakDate = lease.getStartDate().plusYears(2);
            breakOption.changeDates(
                    newBreakDate,
                    newBreakDate.minusMonths(6) );

            // then
            assertThat(breakOption.getBreakDate(), is(newBreakDate));
            assertThat(breakOption.getExerciseDate(), is(newBreakDate.minusMonths(6)));
            assertThat(breakOption.getNotificationPeriod(), is("6m"));
        }

    }

    public static class ChangeNotificationPeriod extends BreakOption_IntegTest {

        Lease lease;

        @Before
        public void setup() {
            lease = runBuilderScript(Lease_enum.OxfTopModel001Gb.builder());
        }

        @Test
        public void happyCase() throws Exception {
            // given
            final LocalDate breakDate = this.lease.getStartDate().plusYears(1);
            final BreakOption breakOption = breakOptionRepository.newBreakOption(
                    this.lease,
                    breakDate,
                    "5m",
                    BreakType.FIXED,
                    BreakExerciseType.MUTUAL,
                    "IntegTest");

            assertThat(breakOption.getExerciseDate(), is(breakDate.minusMonths(5)));


            // when
            breakOption.changeNotificationPeriod("6m");

            // then
            assertThat(breakOption.getExerciseDate(), is(breakDate.minusMonths(6)));
        }

    }

    public static class Remove extends BreakOption_IntegTest {

        Lease lease;
        BreakOption breakOption;

        @Before
        public void setup() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                }
            });

            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

            assertThat(breakOptionRepository.allBreakOptions().size(), is(2));
            assertThat(breakOptionRepository.findByLease(lease).size(), is(2));

        }

        @Test
        public void happyCase() throws Exception {

            // given
            breakOption = breakOptionRepository.findByLease(lease).get(0);

            // when
            breakOption.remove("For some reason");

            // then
            assertThat(breakOptionRepository.findByLease(lease).size(), is(1));

        }
    }

}