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

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;

public class TurnoverReportingConfigRepository_IntegTest extends TurnoverModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void find_or_create_works() throws Exception {

        // given
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDate startDate = new LocalDate(2019, 1, 1);
        // when
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, null, startDate, Frequency.MONTHLY, Frequency.YEARLY);
        // then
        Assertions.assertThat(turnoverReportingConfigRepository.findOrCreate(occupancy, null, new LocalDate(2020,12,31), Frequency.DAILY, Frequency.DAILY)).isSameAs(config);
        Assertions.assertThat(config.getPrelimFrequency()).isEqualTo(Frequency.MONTHLY);

    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}