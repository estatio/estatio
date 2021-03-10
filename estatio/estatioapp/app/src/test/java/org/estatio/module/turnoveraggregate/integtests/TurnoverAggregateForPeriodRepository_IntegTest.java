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
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.AggregationPeriod;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregateForPeriod;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregateForPeriodRepository;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregateForPeriodRepository_IntegTest extends TurnoverAggregateModuleIntegTestAbstract {

    @Test
    public void create_works() throws Exception {

        // given
        assertThat(turnoverAggregateForPeriodRepository.listAll()).hasSize(0);
        final AggregationPeriod period = AggregationPeriod.P_1M;

        // when
        final TurnoverAggregateForPeriod aggregate = turnoverAggregateForPeriodRepository
                .create(period);

        // then
        assertThat(turnoverAggregateForPeriodRepository.listAll()).hasSize(1);
        assertThat(aggregate.getAggregationPeriod()).isEqualTo(period);

    }

    @Inject TurnoverAggregateForPeriodRepository turnoverAggregateForPeriodRepository;

}