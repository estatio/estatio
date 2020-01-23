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
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;
import org.estatio.module.turnover.integtests.TurnoverModuleIntegTestAbstract;
import org.estatio.module.turnoveraggregate.dom.AggregationPeriod;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregateForPeriod;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregateForPeriodRepository;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregationRepository_IntegTest extends TurnoverAggregateModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Currency_enum.EUR.builder());
                executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim.builder());
            }
        });
    }

    @Test
    public void find_or_create_works() throws Exception {

        // given
        final TurnoverReportingConfig config = TurnoverReportingConfig_enum.OxfTopModel001GbPrelim
                .findUsing(serviceRegistry2);
        final LocalDate date = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);

        // when
        TurnoverAggregation aggregation = turnoverAggregationRepository
                .findOrCreate(config, date, euro);

        // then
        assertThat(turnoverAggregationRepository.listAll()).hasSize(1);
        assertThat(aggregation.getTurnoverReportingConfig()).isEqualTo(config);
        assertThat(aggregation.getDate()).isEqualTo(date);
        assertThat(aggregation.getCurrency()).isEqualTo(euro);

        // and when (again)
        TurnoverAggregation aggregation2 = turnoverAggregationRepository
                .findOrCreate(config, date, euro);

        // then still
        assertThat(turnoverAggregationRepository.listAll()).hasSize(1);
        assertThat(aggregation2).isEqualTo(aggregation);

        // and when
        TurnoverAggregation aggregation3 = turnoverAggregationRepository
                .findOrCreate(config, date.plusMonths(1), euro);

        // then
        assertThat(turnoverAggregationRepository.listAll()).hasSize(2);
        assertThat(aggregation3).isNotEqualTo(aggregation);
    }

    @Test
    public void findByTurnoverReportingConfig_works() throws Exception {

        final TurnoverReportingConfig config = TurnoverReportingConfig_enum.OxfTopModel001GbPrelim
                .findUsing(serviceRegistry2);
        final LocalDate date = new LocalDate(2019, 1, 1);
        final LocalDate date2 = new LocalDate(2019, 2, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);

        // when
        turnoverAggregationRepository
                .findOrCreate(config, date, euro);
        turnoverAggregationRepository
                .findOrCreate(config, date2, euro);

        // then
        assertThat(turnoverAggregationRepository.findByTurnoverReportingConfig(config)).hasSize(2);

    }

    @Test
    public void removeWorks() throws Exception {

        final TurnoverReportingConfig config = TurnoverReportingConfig_enum.OxfTopModel001GbPrelim
                .findUsing(serviceRegistry2);
        final LocalDate date = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);

        final TurnoverAggregation agg = turnoverAggregationRepository
                .findOrCreate(config, date, euro);
        transactionService.nextTransaction();
        assertThat(turnoverAggregationRepository.listAll()).hasSize(1);
        assertThat(turnoverAggregateForPeriodRepository.listAll()).hasSize(6);

        // when
        agg.remove();

        // then
        assertThat(turnoverAggregationRepository.listAll()).hasSize(0);
        assertThat(turnoverAggregateForPeriodRepository.listAll()).hasSize(0);


    }

    @Inject TurnoverAggregateForPeriodRepository turnoverAggregateForPeriodRepository;

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}