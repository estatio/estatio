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
import java.math.BigInteger;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemType;
import org.estatio.module.lease.dom.amendments.Lease_amendments;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleAmendmentItemLink;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleAmendmentItemLinkRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleLeaseItemLink;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleLeaseItemLinkRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class AmortisationLinkRepositories_IntegTest extends LeaseModuleIntegTestAbstract {

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
    public void upsert_link_to_lease_item_and_finders_work() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem discountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final Charge charge = discountItem.getCharge();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        final Frequency freq = Frequency.MONTHLY;
        final LocalDate startDate = discountItem.getStartDate();
        final LocalDate endDate = startDate.plusYears(1);
        final AmortisationSchedule schedule = amortisationScheduleRepository
                .findOrCreate(lease, charge, scheduledAmount, freq,
                        startDate, endDate, BigInteger.ONE);
        assertThat(amortisationScheduleLeaseItemLinkRepo.listAll()).isEmpty();
        assertThat(schedule).isNotNull();

        // when
        final AmortisationScheduleLeaseItemLink link = amortisationScheduleLeaseItemLinkRepo
                .findOrCreate(schedule, discountItem);

        // then
        assertThat(link.getAmortisationSchedule()).isEqualTo(schedule);
        assertThat(link.getLeaseItem()).isEqualTo(discountItem);


        assertThat(amortisationScheduleLeaseItemLinkRepo.listAll()).hasSize(1);
        assertThat(amortisationScheduleLeaseItemLinkRepo.findUnique(schedule, discountItem)).isEqualTo(link);
        assertThat(amortisationScheduleLeaseItemLinkRepo.findBySchedule(schedule)).hasSize(1);

        // and when
        transactionService.nextTransaction();
        final AmortisationScheduleLeaseItemLink link2 = amortisationScheduleLeaseItemLinkRepo
                .findOrCreate(schedule, discountItem);
        // then is idempotent
        assertThat(link2).isEqualTo(link);

        assertThat(amortisationScheduleLeaseItemLinkRepo.findByLeaseItem(discountItem)).hasSize(1);

    }

    @Test
    public void upsert_link_to_amendment_item_and_find_by_schedule_works() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem discountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final Charge charge = discountItem.getCharge();
        final BigDecimal scheduledAmount = new BigDecimal("123.45");
        final Frequency freq = Frequency.MONTHLY;
        final LocalDate startDate = discountItem.getStartDate();
        final LocalDate endDate = startDate.plusYears(1);
        final AmortisationSchedule schedule = amortisationScheduleRepository
                .findOrCreate(lease, charge, scheduledAmount, freq,
                        startDate, endDate, BigInteger.ONE);
        assertThat(amortisationScheduleAmendmentItemLinkRepo.listAll()).isEmpty();
        assertThat(schedule).isNotNull();
        final LeaseAmendment amendment = mixin(Lease_amendments.class, lease).$$().stream().findFirst().orElse(null);
        final LeaseAmendmentItemForDiscount amendmentItem = (LeaseAmendmentItemForDiscount) amendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream().findFirst().orElse(null);
        assertThat(amendmentItem).isNotNull();

        // when
        final AmortisationScheduleAmendmentItemLink link = amortisationScheduleAmendmentItemLinkRepo
                .findOrCreate(schedule, amendmentItem);

        // then
        assertThat(link.getAmortisationSchedule()).isEqualTo(schedule);
        assertThat(link.getLeaseAmendmentItemForDiscount()).isEqualTo(amendmentItem);


        assertThat(amortisationScheduleAmendmentItemLinkRepo.listAll()).hasSize(1);
        assertThat(amortisationScheduleAmendmentItemLinkRepo.findUnique(schedule, amendmentItem)).isEqualTo(link);
        assertThat(amortisationScheduleAmendmentItemLinkRepo.findBySchedule(schedule)).hasSize(1);

        // and when
        transactionService.nextTransaction();
        final AmortisationScheduleAmendmentItemLink link2 = amortisationScheduleAmendmentItemLinkRepo
                .findOrCreate(schedule, amendmentItem);
        // then is idempotent
        assertThat(link2).isEqualTo(link);

    }

    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

    @Inject AmortisationScheduleLeaseItemLinkRepository amortisationScheduleLeaseItemLinkRepo;

    @Inject AmortisationScheduleAmendmentItemLinkRepository amortisationScheduleAmendmentItemLinkRepo;

}