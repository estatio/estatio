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

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForFrequencyChange;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentType;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentState;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentItemRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.builder());
            }
        });
    }

    @Test
    public void upsert_discount_item_works() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry);
        final LeaseAmendment leaseAmendment = leaseAmendmentRepository
                .upsert(lease, LeaseAmendmentType.DEMO_TYPE, LeaseAmendmentState.PROPOSED, null, null);
        final BigDecimal discountPercentage = new BigDecimal("50.55");
        final BigDecimal manualDiscountAmount = new BigDecimal("1234.56");
        final LocalDate itemStartDate = new LocalDate(2020, 1, 15);
        final LocalDate itemEndDate = new LocalDate(2020, 3, 31);

        // when
        LeaseAmendmentItemForDiscount amendmentItemForDiscount = leaseAmendmentItemRepository.upsert(
                leaseAmendment,
                discountPercentage,
                manualDiscountAmount,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE, LeaseItemType.MARKETING),
                itemStartDate, itemEndDate);

        // then
        assertThat(amendmentItemForDiscount.getLeaseAmendment()).isEqualTo(leaseAmendment);
        assertThat(amendmentItemForDiscount.getDiscountPercentage()).isEqualTo(discountPercentage);
        assertThat(amendmentItemForDiscount.getManualDiscountAmount()).isEqualTo(manualDiscountAmount);
        assertThat(amendmentItemForDiscount.getApplicableTo()).isEqualTo("RENT,SERVICE_CHARGE,MARKETING");
        assertThat(amendmentItemForDiscount.getStartDate()).isEqualTo(itemStartDate);
        assertThat(amendmentItemForDiscount.getEndDate()).isEqualTo(itemEndDate);

        // and when
        final BigDecimal apdaptedDiscountPercentage = new BigDecimal("51.00");
        final BigDecimal adaptedManualDiscountAmount = new BigDecimal("2345.67");
        final LocalDate adaptedItemStartDate = new LocalDate(2020, 1, 16);
        final LocalDate adaptedItemEndDate = new LocalDate(2020, 4, 1);
        amendmentItemForDiscount = leaseAmendmentItemRepository.upsert(
                leaseAmendment,
                apdaptedDiscountPercentage,
                adaptedManualDiscountAmount,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
                adaptedItemStartDate, adaptedItemEndDate);
        // then
        assertThat(amendmentItemForDiscount.getDiscountPercentage()).isEqualTo(apdaptedDiscountPercentage);
        assertThat(amendmentItemForDiscount.getManualDiscountAmount()).isEqualTo(adaptedManualDiscountAmount);
        assertThat(amendmentItemForDiscount.getApplicableTo()).isEqualTo("RENT,SERVICE_CHARGE");
        assertThat(amendmentItemForDiscount.getStartDate()).isEqualTo(adaptedItemStartDate);
        assertThat(amendmentItemForDiscount.getEndDate()).isEqualTo(adaptedItemEndDate);


    }

    @Test
    public void upsert_frequency_change_item_works() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry);
        final LeaseAmendment leaseAmendment = leaseAmendmentRepository
                .upsert(lease, LeaseAmendmentType.DEMO_TYPE, LeaseAmendmentState.PROPOSED, null, null);
        final InvoicingFrequency freqOnLease = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
        final InvoicingFrequency newFreq = InvoicingFrequency.MONTHLY_IN_ADVANCE;
        final LocalDate itemStartDate = new LocalDate(2020, 1, 15);
        final LocalDate itemEndDate = new LocalDate(2020, 3, 31);

        // when
        LeaseAmendmentItemForFrequencyChange itemForFrequencyChange = leaseAmendmentItemRepository
                .upsert(
                leaseAmendment,
                freqOnLease,
                newFreq,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE, LeaseItemType.MARKETING),
                itemStartDate, itemEndDate);

        // then
        assertThat(itemForFrequencyChange.getLeaseAmendment()).isEqualTo(leaseAmendment);
        assertThat(itemForFrequencyChange.getInvoicingFrequencyOnLease()).isEqualTo(freqOnLease);
        assertThat(itemForFrequencyChange.getAmendedInvoicingFrequency()).isEqualTo(newFreq);
        assertThat(itemForFrequencyChange.getApplicableTo()).isEqualTo("RENT,SERVICE_CHARGE,MARKETING");
        assertThat(itemForFrequencyChange.getStartDate()).isEqualTo(itemStartDate);
        assertThat(itemForFrequencyChange.getEndDate()).isEqualTo(itemEndDate);

        // and when
        final InvoicingFrequency adaptedFreqOnLease = InvoicingFrequency.QUARTERLY_IN_ARREARS;
        final InvoicingFrequency adaptedNewFreq = InvoicingFrequency.MONTHLY_IN_ARREARS;
        final LocalDate adaptedItemStartDate = new LocalDate(2020, 1, 16);
        final LocalDate adaptedItemEndDate = new LocalDate(2020, 4, 1);
        itemForFrequencyChange = leaseAmendmentItemRepository.upsert(
                leaseAmendment,
                adaptedFreqOnLease,
                adaptedNewFreq,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
                adaptedItemStartDate, adaptedItemEndDate);
        // then
        assertThat(itemForFrequencyChange.getInvoicingFrequencyOnLease()).isEqualTo(adaptedFreqOnLease);
        assertThat(itemForFrequencyChange.getAmendedInvoicingFrequency()).isEqualTo(adaptedNewFreq);
        assertThat(itemForFrequencyChange.getApplicableTo()).isEqualTo("RENT,SERVICE_CHARGE");
        assertThat(itemForFrequencyChange.getStartDate()).isEqualTo(adaptedItemStartDate);
        assertThat(itemForFrequencyChange.getEndDate()).isEqualTo(adaptedItemEndDate);

    }

    @Inject LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentItemRepository leaseAmendmentItemRepository;
}