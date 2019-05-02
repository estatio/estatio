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

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}