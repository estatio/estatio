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

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.dom.entry.TurnoverEntryService;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverEntryService_IntegTest extends TurnoverModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void produce_empty_turnovers_works() throws Exception {
        // given
        final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository.listAll();
        assertThat(configs).hasSize(1);
        LocalDate startConfig = new LocalDate(2014,1,2);
        assertThat(configs.get(0).getStartDate()).isEqualTo(startConfig);
        List<Turnover> turnovers = turnoverRepository.listAll();
        assertThat(turnovers).hasSize(0);

        // when
        turnoverEntryService.produceEmptyTurnoversFor(new LocalDate(2014, 1, 1));
        // then
        assertThat(turnovers).isEmpty();

        // and when
        turnoverEntryService.produceEmptyTurnoversFor(new LocalDate(2014, 2, 1));
        // then
        turnovers = turnoverRepository.listAll();
        assertThat(turnovers).hasSize(1);
        assertThat(turnovers.get(0).getType()).isEqualTo(Type.PRELIMINARY);
        assertThat(turnovers.get(0).getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(turnovers.get(0).getStatus()).isEqualTo(Status.NEW);

        // and when
        final LocalDate start2015 = new LocalDate(2015, 1, 1);
        turnoverEntryService.produceEmptyTurnoversFor(start2015);

        // then
        turnovers = turnoverRepository.listAll();
        assertThat(turnovers).hasSize(3);
        assertThat(turnovers.get(2).getType()).isEqualTo(Type.AUDITED);
        assertThat(turnovers.get(2).getFrequency()).isEqualTo(Frequency.YEARLY);
    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject TurnoverEntryService turnoverEntryService;

    @Inject TurnoverRepository turnoverRepository;

}