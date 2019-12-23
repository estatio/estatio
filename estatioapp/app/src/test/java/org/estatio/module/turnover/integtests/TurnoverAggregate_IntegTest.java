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
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitType;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.app.TurnoverMenu;
import org.estatio.module.turnover.contributions.Occupancy_createTurnoverReportingConfig;
import org.estatio.module.turnover.contributions.Occupancy_turnovers;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.TurnoverImportXlsxFixture;
import org.estatio.module.turnover.fixtures.TurnoverImportXlsxFixtureForAggregated;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregate_IntegTest extends TurnoverModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Currency_enum.EUR.builder());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim);
            }
        });
    }

    @Test
    public void import_scenario_1_succeeded() throws Exception {

        // given
        // occ1 ends on 2017,4,16
        // occ2 starts the day after on 2017,4,17
        // turnovers 2019,5,1 and later are on occ2
        // occ3 runs parallel to occ2 and has turnovers on it as well starting 2019,4,1
        // lease 2 and occ4 start on 2019,11,10
        // lease 3 and occ5 start on 2020,5,20
        final LocalDate endDateOcc1 = new LocalDate(2017, 4, 16);
        final Lease oxfTopModelLease1 = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2);
        final Occupancy occ1 = oxfTopModelLease1.getOccupancies().first();
        occ1.terminate(endDateOcc1);
        final Property oxf = Property_enum.OxfGb.findUsing(serviceRegistry2);

        final Unit new_unit_for_topmodel = oxf.newUnit("OXF-001A", "New unit 1 for Topmodel", UnitType.BOUTIQUE);
        final Occupancy occ2 = oxfTopModelLease1.newOccupancy(endDateOcc1.plusDays(1), new_unit_for_topmodel);
        mixin(Occupancy_createTurnoverReportingConfig.class,occ2).createTurnoverReportingConfig(Type.PRELIMINARY, occ2.getStartDate(), Frequency.MONTHLY, Currency_enum.EUR.findUsing(serviceRegistry2));

        final Unit par_unit_for_topmodel = oxf.newUnit("OXF-001A-PAR", "Parallel unit 1 for Topmodel", UnitType.BOUTIQUE);
        final Occupancy occ3 = oxfTopModelLease1.newOccupancy(endDateOcc1.plusDays(1), par_unit_for_topmodel);
        mixin(Occupancy_createTurnoverReportingConfig.class,occ3).createTurnoverReportingConfig(Type.PRELIMINARY, occ3.getStartDate(), Frequency.MONTHLY, Currency_enum.EUR.findUsing(serviceRegistry2));

        final LocalDate startDateLease2 = new LocalDate(2019, 11, 10);
        final LocalDate startDateLease3 = new LocalDate(2020, 5, 20);

        final Lease oxfTopModelLease2 = wrap(oxfTopModelLease1).renew("OXF-TOPMODEL-2", "Lease 2",
                startDateLease2, startDateLease3.minusDays(1));
        final Occupancy occ4 = oxfTopModelLease2.getOccupancies().first();
        mixin(Occupancy_createTurnoverReportingConfig.class,occ4).createTurnoverReportingConfig(Type.PRELIMINARY, occ4.getStartDate(), Frequency.MONTHLY, Currency_enum.EUR.findUsing(serviceRegistry2));

        final Lease oxfTopModelLease3 = wrap(oxfTopModelLease2).renew("OXF-TOPMODEL-3", "Lease 3",
                startDateLease3, startDateLease3.plusYears(1));
        final Occupancy occ5 = oxfTopModelLease3.getOccupancies().first();
        mixin(Occupancy_createTurnoverReportingConfig.class,occ5).createTurnoverReportingConfig(Type.PRELIMINARY, occ5.getStartDate(), Frequency.MONTHLY, Currency_enum.EUR.findUsing(serviceRegistry2));

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new TurnoverImportXlsxFixtureForAggregated());
            }
        });

        final List<Turnover> turnovers = turnoverRepository.listAll();
        assertThat(turnovers).hasSize(76);
        final List<Turnover> turnoversOnOcc1 = mixin(Occupancy_turnovers.class, occ1).turnovers();
        final List<Turnover> turnoversOnOcc2 = mixin(Occupancy_turnovers.class, occ2).turnovers();
        final List<Turnover> turnoversOnOcc3 = mixin(Occupancy_turnovers.class, occ3).turnovers();
        final List<Turnover> turnoversOnOcc4 = mixin(Occupancy_turnovers.class, occ4).turnovers();
        final List<Turnover> turnoversOnOcc5 = mixin(Occupancy_turnovers.class, occ5).turnovers();
        assertThat(turnoversOnOcc1).hasSize(52);
        final LocalDate maxDateOcc1 = turnoversOnOcc1.stream().max(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(maxDateOcc1).isEqualTo(new LocalDate(2019, 4,1));

        assertThat(turnoversOnOcc2).hasSize(7);
        final LocalDate minDateOcc2 = turnoversOnOcc2.stream().min(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(minDateOcc2).isEqualTo(new LocalDate(2019, 5,1));

        assertThat(turnoversOnOcc3).hasSize(8);
        final LocalDate minDateOcc3 = turnoversOnOcc3.stream().min(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(minDateOcc3).isEqualTo(new LocalDate(2019, 4,1));

        assertThat(turnoversOnOcc4).hasSize(7);
        final LocalDate minDateOcc4 = turnoversOnOcc4.stream().min(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(minDateOcc4).isEqualTo(new LocalDate(2019, 11,1));

        assertThat(turnoversOnOcc5).hasSize(2);
        final LocalDate minDateOcc5 = turnoversOnOcc5.stream().min(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(minDateOcc5).isEqualTo(new LocalDate(2020, 5,1));

        // when (aggregate)

        // then

    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}