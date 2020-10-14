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
package org.estatio.module.turnover.integtests;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverReportingConfig_IntegTest extends TurnoverModuleIntegTestAbstract {

    public static class ChangeStartDate extends TurnoverReportingConfig_IntegTest {

        Occupancy occupancy;
        LocalDate startDate;
        Currency euro;
        TurnoverReportingConfig config;
        LocalDateTime reportedAt;


        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Currency_enum.EUR.builder());
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });

            occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
            startDate = new LocalDate(2013, 1, 1);
            euro = Currency_enum.EUR.findUsing(serviceRegistry2);
            reportedAt = new LocalDateTime(2014, 1, 1, 12, 0);
            config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.PRELIMINARY, null, startDate, Frequency.YEARLY, euro);

            turnoverRepository.create(config, startDate, Type.PRELIMINARY, Frequency.YEARLY, Status.NEW, reportedAt, "someone", euro, null, null, null, null, false);
            turnoverRepository.create(config, startDate.plusYears(1), Type.PRELIMINARY, Frequency.YEARLY, Status.NEW, reportedAt, "someone", euro, null, null, null, null, false);

        }

        @Test
        public void changeStartDate_works() throws Exception {

            // given
            LocalDate newStartDate = new LocalDate(2012, 1, 1);
            assertThat(config.getTurnovers()).hasSize(2);

            // when
            wrap(config).changeStartDate(newStartDate);

            // then
            assertThat(config.getStartDate()).isEqualTo(newStartDate);
            assertThat(config.getTurnovers()).hasSize(3);

            // and then
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage("Cannot change start date when there are already turnovers reported");

            // when
            config.getTurnovers().get(0).setStatus(Status.APPROVED);
            wrap(config).changeStartDate(startDate);

        }

        @Test
        public void at_lease_renewal_a_turnover_reporting_config_is_created_on_the_new_lease() throws Exception {

            // given
            final Lease topmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2);
            occupancy.setReportTurnover(Occupancy.OccupancyReportingType.YES);

            // when
            final LocalDate startDate = new LocalDate(2020, 2, 15);
            final Lease topmodelRenewal = topmodelLease.renew("TOPMODEL2", "new lease",
                    startDate, startDate.plusYears(5).minusDays(1));
            // then
            assertThat(topmodelRenewal.getOccupancies()).hasSize(1);
            final Occupancy newOcc = topmodelRenewal.getOccupancies().first();
            assertThat(newOcc.getReportTurnover()).isEqualTo(
                    Occupancy.OccupancyReportingType.YES);
            assertThat(turnoverReportingConfigRepository.findByOccupancy(newOcc)).hasSize(1);
            final TurnoverReportingConfig newConfig = turnoverReportingConfigRepository.findByOccupancy(newOcc).get(0);
            assertThat(newConfig.getStartDate()).isEqualTo(startDate);
            assertThat(newConfig.getFrequency()).isEqualTo(Frequency.MONTHLY);
            assertThat(newConfig.getCurrency().getReference()).isEqualTo("EUR");
        }

        @Test
        public void at_lease_renewal_NO_turnover_reporting_config_is_created_on_the_new_lease() throws Exception {
            // given
            final Lease topmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2);
            occupancy.setReportTurnover(Occupancy.OccupancyReportingType.NO);

            // when
            final LocalDate startDate = new LocalDate(2020, 2, 15);
            final Lease topmodelRenewal = topmodelLease.renew("TOPMODEL2", "new lease",
                    startDate, startDate.plusYears(5).minusDays(1));
            // then
            assertThat(topmodelRenewal.getOccupancies()).hasSize(1);
            final Occupancy newOcc = topmodelRenewal.getOccupancies().first();
            assertThat(newOcc.getReportTurnover()).isEqualTo(
                    Occupancy.OccupancyReportingType.NO);
            assertThat(turnoverReportingConfigRepository.findByOccupancy(newOcc)).isEmpty();
        }

        @Test
        public void when_changing_report_turnover_flag_a_config_is_created_when_none_is_present() throws Exception {

            // given (data previous test)
            final Lease topmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2);
            occupancy.setReportTurnover(Occupancy.OccupancyReportingType.NO);
            final LocalDate startDate = new LocalDate(2020, 2, 15);
            final Lease topmodelRenewal = topmodelLease.renew("TOPMODEL2", "new lease",
                    startDate, startDate.plusYears(5).minusDays(1));
            assertThat(topmodelRenewal.getOccupancies()).hasSize(1);
            final Occupancy newOcc = topmodelRenewal.getOccupancies().first();
            assertThat(newOcc.getReportTurnover()).isEqualTo(
                    Occupancy.OccupancyReportingType.NO);
            assertThat(turnoverReportingConfigRepository.findByOccupancy(newOcc)).isEmpty();

            // when
            newOcc.changeReportingOptions(Occupancy.OccupancyReportingType.YES, Occupancy.OccupancyReportingType.YES,Occupancy.OccupancyReportingType.YES);

            // then
            assertThat(turnoverReportingConfigRepository.findByOccupancy(newOcc)).hasSize(1);
            final TurnoverReportingConfig config = turnoverReportingConfigRepository.findByOccupancy(newOcc).get(0);
            assertThat(config.getStartDate()).isEqualTo(startDate);
            assertThat(config.getFrequency()).isEqualTo(Frequency.MONTHLY);
            assertThat(config.getCurrency().getReference()).isEqualTo("EUR");

        }

    }


    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject TurnoverRepository turnoverRepository;

    
}