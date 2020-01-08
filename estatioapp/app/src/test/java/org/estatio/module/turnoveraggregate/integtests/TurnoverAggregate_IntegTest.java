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
package org.estatio.module.turnoveraggregate.integtests;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitType;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.contributions.Occupancy_createTurnoverReportingConfig;
import org.estatio.module.turnover.contributions.Occupancy_turnovers;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.integtests.TurnoverModuleIntegTestAbstract;
import org.estatio.module.turnoveraggregate.contributions.Occupancy_aggregateTurnovers;
import org.estatio.module.turnoveraggregate.contributions.TurnoverReportingConfig_aggregateTurnovers;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;
import org.estatio.module.turnoveraggregate.fixtures.TurnoverImportXlsxFixtureForAggregated;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregate_IntegTest extends TurnoverAggregateModuleIntegTestAbstract {

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

    LocalDate endDateOcc1;
    LocalDate startDateLease2;
    LocalDate startDateLease3;
    Lease oxfTopModelLease1;
    Lease oxfTopModelLease2;
    Lease oxfTopModelLease3;
    Occupancy occ1;
    Occupancy occ2;
    Occupancy occ3;
    Occupancy occ4;
    Occupancy occ5;
    Property oxf;
    Currency euro;
    Unit new_unit_for_topmodel;
    Unit par_unit_for_topmodel;

    void setupScenario_and_validate_import() {

        // given
        // occ1 ends on 2017,4,16
        // occ2 starts the day after on 2017,4,17
        // turnovers 2019,5,1 and later are on occ2
        // occ3 runs parallel to occ2 and has turnovers on it as well starting 2019,4,1
        // lease 2 and occ4 start on 2019,11,10
        // lease 3 and occ5 start on 2020,5,20
        endDateOcc1 = new LocalDate(2017, 4, 16);
        oxfTopModelLease1 = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2);
        occ1 = oxfTopModelLease1.getOccupancies().first();
        occ1.terminate(endDateOcc1);
        oxf = Property_enum.OxfGb.findUsing(serviceRegistry2);
        euro = Currency_enum.EUR.findUsing(serviceRegistry2);

        new_unit_for_topmodel = oxf.newUnit("OXF-001A", "New unit 1 for Topmodel", UnitType.BOUTIQUE);
        occ2 = oxfTopModelLease1.newOccupancy(endDateOcc1.plusDays(1), new_unit_for_topmodel);
        mixin(Occupancy_createTurnoverReportingConfig.class,occ2).createTurnoverReportingConfig(Type.PRELIMINARY, occ2.getStartDate(), Frequency.MONTHLY,
                euro);

        par_unit_for_topmodel = oxf.newUnit("OXF-001A-PAR", "Parallel unit 1 for Topmodel", UnitType.BOUTIQUE);
        occ3 = oxfTopModelLease1.newOccupancy(endDateOcc1.plusDays(1), par_unit_for_topmodel);
        mixin(Occupancy_createTurnoverReportingConfig.class,occ3).createTurnoverReportingConfig(Type.PRELIMINARY, occ3.getStartDate(), Frequency.MONTHLY,
                euro);

        startDateLease2 = new LocalDate(2019, 11, 10);
        startDateLease3 = new LocalDate(2020, 5, 20);

        oxfTopModelLease2 = wrap(oxfTopModelLease1).renew("OXF-TOPMODEL-2", "Lease 2",
                startDateLease2, startDateLease3.minusDays(1));
        occ4 = oxfTopModelLease2.getOccupancies().first();
        mixin(Occupancy_createTurnoverReportingConfig.class,occ4).createTurnoverReportingConfig(Type.PRELIMINARY, occ4.getStartDate(), Frequency.MONTHLY,
                euro);

        oxfTopModelLease3 = wrap(oxfTopModelLease2).renew("OXF-TOPMODEL-3", "Lease 3",
                startDateLease3, startDateLease3.plusYears(1));
        occ5 = oxfTopModelLease3.getOccupancies().first();
        mixin(Occupancy_createTurnoverReportingConfig.class,occ5).createTurnoverReportingConfig(Type.PRELIMINARY, occ5.getStartDate(), Frequency.MONTHLY,
                euro);

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

    }

    @Test
    public void import_and_simple_scenario_succeeded() throws Exception {

        // given
        setupScenario_and_validate_import();

        // when (aggregate)
        final TurnoverAggregation aggOnOcc1 = turnoverAggregationRepository
                .findOrCreate(occ1, new LocalDate(2019, 4, 1), Type.PRELIMINARY, Frequency.MONTHLY, euro);
        aggOnOcc1.aggregate();

        // then
        assertTurnoverAggregation(
                aggOnOcc1,
                new BigDecimal("52775.00"), new BigDecimal("68878.00"), true,
                new BigDecimal("110971.00"), new BigDecimal("136651.00"), true,
                new BigDecimal("177935.00"), new BigDecimal("197351.00"), true,
                new BigDecimal("454501.00"), new BigDecimal("469051.00"), true,
                new BigDecimal("657250.00"), new BigDecimal("689764.00"), true,
                new BigDecimal("880724.00"), new BigDecimal("903262.00"), true,
                new BigDecimal("271771.00"), new BigDecimal("288759.00"), true,
                new BigInteger("0"), new BigInteger("0"), true,
                new BigInteger("1130"), new BigInteger("532"), true,
                new BigInteger("3590"), new BigInteger("2918"), true,
                new BigInteger("7463"), new BigInteger("6338"), true,
                "xxxyyy", "zzz",
                new BigDecimal("58196.00"), new BigDecimal("66964.00")
                );
    }

    @Test
    public void scenario_parallel_occupancy() throws Exception {

        // given
        setupScenario_and_validate_import();
        Occupancy occPar = occ3;

        // when
        final TurnoverAggregation aggOccPar = turnoverAggregationRepository
                .findOrCreate(occPar, new LocalDate(2019, 4, 1), Type.PRELIMINARY, Frequency.MONTHLY, euro);
        aggOccPar.aggregate();

        // then
        assertTurnoverAggregation(
                aggOccPar,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigDecimal("12342.00"), BigDecimal.ZERO, false,
                new BigInteger("120"), new BigInteger("0"), false,
                new BigInteger("120"), new BigInteger("0"), false,
                new BigInteger("120"), new BigInteger("0"), false,
                new BigInteger("120"), new BigInteger("0"), false,
                "abc", "",
                BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    @Test
    public void scenario_parent_leases() throws Exception {

        // given
        setupScenario_and_validate_import();

        // when
        final TurnoverAggregation aggOcc4 = turnoverAggregationRepository
                .findOrCreate(occ4, new LocalDate(2019, 11, 1), Type.PRELIMINARY, Frequency.MONTHLY, euro);
        aggOcc4.aggregate();

        // then
        assertTurnoverAggregation(
                aggOcc4,
                new BigDecimal("72053.00"), BigDecimal.ZERO, false,  // 65264 + 6789
                new BigDecimal("148282.00"), BigDecimal.ZERO, false,
                new BigDecimal("210282.00"), BigDecimal.ZERO, false,
                new BigDecimal("376212.00"), BigDecimal.ZERO, false,
                new BigDecimal("446235.00"), BigDecimal.ZERO, false,
                new BigDecimal("446235.00"), BigDecimal.ZERO, false,
                new BigDecimal("446235.00"), BigDecimal.ZERO, false,
                new BigInteger("567"), new BigInteger("0"), false,
                new BigInteger("1129"), new BigInteger("0"), false,
                new BigInteger("1482"), new BigInteger("0"), false,
                new BigInteger("1482"), new BigInteger("0"), false,
                "", "",
                new BigDecimal("76229.00"), new BigDecimal("62000.00")
        );

        // and when 2 parent leases
        final TurnoverAggregation aggOcc5 = turnoverAggregationRepository
                .findOrCreate(occ5, new LocalDate(2020, 6, 1), Type.PRELIMINARY, Frequency.MONTHLY, euro);
        aggOcc5.aggregate();

        // then
        assertTurnoverAggregation(
                aggOcc5,
                new BigDecimal("12345.00"), new BigDecimal("76342.00"), true,
                new BigDecimal("25905.00"), new BigDecimal("146365.00"), true,
                new BigDecimal("38253.00"), new BigDecimal("146365.00"), false,
                new BigDecimal("75381.00"), new BigDecimal("146365.00"), false,
                new BigDecimal("236007.00"), new BigDecimal("146365.00"), false,
                new BigDecimal("387595.00"), new BigDecimal("146365.00"), false,
                new BigDecimal("75381.00"), new BigDecimal("146365.00"), false,
                new BigInteger("0"), new BigInteger("0"), true,
                new BigInteger("0"), new BigInteger("0"), false,
                new BigInteger("0"), new BigInteger("0"), false,
                new BigInteger("1605"), new BigInteger("0"), false,
                "", "",
                new BigDecimal("13560.00"), new BigDecimal("12348.00")
        );

    }

    private void assertTurnoverAggregation(final TurnoverAggregation aggOnOcc1,
            final BigDecimal A1mGross,
            final BigDecimal A1mGPY,
            final boolean A1mC,
            final BigDecimal A2mGross,
            final BigDecimal A2mGPY,
            final boolean A2mC,
            final BigDecimal A3mGross,
            final BigDecimal A3mGPY,
            final boolean A3mC,
            final BigDecimal A6mGross,
            final BigDecimal A6mGPY,
            final boolean A6mC,
            final BigDecimal A9mGross,
            final BigDecimal A9mGPY,
            final boolean A9mC,
            final BigDecimal A12mGross,
            final BigDecimal A12mGPY,
            final boolean A12mC,
            final BigDecimal ATDGross,
            final BigDecimal ATDGPY,
            final boolean ATDC,
            final BigInteger PC1m,
            final BigInteger PC1mPY,
            final boolean PC1mC,
            final BigInteger PC3m,
            final BigInteger PC3mPY,
            final boolean PC3mC,
            final BigInteger PC6m,
            final BigInteger PC6mPY,
            final boolean PC6mC,
            final BigInteger PC12m,
            final BigInteger PC12mPY,
            final boolean PC12mC,
            final String comments12m,
            final String comments12MPY,
            final BigDecimal Gross1m_1,
            final BigDecimal Gross1m_2
    ) {
        Assertions.assertThat(aggOnOcc1.getAggregate1Month().getGrossAmount()).isEqualTo(A1mGross);
        Assertions.assertThat(aggOnOcc1.getAggregate1Month().getGrossAmountPreviousYear()).isEqualTo(A1mGPY);
        Assertions.assertThat(aggOnOcc1.getAggregate1Month().isComparable()).isEqualTo(A1mC);
        Assertions.assertThat(aggOnOcc1.getAggregate2Month().getGrossAmount()).isEqualTo(A2mGross);
        Assertions.assertThat(aggOnOcc1.getAggregate2Month().getGrossAmountPreviousYear()).isEqualTo(A2mGPY);
        Assertions.assertThat(aggOnOcc1.getAggregate2Month().isComparable()).isEqualTo(A2mC);
        Assertions.assertThat(aggOnOcc1.getAggregate3Month().getGrossAmount()).isEqualTo(A3mGross);
        Assertions.assertThat(aggOnOcc1.getAggregate3Month().getGrossAmountPreviousYear()).isEqualTo(A3mGPY);
        Assertions.assertThat(aggOnOcc1.getAggregate3Month().isComparable()).isEqualTo(A3mC);
        Assertions.assertThat(aggOnOcc1.getAggregate6Month().getGrossAmount()).isEqualTo(A6mGross);
        Assertions.assertThat(aggOnOcc1.getAggregate6Month().getGrossAmountPreviousYear()).isEqualTo(A6mGPY);
        Assertions.assertThat(aggOnOcc1.getAggregate6Month().isComparable()).isEqualTo(A6mC);
        Assertions.assertThat(aggOnOcc1.getAggregate9Month().getGrossAmount()).isEqualTo(A9mGross);
        Assertions.assertThat(aggOnOcc1.getAggregate9Month().getGrossAmountPreviousYear()).isEqualTo(A9mGPY);
        Assertions.assertThat(aggOnOcc1.getAggregate9Month().isComparable()).isEqualTo(A9mC);
        Assertions.assertThat(aggOnOcc1.getAggregate12Month().getGrossAmount()).isEqualTo(A12mGross);
        Assertions.assertThat(aggOnOcc1.getAggregate12Month().getGrossAmountPreviousYear()).isEqualTo(A12mGPY);
        Assertions.assertThat(aggOnOcc1.getAggregate12Month().isComparable()).isEqualTo(A12mC);
        Assertions.assertThat(aggOnOcc1.getAggregateToDate().getGrossAmount()).isEqualTo(ATDGross);
        Assertions.assertThat(aggOnOcc1.getAggregateToDate().getGrossAmountPreviousYear()).isEqualTo(ATDGPY);
        Assertions.assertThat(aggOnOcc1.getAggregateToDate().isComparable()).isEqualTo(ATDC);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate1Month().getCount()).isEqualTo(PC1m);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate1Month().getCountPreviousYear()).isEqualTo(PC1mPY);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate1Month().isComparable()).isEqualTo(PC1mC);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate3Month().getCount()).isEqualTo(PC3m);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate3Month().getCountPreviousYear()).isEqualTo(PC3mPY);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate3Month().isComparable()).isEqualTo(PC3mC);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate6Month().getCount()).isEqualTo(PC6m);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate6Month().getCountPreviousYear()).isEqualTo(PC6mPY);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate6Month().isComparable()).isEqualTo(PC6mC);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate12Month().getCount()).isEqualTo(PC12m);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate12Month().getCountPreviousYear()).isEqualTo(PC12mPY);
        Assertions.assertThat(aggOnOcc1.getPurchaseCountAggregate12Month().isComparable()).isEqualTo(PC12mC);
        Assertions.assertThat(aggOnOcc1.getComments12MCY()).isEqualTo(comments12m);
        Assertions.assertThat(aggOnOcc1.getComments12MPY()).isEqualTo(comments12MPY);
        Assertions.assertThat(aggOnOcc1.getGrossAmount1MCY_1()).isEqualTo(Gross1m_1);
        Assertions.assertThat(aggOnOcc1.getGrossAmount1MCY_2()).isEqualTo(Gross1m_2);
    }

    @Test
    public void aggregate_for_turnover_config_works() throws Exception {

        // given
        setupScenario_and_validate_import();

        TurnoverReportingConfig configForOcc1 = turnoverReportingConfigRepository
                .findByOccupancyAndTypeAndFrequency(occ1, Type.PRELIMINARY, Frequency.MONTHLY).get(0);
        Assertions.assertThat(turnoverAggregationRepository.listAll()).isEmpty();

        // when
        mixin(TurnoverReportingConfig_aggregateTurnovers.class, configForOcc1).$$();

        // then
        final List<TurnoverAggregation> aggregations = turnoverAggregationRepository.listAll();
        Assertions.assertThat(aggregations).hasSize(71);
        Assertions.assertThat(aggregations.get(0).getDate()).isEqualTo(configForOcc1.getEffectiveStartDate().withDayOfMonth(1));
        Assertions.assertThat(aggregations.get(70).getDate()).isEqualTo(occ1.getEffectiveEndDate().withDayOfMonth(1));

    }

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}