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
package org.estatio.module.lease.integtests.amendments;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentType;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentItem_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void changeDates_for_discount_item_works() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseAmendment leaseAmendment = leaseAmendmentRepository
                .findUnique(lease, LeaseAmendmentType.DEMO_TYPE);
        LocalDate discountItemStartDate = LeaseAmendmentType.DEMO_TYPE.getDiscountStartDate();
        LocalDate discountItemEndDate =  LeaseAmendmentType.DEMO_TYPE.getDiscountEndDate();
        LeaseAmendmentItemForDiscount amendmentItemForDiscount = (LeaseAmendmentItemForDiscount) leaseAmendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT).stream().findFirst().orElse(null);

        assertThat(amendmentItemForDiscount.getStartDate()).isEqualTo(discountItemStartDate);
        assertThat(amendmentItemForDiscount.getEndDate()).isEqualTo(discountItemEndDate);

        // when
        wrap(amendmentItemForDiscount).changeDates(discountItemStartDate.plusDays(1), discountItemEndDate.plusDays(1));
        // then
        assertThat(amendmentItemForDiscount.getStartDate()).isEqualTo(discountItemStartDate.plusDays(1));
        assertThat(amendmentItemForDiscount.getEndDate()).isEqualTo(discountItemEndDate.plusDays(1));

        // also given a second discount item
        leaseAmendmentItemRepository.create(leaseAmendment, new BigDecimal("10.00"), null, Arrays.asList(
                LeaseItemType.RENT), discountItemEndDate.plusMonths(1), discountItemEndDate.plusMonths(2));
        transactionService.nextTransaction();
        // and when
        wrap(amendmentItemForDiscount).changeDates(discountItemStartDate, discountItemEndDate.plusMonths(1).minusDays(1));
        transactionService.nextTransaction();
        // then
        assertThat(amendmentItemForDiscount.getStartDate()).isEqualTo(discountItemStartDate);
        assertThat(amendmentItemForDiscount.getEndDate()).isEqualTo(discountItemEndDate.plusMonths(1).minusDays(1));

        // and expect
        expectedExceptions.expect(InvalidException.class);
        expectedExceptions.expectMessage("Overlapping item for discount found on amendment OXF-TOPMODEL-001-DEM for startdate 2020-03-16 and enddate 2020-06-10");

        // when
        wrap(amendmentItemForDiscount).changeDates(discountItemStartDate, discountItemEndDate.plusMonths(1));
    }

    @Inject LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentItemRepository leaseAmendmentItemRepository;
}