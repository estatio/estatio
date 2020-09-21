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
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class AmortisationEntryRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb);
            }
        });
    }

    @Test
    public void upsert_amortisation_entry_and_find_by_schedule_works() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem discountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final Charge charge = discountItem.getCharge();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        final Frequency freq = Frequency.MONTHLY;
        final LocalDate startDate = discountItem.getStartDate();
        final LocalDate endDate = startDate.plusYears(1);
        final AmortisationSchedule schedule = amortisationScheduleRepository
                .findOrCreate(discountItem, scheduledAmount, freq,
                        startDate, endDate);
        assertThat(amortisationEntryRepository.listAll()).isEmpty();
        assertThat(schedule).isNotNull();

        // when
        final AmortisationEntry entry = amortisationEntryRepository
                .findOrCreate(schedule, startDate, scheduledAmount);

        // then
        assertThat(entry.getSchedule()).isEqualTo(schedule);
        assertThat(entry.getEntryDate()).isEqualTo(startDate);
        assertThat(entry.getEntryAmount()).isEqualTo(scheduledAmount);

        assertThat(amortisationEntryRepository.listAll()).hasSize(1);
        assertThat(amortisationEntryRepository.findUnique(schedule, startDate)).isEqualTo(entry);
        assertThat(amortisationEntryRepository.findBySchedule(schedule)).hasSize(1);

        // and when
        transactionService.nextTransaction();
        final AmortisationEntry entry2 = amortisationEntryRepository
                .findOrCreate(schedule, startDate, scheduledAmount);
        // then is idempotent
        assertThat(entry2).isEqualTo(entry);

    }

    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

    @Inject AmortisationEntryRepository amortisationEntryRepository;


}