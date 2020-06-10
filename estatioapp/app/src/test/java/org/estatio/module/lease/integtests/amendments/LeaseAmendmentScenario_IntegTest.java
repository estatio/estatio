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

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForFrequencyChange;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentType;
import org.estatio.module.lease.dom.amendments.Lease_createLeaseAmendment;
import org.estatio.module.lease.dom.amendments.Lease_invoiceCalculations;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentScenario_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void scenario_test() throws Exception {

        Lease oxf = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

        final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(oxf, LeaseAmendmentType.DEMO_TYPE);
        assertThat(amendment).isNotNull();
        assertThat(amendment.getLeasePreview()).isNull();

        final LeaseAmendmentItemForDiscount discountAmendmentItem = (LeaseAmendmentItemForDiscount) amendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream().findFirst().orElse(null);
        final LeaseAmendmentItemForFrequencyChange frqChangeAmendmentItem = (LeaseAmendmentItemForFrequencyChange) amendment.findItemsOfType(LeaseAmendmentItemType.INVOICING_FREQUENCY_CHANGE)
                .stream().findFirst().orElse(null);

        final LeaseItem originalRentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(originalRentItem.getEndDate()).isEqualTo(new LocalDate(2022, 7,14));
        assertThat(originalRentItem.getTerms()).hasSize(1);
        assertThat(originalRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        assertThat(oxf.findItemsOfType(LeaseItemType.RENT)).hasSize(1);


        // when
        amendment.createOrRenewLeasePreview();

        // then
        assertThat(originalRentItem.getTerms()).hasSize(11);
        final Lease leasePreview = amendment.getLeasePreview();
        assertThat(leasePreview).isNotNull();
        assertThat(leasePreview.findItemsOfType(LeaseItemType.RENT)).hasSize(3);
        assertThat(leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT)).hasSize(1);

        final LeaseItem firstNewRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getStartDate().equals(originalRentItem.getStartDate()))
                .findFirst().orElse(null);
        assertThat(firstNewRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        assertThat(firstNewRentItem.getEndDate()).isEqualTo(frqChangeAmendmentItem.getStartDate().minusDays(1));

        final LeaseItem secondNewRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getStartDate().equals(frqChangeAmendmentItem.getStartDate()))
                .findFirst().orElse(null);
        assertThat(secondNewRentItem.getEndDate()).isEqualTo(frqChangeAmendmentItem.getEndDate());
        assertThat(secondNewRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.MONTHLY_IN_ARREARS);
        assertThat(secondNewRentItem.getTerms()).hasSize(2);

        final LeaseItem thirdNewRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getStartDate().equals(frqChangeAmendmentItem.getEndDate().plusDays(1)))
                .findFirst().orElse(null);
        assertThat(thirdNewRentItem.getEndDate()).isEqualTo(originalRentItem.getEndDate());
        assertThat(thirdNewRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        assertThat(thirdNewRentItem.getTerms()).hasSize(1);

        final LeaseItem discountRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT).stream()
                .filter(li -> li.getStartDate().equals(discountAmendmentItem.getStartDate()))
                .findFirst().orElse(null);
        assertThat(discountRentItem.getEndDate()).isEqualTo(discountAmendmentItem.getEndDate());
        assertThat(discountRentItem.getCharge().getReference()).isEqualTo(amendment.getLeaseAmendmentType().getChargeReferenceForDiscountItem().get(0).newValue);
        assertThat(discountRentItem.getTerms()).hasSize(1);
        final LeaseTermForIndexable first = (LeaseTermForIndexable) discountRentItem.getTerms().first();
        assertThat(first.getEffectiveValue()).isEqualTo(new BigDecimal("-10652.51"));

        assertThat(discountAmendmentItem.calculateDiscountAmountUsingLeasePreview()).isEqualTo(new BigDecimal("-1638.85"));
        assertThat(discountAmendmentItem.getCalculatedDiscountAmount()).isEqualTo(new BigDecimal("-1638.85"));
        assertThat(discountAmendmentItem.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("21305.02"));
        assertThat(originalRentItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02")); // EQUALS the value for date just before discount of the only lease item used by amendment item for discount
        assertThat(mixin(Lease_invoiceCalculations.class, leasePreview).$$()).hasSize(20);

    }

    @Test
    public void scenario_manual_discount_value() throws Exception {

        Lease oxf = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

        mixin(Lease_createLeaseAmendment.class, oxf).$$(LeaseAmendmentType.DEMO_TYPE2);
        transactionService.nextTransaction();
        final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(oxf, LeaseAmendmentType.DEMO_TYPE2);
        assertThat(amendment).isNotNull();
        assertThat(amendment.getLeasePreview()).isNull();

        final LeaseAmendmentItemForDiscount discountAmendmentItem = (LeaseAmendmentItemForDiscount) amendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream().findFirst().orElse(null);
        final LeaseAmendmentItemForFrequencyChange frqChangeAmendmentItem = (LeaseAmendmentItemForFrequencyChange) amendment.findItemsOfType(LeaseAmendmentItemType.INVOICING_FREQUENCY_CHANGE)
                .stream().findFirst().orElse(null);

        final LeaseItem originalRentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(originalRentItem.getEndDate()).isEqualTo(new LocalDate(2022, 7,14));
        assertThat(originalRentItem.getTerms()).hasSize(1);
        assertThat(originalRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        assertThat(oxf.findItemsOfType(LeaseItemType.RENT)).hasSize(1);

        // when
        amendment.createOrRenewLeasePreview();

        // then
        Lease leasePreview = amendment.getLeasePreview();
        final LeaseItem discountRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT).stream()
                .filter(li -> li.getStartDate().equals(discountAmendmentItem.getStartDate()))
                .findFirst().orElse(null);
        assertThat(discountRentItem.getEndDate()).isEqualTo(discountAmendmentItem.getEndDate());
        assertThat(discountRentItem.getCharge().getReference()).isEqualTo(amendment.getLeaseAmendmentType().getChargeReferenceForDiscountItem().get(0).newValue);
        assertThat(discountRentItem.getTerms()).hasSize(2);
        final LeaseTermForIndexable first = (LeaseTermForIndexable) discountRentItem.getTerms().first();
        assertThat(first.getEffectiveValue()).isEqualTo(new BigDecimal("-21305.02"));
        assertThat(discountAmendmentItem.calculateDiscountAmountUsingLeasePreview()).isEqualTo(new BigDecimal("-3550.84"));
        assertThat(discountAmendmentItem.getCalculatedDiscountAmount()).isEqualTo(new BigDecimal("-3550.84"));
        assertThat(discountAmendmentItem.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("21305.02"));
        assertThat(originalRentItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02")); // EQUALS the value for date just before discount of the only lease item used by amendment item for discount

        // when using manual value
        final BigDecimal manualDiscountAmount = new BigDecimal("-1234.56");
        discountAmendmentItem.changeManualDiscountAmount(manualDiscountAmount);

        // then
        leasePreview = amendment.getLeasePreview();
        final LeaseItem firstNewRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT_FIXED).stream()
                .filter(li -> li.getCharge().getReference().equals( LeaseAmendmentType.DEMO_TYPE2.getChargeReferenceForDiscountItem().get(0).newValue))
                .findFirst().orElse(null);
        assertThat(firstNewRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
        assertThat(discountAmendmentItem.calculateDiscountAmountUsingLeasePreview()).isEqualTo(manualDiscountAmount);
        assertThat(discountAmendmentItem.getCalculatedDiscountAmount()).isEqualTo(manualDiscountAmount);
        assertThat(originalRentItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02")); // EQUALS the value for date just before discount of the only lease item used by amendment item for discount

        final LeaseItem rentItemUsedInTotalValueCalculationBeforeDiscount = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getInterval().contains(discountAmendmentItem.getStartDate().minusDays(1))).findFirst()
                .orElse(null);
        assertThat(rentItemUsedInTotalValueCalculationBeforeDiscount.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02"));
        assertThat(discountAmendmentItem.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("21305.02"));

    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

}