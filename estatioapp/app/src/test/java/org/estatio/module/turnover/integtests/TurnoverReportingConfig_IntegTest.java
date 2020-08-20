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

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.dom.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

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

    }


    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject TurnoverRepository turnoverRepository;

    
}