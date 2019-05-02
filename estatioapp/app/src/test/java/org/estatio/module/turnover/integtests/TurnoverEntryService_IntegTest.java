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

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.dom.entry.TurnoverEntryService;
import org.estatio.module.turnover.dom.entry.Turnover_enter;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverEntryService_IntegTest extends TurnoverModuleIntegTestAbstract {

    public static class ProduceEmptyTurnovers extends TurnoverEntryService_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbAudit);
                }
            });
        }

        @Test
        public void produce_empty_turnovers_works() throws Exception {
            // given
            final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository.listAll();
            assertThat(configs).hasSize(2);
            LocalDate startConfig = new LocalDate(2014, 1, 2);
            assertThat(configs.get(0).getStartDate()).isEqualTo(startConfig);
            assertThat(configs.get(1).getStartDate()).isEqualTo(startConfig);
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

    }

    public static class NextNewForReporter extends TurnoverEntryService_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this,Person_enum.JohnTurnover);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbAudit);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.BudPoison001NlPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.BudPoison001NlAudit);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.BudMiracle002NlPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.BudMiracle002NlAudit);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.BudDago004NlPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.BudDago004NlAudit);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfMiracl005GbPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfMiracl005GbAudit);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfMediaX002GbPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfMediaX002GbAudit);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfPoison003GbPrelim);
                    executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfPoison003GbAudit);

                }
            });
        }

        @Test
        public void next_new_for_reporter_works() throws Exception {

            // given
            final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository.listAll();
            assertThat(configs).hasSize(14);
            final LocalDate jan = new LocalDate(2014, 01, 01);
            turnoverEntryService.produceEmptyTurnoversFor(jan);
            final LocalDate feb = new LocalDate(2014, 02, 01);
            turnoverEntryService.produceEmptyTurnoversFor(feb);
            final LocalDate march = new LocalDate(2014, 03, 01);
            turnoverEntryService.produceEmptyTurnoversFor(march);
            List<Turnover> turnovers = turnoverRepository.listAll();
            assertThat(turnovers).hasSize(26); // no turnovers for topmodel on 01-01-2014

            // when
            Person reporterJohn = Person_enum.JohnTurnover.findUsing(serviceRegistry);
            assertThat(turnoverReportingConfigRepository.findByReporter(reporterJohn)).hasSize(12);
            Turnover turnover = turnoverRepository.findByConfigAndTypeAndDateWithStatusNew(TurnoverReportingConfig_enum.BudPoison001NlPrelim.findUsing(serviceRegistry), Type.PRELIMINARY, march).get(0);
            mixin(Turnover_enter.class, turnover).$$(BigDecimal.ZERO, null, null, false, null);

            // then
            Turnover nextNew = turnoverEntryService.nextNewForReporter(reporterJohn, turnover);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-DAGO");
            assertThat(nextNew.getDate()).isEqualTo(march);
            // and when
            nextNew = turnoverEntryService.nextNewForReporter(reporterJohn, nextNew);
            // then still
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-DAGO");
            assertThat(nextNew.getDate()).isEqualTo(march);

            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(march);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);

            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MEDIAX");
            assertThat(nextNew.getDate()).isEqualTo(march);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(march);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-POI");
            assertThat(nextNew.getDate()).isEqualTo(march);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);

            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-DAGO");
            assertThat(nextNew.getDate()).isEqualTo(feb);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(feb);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-POI");
            assertThat(nextNew.getDate()).isEqualTo(feb);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);

            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MEDIAX");
            assertThat(nextNew.getDate()).isEqualTo(feb);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(feb);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-POI");
            assertThat(nextNew.getDate()).isEqualTo(feb);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);

            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-DAGO");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-POI");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);

            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MEDIAX");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);
            // and when, then
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-POI");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.PRELIMINARY);

            // and finally the other types also in alphabetical order
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-DAGO");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.AUDITED);

            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.AUDITED);

            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("BUD-POI");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.AUDITED);

            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MEDIAX");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.AUDITED);

            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-MIRA");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.AUDITED);

            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew.getConfig().getOccupancy().getLease().getReference()).startsWith("OXF-POI");
            assertThat(nextNew.getDate()).isEqualTo(jan);
            assertThat(nextNew.getType()).isEqualTo(Type.AUDITED);

            // and that is it
            nextNew = nextAfterDataEntry(nextNew, reporterJohn);
            assertThat(nextNew).isNull();

        }

        private Turnover nextAfterDataEntry(final Turnover turnover, final Person reporter){
            mixin(Turnover_enter.class, turnover).$$(BigDecimal.ZERO, null, null, false, null);
            return turnoverEntryService.nextNewForReporter(reporter, turnover);
        }


    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject TurnoverEntryService turnoverEntryService;

    @Inject TurnoverRepository turnoverRepository;

}