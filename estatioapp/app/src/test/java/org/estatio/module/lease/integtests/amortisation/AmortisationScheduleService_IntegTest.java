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
package org.estatio.module.lease.integtests.amortisation;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.amortisation.AmortisationEntry;
import org.estatio.module.lease.dom.amortisation.AmortisationEntryRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleService;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class AmortisationScheduleService_IntegTest extends LeaseModuleIntegTestAbstract {

    AmortisationSchedule schedule;
    LocalDate startDate;
    LocalDate endDate;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb);
            }
        });
        final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem discountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final Charge charge = discountItem.getCharge();
        final BigDecimal scheduledAmount = BigDecimal.ZERO;
        final Frequency freq = Frequency.MONTHLY;
        startDate = discountItem.getStartDate();
        endDate = startDate.plusYears(1);
        schedule = amortisationScheduleRepository
                .findOrCreate(lease, charge, scheduledAmount, freq,
                        startDate, endDate);
        assertThat(schedule).isNotNull();
    }

    @Test
    public void createAndDistributeEntries_works() throws Exception {

        // given
        assertThat(schedule).isNotNull();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        schedule.setScheduledValue(scheduledAmount);

        // when
        amortisationScheduleService.createAndDistributeEntries(schedule);

        // then
        assertThat(schedule.getEntries()).hasSize(13);
        assertThat(schedule.getEntries().first().getEntryDate()).isEqualTo(startDate);
        assertThat(schedule.getEntries().first().getEntryAmount()).isEqualTo(new BigDecimal("9.50"));
        assertThat(schedule.getEntries().last().getEntryDate()).isEqualTo(endDate.withDayOfMonth(1));
        assertThat(schedule.getEntries().last().getEntryAmount()).isEqualTo(new BigDecimal("9.49"));

        BigDecimal sumOfEntries = BigDecimal.ZERO;
        for (AmortisationEntry e : schedule.getEntries()){
            sumOfEntries = sumOfEntries.add(e.getEntryAmount());
        }
        assertThat(sumOfEntries).isEqualTo(scheduledAmount);

    }

    @Test
    public void createAndDistributeEntries_works_when_freq_quarterly() throws Exception {

        // given
        assertThat(schedule).isNotNull();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        schedule.setScheduledValue(scheduledAmount);
        schedule.setFrequency(Frequency.QUARTERLY);
        final LocalDate startDate = new LocalDate(2020, 8, 15);
        schedule.setStartDate(startDate);
        final LocalDate endDate = startDate.plusYears(1);
        schedule.setEndDate(endDate);

        // when
        amortisationScheduleService.createAndDistributeEntries(schedule);

        // then
        assertThat(schedule.getEntries()).hasSize(5);
        assertThat(schedule.getEntries().first().getEntryDate()).isEqualTo(startDate);
        assertThat(schedule.getEntries().first().getEntryAmount()).isEqualTo(new BigDecimal("24.69"));
        assertThat(schedule.getEntries().last().getEntryDate()).isEqualTo(endDate.withDayOfMonth(1).withMonthOfYear(7));
        assertThat(schedule.getEntries().last().getEntryAmount()).isEqualTo(new BigDecimal("24.69"));

        BigDecimal sumOfEntries = BigDecimal.ZERO;
        for (AmortisationEntry e : schedule.getEntries()){
            sumOfEntries = sumOfEntries.add(e.getEntryAmount());
        }
        assertThat(sumOfEntries).isEqualTo(scheduledAmount);

    }

    @Test
    public void freq_yearly_not_supported_yet() throws Exception {

        // given
        assertThat(schedule).isNotNull();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        schedule.setScheduledValue(scheduledAmount);
        schedule.setFrequency(Frequency.YEARLY);

        // when
        amortisationScheduleService.createAndDistributeEntries(schedule);

        // then
        assertThat(schedule.getEntries()).isEmpty();

    }

    @Test
    public void no_entries_created_when_has_already_entries() throws Exception {

        // given
        assertThat(schedule).isNotNull();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        schedule.setScheduledValue(scheduledAmount);
        amortisationEntryRepository.findOrCreate(schedule, startDate, BigDecimal.ZERO);
        transactionService.nextTransaction();
        assertThat(schedule.getEntries()).hasSize(1);

        // when
        amortisationScheduleService.createAndDistributeEntries(schedule);

        // then
        assertThat(schedule.getEntries()).hasSize(1);

    }

    @Test
    public void no_entries_created_when_amount_when_amount_zero() throws Exception {

        // given
        assertThat(schedule).isNotNull();
        final BigDecimal scheduledAmount = BigDecimal.ZERO;
        schedule.setScheduledValue(scheduledAmount);

        // when
        amortisationScheduleService.createAndDistributeEntries(schedule);

        // then
        assertThat(schedule.getEntries()).isEmpty();

    }

    @Test
    public void no_entries_created_when_amount_negative() throws Exception {

        // given
        assertThat(schedule).isNotNull();
        final BigDecimal negativeAmount = new BigDecimal("-123.45");
        schedule.setScheduledValue(negativeAmount);

        // when
        amortisationScheduleService.createAndDistributeEntries(schedule);

        // then
        assertThat(schedule.getEntries()).isEmpty();

    }

    @Inject AmortisationScheduleService amortisationScheduleService;

    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

    @Inject AmortisationEntryRepository amortisationEntryRepository;

}