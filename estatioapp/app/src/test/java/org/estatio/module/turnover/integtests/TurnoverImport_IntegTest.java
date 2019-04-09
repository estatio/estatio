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
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.TurnoverImportXlsxFixture;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverImport_IntegTest extends TurnoverModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Currency_enum.EUR.builder());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, new TurnoverImportXlsxFixture());
            }
        });
    }

    @Test
    public void import_succeeded() throws Exception {

        final List<Turnover> turnovers = turnoverRepository.listAll();
        assertThat(turnovers).hasSize(2);
        final Turnover first = turnovers.get(0);
        assertThat(first.getOccupancy().getLease()).isEqualTo(Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2));
        assertThat(first.getOccupancy().getUnit().getReference()).isEqualTo("OXF-001");
        assertThat(first.getDate()).isEqualTo(new LocalDate(2019,1,1));
        assertThat(first.getType()).isEqualTo(Type.PRELIMINARY);
        assertThat(first.getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(first.getReportedAt().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(first.getReportedBy()).isEqualTo("tester");
        assertThat(first.getCurrency()).isEqualTo(Currency_enum.EUR.findUsing(serviceRegistry2));
        assertThat(first.getNetAmount()).isEqualTo(new BigDecimal("12345.56"));
        assertThat(first.getGrossAmount()).isEqualTo(new BigDecimal("14814.67"));
        assertThat(first.getPurchaseCount()).isEqualTo(BigInteger.valueOf(123));
        assertThat(first.getComments()).isNull();
        assertThat(first.isNonComparable()).isFalse();

        final Turnover second = turnovers.get(1);
        assertThat(second.getOccupancy().getLease()).isEqualTo(Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2));
        assertThat(second.getOccupancy().getUnit().getReference()).isEqualTo("OXF-001");
        assertThat(second.getDate()).isEqualTo(new LocalDate(2019,4,1));
        assertThat(second.getType()).isEqualTo(Type.AUDITED);
        assertThat(second.getFrequency()).isEqualTo(Frequency.DAILY);
        assertThat(second.getReportedAt().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(second.getReportedBy()).isEqualTo("tester");
        assertThat(second.getCurrency()).isEqualTo(Currency_enum.GBP.findUsing(serviceRegistry2));
        assertThat(second.getNetAmount()).isNull();
        assertThat(second.getGrossAmount()).isEqualTo(new BigDecimal("2345.67"));
        assertThat(second.getPurchaseCount()).isNull();
        assertThat(second.getComments()).isEqualTo("This is non comparable because of some reason");
        assertThat(second.isNonComparable()).isTrue();
    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}