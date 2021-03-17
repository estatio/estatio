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

import org.assertj.core.api.Assertions;
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
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
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
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDateTime reportedAt = new LocalDateTime(2019, 1, 1, 12, 0);

        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        final BigDecimal originalGrossAmount = new BigDecimal("1234.56");
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.AUDITED, null, occupancy.getStartDate(), Frequency.MONTHLY, euro);
        Turnover turnover = turnoverRepository.create(config, turnoverDate, Type.AUDITED, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, originalGrossAmount, null, null, false);

        // when
        final Currency sek = Currency_enum.SEK.findUsing(serviceRegistry2);
        Turnover turnover2 = turnoverRepository.findOrCreate(config, turnoverDate, Type.AUDITED, Frequency.YEARLY, Status.NEW, reportedAt, "someone", sek, new BigDecimal("4321.00"), null, null, null, false);

        // then
        assertThat(turnover).isSameAs(turnover2);
        assertThat(turnover2.getGrossAmount()).isEqualTo(originalGrossAmount);
        assertThat(turnover2.getCurrency()).isEqualTo(euro);
    }

    @Test
    public void upsert_works() throws Exception {

        // given
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDateTime reportedAt = new LocalDateTime(2019, 1, 1, 12, 0);

        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        final BigDecimal originalGrossAmount = new BigDecimal("1234.56");
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.AUDITED, null, occupancy.getStartDate(), Frequency.MONTHLY, euro);
        Turnover turnover = turnoverRepository.create(config, turnoverDate, Type.AUDITED, Frequency.MONTHLY, Status.NEW, reportedAt, "someone", euro, null, originalGrossAmount, null, null, false);

        // when
        final LocalDateTime reportedAt2 = new LocalDateTime(2019, 1, 1, 13, 30);
        final BigDecimal updatedGrossAmount = new BigDecimal("1230.00");
        final Currency sek = Currency_enum.SEK.findUsing(serviceRegistry2);
        final BigDecimal netAmount = new BigDecimal("4321.00");
        Turnover turnover2 = turnoverRepository.upsert(config, turnoverDate, Type.AUDITED, Frequency.YEARLY, Status.APPROVED, reportedAt2, "someone else", sek, netAmount, updatedGrossAmount, BigInteger.valueOf(123), "changed", true);

        // then
        /* NOTE: config, date, type = unique,
        we consider frequency and currency immutable */
        assertThat(turnover).isSameAs(turnover2);
        assertThat(turnover2.getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(turnover2.getCurrency()).isEqualTo(euro);
        // mutable properties
        assertThat(turnover2.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(turnover2.getReportedAt()).isEqualTo(reportedAt2);
        assertThat(turnover2.getReportedBy()).isEqualTo("someone else");
        assertThat(turnover2.getNetAmount()).isEqualTo(netAmount);
        assertThat(turnover2.getGrossAmount()).isEqualTo(updatedGrossAmount);
        assertThat(turnover2.getPurchaseCount()).isEqualTo(BigInteger.valueOf(123));
        assertThat(turnover2.getComments()).isEqualTo("changed");
        assertThat(turnover2.isNonComparable()).isTrue();
    }

    @Test
    public void create_empty_works() throws Exception {

        // given
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);

        // when
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.AUDITED, null, occupancy.getStartDate(), Frequency.MONTHLY, euro);
        Turnover turnover = turnoverRepository.createNewEmpty(config, turnoverDate, Type.AUDITED, Frequency.MONTHLY, euro);

        // then
        assertThat(turnoverRepository.listAll()).hasSize(1);
        assertThat(turnover.getConfig()).isEqualTo(config);
        assertThat(turnover.getDate()).isEqualTo(turnoverDate);
        assertThat(turnover.getType()).isEqualTo(Type.AUDITED);
        assertThat(turnover.getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(turnover.getCurrency()).isEqualTo(euro);
        assertThat(turnover.isNonComparable()).isFalse();

        // and when
        Turnover secondTry = turnoverRepository.createNewEmpty(config, turnoverDate, Type.AUDITED, Frequency.MONTHLY, euro);
        // then still
        assertThat(turnoverRepository.listAll()).hasSize(1);
        assertThat(secondTry).isSameAs(turnover);

    }

    @Test
    public void findApprovedByConfigAndTypeAndFrequencyBeforeDate_works() throws Exception {

        // given
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        final LocalDateTime reportedAt = new LocalDateTime(2019, 1, 1, 12, 0);

        // when
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.PRELIMINARY, null, occupancy.getStartDate(), Frequency.MONTHLY, euro);
        Turnover turnover1 = turnoverRepository.create(config, turnoverDate, Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover2 = turnoverRepository.create(config, turnoverDate.minusMonths(2), Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover3 = turnoverRepository.create(config, turnoverDate.minusMonths(1), Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        List<Turnover> turnovers = turnoverRepository.findApprovedByConfigAndTypeAndFrequencyBeforeDate(config, Type.PRELIMINARY, Frequency.MONTHLY, turnoverDate);

        // then
        Assertions.assertThat(turnovers).hasSize(2);
        Assertions.assertThat(turnovers.get(0)).isEqualTo(turnover3);
        Assertions.assertThat(turnovers.get(1)).isEqualTo(turnover2);

        // and when
        turnovers = turnoverRepository.findApprovedByConfigAndTypeAndFrequencyBeforeDate(config, Type.PRELIMINARY, Frequency.MONTHLY, turnoverDate.plusDays(1));

        // then
        Assertions.assertThat(turnovers).hasSize(3);
        Assertions.assertThat(turnovers.get(0)).isEqualTo(turnover1);
        Assertions.assertThat(turnovers.get(1)).isEqualTo(turnover3);
        Assertions.assertThat(turnovers.get(2)).isEqualTo(turnover2);
    }

    @Test
    public void findByConfigAndTypeAndFrequencyAndStatusInPeriod() throws Exception {

        // given
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        final LocalDateTime reportedAt = new LocalDateTime(2019, 1, 1, 12, 0);

        // when
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.PRELIMINARY, null, occupancy.getStartDate(), Frequency.MONTHLY, euro);
        Turnover turnover1 = turnoverRepository.create(config, turnoverDate, Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover2 = turnoverRepository.create(config, turnoverDate.minusMonths(2), Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover3 = turnoverRepository.create(config, turnoverDate.minusMonths(1), Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);

        // then
        Assertions.assertThat(turnoverRepository.findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.MONTHLY, Status.NEW, turnoverDate, turnoverDate)).hasSize(0);
        Assertions.assertThat(turnoverRepository.findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.DAILY, Status.APPROVED, turnoverDate, turnoverDate)).hasSize(0);
        Assertions.assertThat(turnoverRepository.findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.AUDITED, Frequency.MONTHLY, Status.APPROVED, turnoverDate, turnoverDate)).hasSize(0);

        final List<Turnover> list1 = turnoverRepository
                .findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.MONTHLY,
                        Status.APPROVED, turnoverDate, turnoverDate);
        Assertions.assertThat(list1).hasSize(1);
        Assertions.assertThat(list1).contains(turnover1);
        Assertions.assertThat(list1).doesNotContain(turnover2);
        Assertions.assertThat(list1).doesNotContain(turnover3);
        final List<Turnover> list2 = turnoverRepository
                .findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.MONTHLY,
                        Status.APPROVED, turnoverDate.minusMonths(1), turnoverDate);
        Assertions.assertThat(list2).hasSize(2);
        Assertions.assertThat(list2).contains(turnover1);
        Assertions.assertThat(list2).contains(turnover3);
        Assertions.assertThat(list2).doesNotContain(turnover2);
        Assertions.assertThat(turnoverRepository.findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, turnoverDate.minusMonths(2).plusDays(1), turnoverDate)).hasSize(2);
        final List<Turnover> list3 = turnoverRepository
                .findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.MONTHLY,
                        Status.APPROVED, turnoverDate.minusMonths(2), turnoverDate);
        Assertions.assertThat(list3).hasSize(3);
        Assertions.assertThat(list3).contains(turnover1);
        Assertions.assertThat(list3).contains(turnover2);
        Assertions.assertThat(list3).contains(turnover3);
        Assertions.assertThat(turnoverRepository.findByConfigAndTypeAndFrequencyAndStatusInPeriod(config, Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, turnoverDate.minusMonths(3), turnoverDate)).hasSize(3);
    }

    @Test
    public void findApprovedByConfigAndTypeAndFrequency() throws Exception {

        // given
        final Occupancy occupancy = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first();
        final LocalDate turnoverDate = new LocalDate(2019, 1, 1);
        final Currency euro = Currency_enum.EUR.findUsing(serviceRegistry2);
        final LocalDateTime reportedAt = new LocalDateTime(2019, 1, 1, 12, 0);

        // when
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findOrCreate(occupancy, Type.PRELIMINARY, null, occupancy.getStartDate(), Frequency.MONTHLY, euro);

        Turnover turnover1 = turnoverRepository.create(config, turnoverDate, Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover2 = turnoverRepository.create(config, turnoverDate.minusMonths(2), Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover3 = turnoverRepository.create(config, turnoverDate.minusMonths(1), Type.PRELIMINARY, Frequency.MONTHLY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover4 = turnoverRepository.create(config, turnoverDate.minusMonths(1).minusDays(1), Type.PRELIMINARY, Frequency.DAILY, Status.APPROVED, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover5 = turnoverRepository.create(config, turnoverDate.minusMonths(3), Type.PRELIMINARY, Frequency.MONTHLY, Status.NEW, reportedAt, "someone", euro, null, null, null, null, false);
        Turnover turnover6 = turnoverRepository.create(config, turnoverDate.minusMonths(3), Type.AUDITED, Frequency.MONTHLY, Status.NEW, reportedAt, "someone", euro, null, null, null, null, false);
        // then
        Assertions.assertThat(turnoverRepository.findApprovedByConfigAndTypeAndFrequency(config, Type.PRELIMINARY, Frequency.MONTHLY)).hasSize(3);
        Assertions.assertThat(turnoverRepository.findApprovedByConfigAndTypeAndFrequency(config, Type.PRELIMINARY, Frequency.DAILY)).hasSize(1);
        Assertions.assertThat(turnoverRepository.findApprovedByConfigAndTypeAndFrequency(config, Type.AUDITED, Frequency.MONTHLY)).hasSize(0);

        // and when
        turnover5.setStatus(Status.APPROVED);
        turnover6.setStatus(Status.APPROVED);
        // then
        Assertions.assertThat(turnoverRepository.findApprovedByConfigAndTypeAndFrequency(config, Type.PRELIMINARY, Frequency.MONTHLY)).hasSize(4);
        Assertions.assertThat(turnoverRepository.findApprovedByConfigAndTypeAndFrequency(config, Type.PRELIMINARY, Frequency.DAILY)).hasSize(1);
        Assertions.assertThat(turnoverRepository.findApprovedByConfigAndTypeAndFrequency(config, Type.AUDITED, Frequency.MONTHLY)).hasSize(1);

    }


    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}