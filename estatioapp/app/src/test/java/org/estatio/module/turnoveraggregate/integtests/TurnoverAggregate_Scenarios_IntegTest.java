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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.contributions.Occupancy_turnovers;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;
import org.estatio.module.turnoveraggregate.contributions.Lease_aggregateTurnovers;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;
import org.estatio.module.turnoveraggregate.fixtures.TurnoverImportXlsxFixtureForAggregated123;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregate_Scenarios_IntegTest extends TurnoverAggregateModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Currency_enum.EUR.builder());
                executionContext.executeChild(this, Lease_enum.Oxf123.builder());
            }
        });
    }

    LocalDate endDateOcc1;
    Lease oxf123Lease;
    Occupancy occ1;
    TurnoverReportingConfig occ1Cfg;
    Occupancy occ2;
    TurnoverReportingConfig occ2Cfg;
    Property oxf;
    Currency euro;

    void setupScenario_and_validate_import() {

        oxf = Property_enum.OxfGb.findUsing(serviceRegistry2);
        euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        oxf123Lease = Lease_enum.Oxf123.findUsing(serviceRegistry2);
        oxf123Lease.setTenancyStartDate(new LocalDate(2005, 10, 28));
        occ1 = oxf123Lease.getOccupancies().last();
        occ1Cfg = turnoverReportingConfigRepository.findOrCreate(occ1, Type.PRELIMINARY,null, occ1.getStartDate(), Frequency.MONTHLY, euro);
        occ2 = oxf123Lease.getOccupancies().first();
        occ2Cfg = turnoverReportingConfigRepository.findOrCreate(occ2, Type.PRELIMINARY,null, occ2.getStartDate(), Frequency.MONTHLY, euro);

        assertThat(occ1.getUnit().getName()).isEqualTo("Unit 1");
        assertThat(occ2.getUnit().getName()).isEqualTo("Unit 2");

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new TurnoverImportXlsxFixtureForAggregated123());
            }
        });

        final List<Turnover> turnovers = turnoverRepository.listAll();
        assertThat(turnovers).hasSize(119);
        final List<Turnover> turnoversOnOcc1 = mixin(Occupancy_turnovers.class, occ1).turnovers();
        final List<Turnover> turnoversOnOcc2 = mixin(Occupancy_turnovers.class, occ2).turnovers();

        assertThat(turnoversOnOcc1).hasSize(112);
        final LocalDate maxDateOcc1 = turnoversOnOcc1.stream().max(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(maxDateOcc1).isEqualTo(new LocalDate(2019, 4,1));

        assertThat(turnoversOnOcc2).hasSize(7);
        final LocalDate minDateOcc2 = turnoversOnOcc2.stream().min(Comparator.comparing(Turnover::getDate))
                .map(Turnover::getDate).get();
        assertThat(minDateOcc2).isEqualTo(new LocalDate(2019, 5,1));

    }

    @Test
    public void calculate_turnover_aggregations_works() throws Exception {

        // given
        setFixtureClockDate(new LocalDate(2020,2,16));
        setupScenario_and_validate_import();
        assertThat(turnoverAggregationRepository.listAll()).isEmpty();

        // when
        final LocalDate startDate = new LocalDate(2018, 2, 1);
        final LocalDate endDate = new LocalDate(2020, 1, 1);
        mixin(Lease_aggregateTurnovers.class, oxf123Lease).$$(startDate, endDate,false);
        transactionService.nextTransaction();

        // then
        final List<TurnoverAggregation> aggregations = turnoverAggregationRepository.listAll();
        final List<TurnoverAggregation> aggsOcc2 = turnoverAggregationRepository
                .findByTurnoverReportingConfig(occ2Cfg);
        final TurnoverAggregation agg20100101 = turnoverAggregationRepository.findUnique(occ2Cfg, endDate);
        assertThat(agg20100101).isNotNull();

        assertThat(agg20100101.getAggregate1Month().getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("93836.00"));
        assertThat(agg20100101.getAggregate1Month().getTurnoverCountPreviousYear()).isEqualTo(1);
        assertThat(agg20100101.getAggregate1Month().getNetAmountPreviousYear()).isEqualTo(new BigDecimal("0.00"));
        assertThat(agg20100101.getAggregate1Month().getTurnoverCount()).isNull();
        assertThat(agg20100101.getAggregate1Month().getNonComparableThisYear()).isNull();
        assertThat(agg20100101.getAggregate1Month().getNonComparablePreviousYear()).isEqualTo(false);
        assertThat(agg20100101.getAggregate2Month().getGrossAmount()).isNull();
        assertThat(agg20100101.getAggregate2Month().getNonComparableThisYear()).isNull();
        assertThat(agg20100101.getAggregate2Month().getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("176524.00"));
        assertThat(agg20100101.getAggregate2Month().getTurnoverCountPreviousYear()).isEqualTo(2);
        assertThat(agg20100101.getAggregate3Month().getNonComparableThisYear()).isFalse();
        assertThat(agg20100101.getAggregate3Month().getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("276566.00"));
        assertThat(agg20100101.getAggregate3Month().getTurnoverCountPreviousYear()).isEqualTo(3);
        assertThat(agg20100101.getAggregate12Month().getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("900140.00"));
        assertThat(agg20100101.getAggregate12Month().getTurnoverCountPreviousYear()).isEqualTo(12);
        assertThat(agg20100101.getGrossAmount1MCY_2()).isEqualTo(new BigDecimal("65264.00"));

        // when
        final LocalDate startDate1 = new LocalDate(2020, 1, 1);
        mixin(Lease_aggregateTurnovers.class, oxf123Lease).$$(startDate1, startDate1.plusMonths(23), false);
        transactionService.nextTransaction();
        // then still
        final TurnoverAggregation agg20100101v2 = turnoverAggregationRepository.findUnique(occ2Cfg, new LocalDate(2020,1,1));
        assertThat(agg20100101v2.getAggregate1Month().getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("93836.00"));
        assertThat(agg20100101v2.getAggregate12Month().getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("900140.00"));
        assertThat(agg20100101v2.getAggregate12Month().getTurnoverCountPreviousYear()).isEqualTo(12);
        assertThat(agg20100101v2.getGrossAmount1MCY_2()).isEqualTo(new BigDecimal("65264.00"));

        // TODO: finish

    }

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject TurnoverRepository turnoverRepository;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;
}