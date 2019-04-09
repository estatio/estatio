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

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverRepository_IntegTest extends TurnoverModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(FixtureScript.ExecutionContext executionContext) {
                executionContext.executeChild(this, Currency_enum.EUR.builder());
                executionContext.executeChild(this, Currency_enum.SEK.builder());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void find_or_create_works() throws Exception {

        // given
        // occupancy - reportedAt is unique
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDateTime reportedAt = new LocalDateTime(2019, 1, 1, 12, 0);

        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        final BigDecimal originalGrossAmount = new BigDecimal("1234.56");
        Turnover turnover = turnoverRepository.create(occupancy, turnoverDate, Type.AUDITED, reportedAt, "someone", euro, null, originalGrossAmount, null, null, false);

        // when
        final Currency sek = Currency_enum.SEK.findUsing(serviceRegistry2);
        Turnover turnover2 = turnoverRepository.findOrCreate(occupancy, turnoverDate, Type.AUDITED, reportedAt, "someone", sek, new BigDecimal("4321.00"), null, null, null, false);

        // then
        assertThat(turnover).isSameAs(turnover2);
        assertThat(turnover2.getTurnoverGrossAmount()).isEqualTo(originalGrossAmount);
        assertThat(turnover2.getCurrency()).isEqualTo(euro);

    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}