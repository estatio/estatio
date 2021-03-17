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
package org.estatio.module.lease.integtests.prolongation;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.prolongation.Lease_newProlongationOption;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOption;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOptionRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.prolongation.enums.ProlongationOption_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class ProlongationOptionRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    ProlongationOptionRepository prolongationOptionRepository;

    @Inject
    LeaseRepository leaseRepository;

    public static class FindByLease extends ProlongationOptionRepository_IntegTest {

        @Before
        public void setupData() {
            runBuilderScript(ProlongationOption_enum.OxfTopModel001.builder());
        }

        @Test
        public void findByLease() throws Exception {
            // given
            Lease lease = ProlongationOption_enum.OxfTopModel001.getLease_d().findUsing(serviceRegistry);

            // when
            final ProlongationOption option = prolongationOptionRepository.findByLease(lease).get(0);

            // then
            assertThat(option).isNotNull();
            assertThat(option.getProlongationPeriod()).isEqualToIgnoringCase("5y");
            assertThat(option.getNotificationPeriod()).isEqualToIgnoringCase("6m");
        }
    }

    public static class NewProlongationOption extends ProlongationOptionRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(ProlongationOption_enum.OxfTopModel001.builder());
        }

        @Test
        public void validation_works() throws Exception {
            // given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            assertThat(prolongationOptionRepository.findByLease(lease)).isNotEmpty();

            // expect
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("Reason: This lease already has a PROLONGATION break option for this date");

            // when
            wrap(mixin(Lease_newProlongationOption.class, lease)).$$("1y", "6m", "second option");
        }

        @Test
        public void multiple_prolongation_options_possible() throws Exception {

            // given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            LocalDate leaseEndDate = lease.getEndDate();

            assertThat(leaseEndDate).isEqualTo(new LocalDate(2022, 7,14));
            assertThat(prolongationOptionRepository.findByLease(lease).size()).isEqualTo(1);
            final ProlongationOption option1 = prolongationOptionRepository.findByLease(lease).get(0);
            assertThat(option1.getBreakDate()).isEqualTo(leaseEndDate);


            // when
            lease.setEndDate(leaseEndDate.plusDays(1));
            wrap(mixin(Lease_newProlongationOption.class, lease)).$$("1y", "6m", "second option");

            // then
            assertThat(prolongationOptionRepository.findByLease(lease).size()).isEqualTo(2);
            final ProlongationOption option2 = prolongationOptionRepository.findByLease(lease).get(1);
            assertThat(option2.getBreakDate()).isEqualTo(leaseEndDate.plusDays(1));


        }


    }

    public static class Prolong extends ProlongationOptionRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(ProlongationOption_enum.OxfTopModel001.builder());
        }

        @Test
        public void prolong_works() throws Exception {
            // given
            Lease lease = ProlongationOption_enum.OxfTopModel001.getLease_d().findUsing(serviceRegistry);
            LocalDate leaseEndDate = lease.getEndDate();
            assertThat(leaseEndDate).isEqualTo(new LocalDate(2022, 7,14));
            lease.setTenancyEndDate(null); // needed since EST-1804

            ProlongationOption option1 = prolongationOptionRepository.findByLease(lease).get(0);

            // when
            wrap(option1).prolong();

            // then
            assertThat(lease.getEndDate()).isEqualTo(new LocalDate(2027, 7, 14));
            ProlongationOption option2 = prolongationOptionRepository.findByLease(lease).get(1);
            assertThat(option2.getProlongationPeriod()).isEqualTo(option1.getProlongationPeriod());
            assertThat(option2.getNotificationPeriod()).isEqualTo(option1.getNotificationPeriod());
            assertThat(option2.getDescription()).isEqualTo(option1.getDescription());
            assertThat(option2.getCalendarEvents().size()).isEqualTo(2);

        }

        @Test
        public void prolong_sets_end_of_next_month_correctly() throws Exception {
            // given
            Lease lease = ProlongationOption_enum.OxfTopModel001.getLease_d().findUsing(serviceRegistry);
            lease.setEndDate(new LocalDate(2022, 6, 30));
            lease.setTenancyEndDate(null); // needed since EST-1804

            ProlongationOption option1 = prolongationOptionRepository.findByLease(lease).get(0);
            option1.setProlongationPeriod("1m");
            option1.setBreakDate(new LocalDate(2022, 6, 30));

            // when
            wrap(option1).prolong();

            // then
            assertThat(lease.getEndDate()).isEqualTo(new LocalDate(2022, 7, 31));
        }


    }

}