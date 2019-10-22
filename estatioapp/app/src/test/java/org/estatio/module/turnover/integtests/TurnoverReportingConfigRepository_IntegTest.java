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
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverReportingConfigRepository_IntegTest extends TurnoverModuleIntegTestAbstract {

    public static class FindOrCreate extends TurnoverReportingConfigRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Currency_enum.EUR.builder());
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }


        @Test
        public void find_or_create_works() throws Exception {

            // given
            final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
            final LocalDate startDate = new LocalDate(2019, 1, 1);
            final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
            // when
            TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.PRELIMINARY, null, startDate, Frequency.MONTHLY, euro);
            // then
            assertThat(turnoverReportingConfigRepository.findOrCreate(occupancy, Type.PRELIMINARY, null, new LocalDate(2020, 12, 31), Frequency.DAILY, euro)).isSameAs(config);
            assertThat(config.getFrequency()).isEqualTo(Frequency.MONTHLY);

        }

    }

    public static class FindByReporter extends TurnoverReportingConfigRepository_IntegTest {


        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim.builder());
                }
            });
        }

        @Test
        public void find_by_reporter_works() throws Exception {

            // given
            assertThat(turnoverReportingConfigRepository.listAll()).hasSize(1);
            final Person personGino = TurnoverReportingConfig_enum.OxfTopModel001GbPrelim.getPerson_d().findUsing(serviceRegistry2);
            assertThat(turnoverReportingConfigRepository.listAll().get(0).getReporter()).isEqualTo(personGino);

            // when, then
            assertThat(turnoverReportingConfigRepository.findByReporter(personGino)).hasSize(1);
            assertThat(turnoverReportingConfigRepository.findByReporter(null)).isEmpty();


        }


    }

    public static class FindByPropertyActiveOnDate extends TurnoverReportingConfigRepository_IntegTest {


        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim.builder());
                }
            });
        }

        @Test
        public void find_by_property_active_on_date_works() throws Exception {

            // given
            TurnoverReportingConfig config = turnoverReportingConfigRepository.listAll().get(0);
            final LocalDate occupancyStartDate = new LocalDate(2010, 7, 15);
            assertThat(config.getOccupancy().getStartDate()).isEqualTo(occupancyStartDate);
            assertThat(config.getStartDate()).isEqualTo(new LocalDate(2014,1,2));

            // when, then
            final LocalDate firstDayOfOccupancyMonth = new LocalDate(2010, 7, 1);
            assertThat(turnoverReportingConfigRepository.findByPropertyActiveOnDate(Property_enum.OxfGb.findUsing(serviceRegistry2), firstDayOfOccupancyMonth)).hasSize(0);

            // and when
            config.setStartDate(occupancyStartDate);
            // then
            assertThat(turnoverReportingConfigRepository.findByPropertyActiveOnDate(Property_enum.OxfGb.findUsing(serviceRegistry2), firstDayOfOccupancyMonth)).hasSize(1);
            final LocalDate lastDayOfOccupancyMonth = new LocalDate(2010, 7, 31);
            assertThat(turnoverReportingConfigRepository.findByPropertyActiveOnDate(Property_enum.OxfGb.findUsing(serviceRegistry2), lastDayOfOccupancyMonth)).hasSize(1);

            // and when
            final LocalDate lastDayOfPreviousOccupancyMonth = firstDayOfOccupancyMonth.minusDays(1);
            // then
            assertThat(turnoverReportingConfigRepository.findByPropertyActiveOnDate(Property_enum.OxfGb.findUsing(serviceRegistry2), lastDayOfPreviousOccupancyMonth)).hasSize(0);

            // and when occupancy ends before next month
            config.getOccupancy().setEndDate(lastDayOfOccupancyMonth);
            final LocalDate firstDayOfNextOccupancyMonth = new LocalDate(2010, 8, 1);
            assertThat(turnoverReportingConfigRepository.findByPropertyActiveOnDate(Property_enum.OxfGb.findUsing(serviceRegistry2), firstDayOfNextOccupancyMonth)).hasSize(0);

        }


    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}