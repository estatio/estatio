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
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForFrequencyChange;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentTemplate;
import org.estatio.module.lease.dom.amendments.Lease_createLeaseAmendment;
import org.estatio.module.lease.dom.amendments.Lease_invoiceCalculations;
import org.estatio.module.lease.dom.indexation.IndexationMethod;
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

        final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(oxf, LeaseAmendmentTemplate.DEMO_TYPE);
        assertThat(amendment).isNotNull();
        assertThat(amendment.getLeasePreview()).isNull();

        final LeaseAmendmentItemForDiscount discountAmendmentItem2 = leaseAmendmentItemRepository
                .create(
                        amendment,
                        new BigDecimal("50.00"),
                        null,
                        Arrays.asList(LeaseItemType.RENT, LeaseItemType.RENT_DISCOUNT, LeaseItemType.RENT_DISCOUNT_FIXED),
                        new LocalDate(2020, 6, 1),
                        new LocalDate(2020, 6, 30));

        final LeaseAmendmentItemForDiscount discountAmendmentItem3 = leaseAmendmentItemRepository
                .create(
                        amendment,
                        new BigDecimal("25.00"),
                        null,
                        Arrays.asList(LeaseItemType.RENT, LeaseItemType.RENT_DISCOUNT, LeaseItemType.RENT_DISCOUNT_FIXED),
                        new LocalDate(2020, 7, 1),
                        new LocalDate(2020, 7, 31));

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
        assertThat(leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT)).hasSize(3);

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
        assertThat(thirdNewRentItem.getTerms()).hasSize(2);

        final LeaseItem discountRentItem1 = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT).stream()
                .filter(li -> li.getStartDate().equals(discountAmendmentItem.getStartDate()))
                .findFirst().orElse(null);
        assertThat(discountRentItem1.getEndDate()).isEqualTo(discountAmendmentItem.getEndDate());
        assertThat(discountRentItem1.getCharge().getReference()).isEqualTo(amendment.getLeaseAmendmentTemplate().getChargeReferenceForDiscountItem().get(0).newValue);
        assertThat(discountRentItem1.getTerms()).hasSize(1);
        final LeaseTermForIndexable firstOnDiscountItem1 = (LeaseTermForIndexable) discountRentItem1.getTerms().first();
        assertThat(firstOnDiscountItem1.getEffectiveValue()).isEqualTo(new BigDecimal("-10652.51"));

        final LeaseItem discountRentItem2 = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT).stream()
                .filter(li -> li.getStartDate().equals(discountAmendmentItem2.getStartDate()))
                .findFirst().orElse(null);
        assertThat(discountRentItem2.getEndDate()).isEqualTo(discountAmendmentItem2.getEndDate());
        assertThat(discountRentItem2.getCharge().getReference()).isEqualTo(amendment.getLeaseAmendmentTemplate().getChargeReferenceForDiscountItem().get(0).newValue);
        assertThat(discountRentItem2.getTerms()).hasSize(1);
        final LeaseTermForIndexable firstOnDiscountItem2 = (LeaseTermForIndexable) discountRentItem2.getTerms().first();
        assertThat(firstOnDiscountItem2.getEffectiveValue()).isEqualTo(new BigDecimal("-10652.51"));

        final LeaseItem discountRentItem3 = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT).stream()
                .filter(li -> li.getStartDate().equals(discountAmendmentItem3.getStartDate()))
                .findFirst().orElse(null);
        assertThat(discountRentItem3.getEndDate()).isEqualTo(discountAmendmentItem3.getEndDate());
        assertThat(discountRentItem3.getCharge().getReference()).isEqualTo(amendment.getLeaseAmendmentTemplate().getChargeReferenceForDiscountItem().get(0).newValue);
        assertThat(discountRentItem3.getTerms()).hasSize(2);
        final LeaseTermForIndexable firstOnDiscountItem3 = (LeaseTermForIndexable) discountRentItem3.getTerms().first();
        assertThat(firstOnDiscountItem3.getEffectiveValue()).isEqualTo(new BigDecimal("-5326.26"));

        assertThat(discountAmendmentItem.calculateDiscountAmountUsingLeasePreview()).isEqualTo(new BigDecimal("-1638.85"));
        assertThat(discountAmendmentItem.getCalculatedDiscountAmount()).isEqualTo(new BigDecimal("-1638.85"));
        assertThat(discountAmendmentItem.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("19305.02")); // is the value for date of the original rent item and the original discount item
        assertThat(discountAmendmentItem2.calculateDiscountAmountUsingLeasePreview()).isEqualTo(new BigDecimal("-877.95"));
        assertThat(discountAmendmentItem2.getCalculatedDiscountAmount()).isEqualTo(new BigDecimal("-877.95"));
        assertThat(discountAmendmentItem2.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("19305.02")); // is the value for date of the original rent item and the original discount item
        assertThat(discountAmendmentItem3.calculateDiscountAmountUsingLeasePreview()).isEqualTo(new BigDecimal("-443.85"));
        assertThat(discountAmendmentItem3.getCalculatedDiscountAmount()).isEqualTo(new BigDecimal("-443.85"));
        assertThat(discountAmendmentItem3.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("19305.02")); // is the value for date of the original rent item and the original discount item
        assertThat(originalRentItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02"));

        final LeaseItem originalDiscountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(originalDiscountItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("-2000.00"));
        assertThat(mixin(Lease_invoiceCalculations.class, leasePreview).$$()).hasSize(23);

    }

    @Test
    public void scenario_manual_discount_value() throws Exception {

        Lease oxf = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

        mixin(Lease_createLeaseAmendment.class, oxf).$$(LeaseAmendmentTemplate.DEMO_TYPE2);
        transactionService.nextTransaction();
        final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(oxf, LeaseAmendmentTemplate.DEMO_TYPE2);
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
        assertThat(discountRentItem.getCharge().getReference()).isEqualTo(amendment.getLeaseAmendmentTemplate().getChargeReferenceForDiscountItem().get(0).newValue);
        assertThat(discountRentItem.getTerms()).hasSize(2);
        final LeaseTermForIndexable first = (LeaseTermForIndexable) discountRentItem.getTerms().first();
        assertThat(first.getEffectiveValue()).isEqualTo(new BigDecimal("-21305.02"));
        assertThat(discountAmendmentItem.calculateDiscountAmountUsingLeasePreview()).isEqualTo(new BigDecimal("-3550.84"));
        assertThat(discountAmendmentItem.getCalculatedDiscountAmount()).isEqualTo(new BigDecimal("-3550.84"));
        assertThat(discountAmendmentItem.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("19305.02")); // is the value for date of the original rent item and the original discount item
        assertThat(originalRentItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02"));
        final LeaseItem originalDiscountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(originalDiscountItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("-2000.00"));

        // when using manual value
        final BigDecimal manualDiscountAmount = new BigDecimal("-1234.56");
        discountAmendmentItem.changeManualDiscountAmount(manualDiscountAmount);

        // then
        leasePreview = amendment.getLeasePreview();
        final LeaseItem firstNewRentItem = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT_FIXED).stream()
                .filter(li -> li.getCharge().getReference().equals( LeaseAmendmentTemplate.DEMO_TYPE2.getChargeReferenceForDiscountItem().get(0).newValue))
                .findFirst().orElse(null);
        assertThat(firstNewRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
        assertThat(discountAmendmentItem.calculateDiscountAmountUsingLeasePreview()).isEqualTo(manualDiscountAmount);
        assertThat(discountAmendmentItem.getCalculatedDiscountAmount()).isEqualTo(manualDiscountAmount);
        assertThat(originalRentItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02")); // EQUALS the value for date just before discount of the only lease item used by amendment item for discount

        final LeaseItem rentItemUsedInTotalValueCalculationBeforeDiscount = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getInterval().contains(discountAmendmentItem.getStartDate().minusDays(1))).findFirst()
                .orElse(null);
        assertThat(rentItemUsedInTotalValueCalculationBeforeDiscount.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("21305.02"));
        assertThat(originalDiscountItem.valueForDate(discountAmendmentItem.getStartDate().minusDays(1))).isEqualTo(new BigDecimal("-2000.00"));
        assertThat(discountAmendmentItem.getTotalValueForDateBeforeDiscount()).isEqualTo(new BigDecimal("19305.02"));

    }

    @Test
    public void scenario_no_indexed_value() throws Exception {

        // given
        Lease oxfLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        LeaseItem rentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(rentItem.getTerms()).hasSize(1);
        final LeaseTermForIndexable term1 = (LeaseTermForIndexable) rentItem.getTerms().first();
        term1.setIndexationMethod(IndexationMethod.BASE_INDEX);

        // when
        final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(oxfLease, LeaseAmendmentTemplate.DEMO_TYPE);
        amendment.createOrRenewLeasePreview();

        // then
        assertThat(rentItem.getTerms()).hasSize(11);
        final LeaseTermForIndexable lastRentTerm = (LeaseTermForIndexable) rentItem.getTerms().last();
        final BigDecimal baseValue = new BigDecimal("20000.00");
        assertThat(lastRentTerm.getBaseValue()).isEqualTo(baseValue);
        final BigDecimal effectiveIndexedValueOnLastRentTerm = new BigDecimal("21300.00");
        assertThat(lastRentTerm.getEffectiveIndexedValue()).isEqualTo(effectiveIndexedValueOnLastRentTerm);

        final Lease leasePreview = amendment.getLeasePreview();

        assertThat(leasePreview.findItemsOfType(LeaseItemType.RENT)).hasSize(3);
        final LeaseItem rentItemPrev1 = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getStartDate().equals(rentItem.getStartDate())).findFirst()
                .orElse(null);
        final LeaseItem rentItemPrev2 = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getStartDate().equals(new LocalDate(2020,7,1))).findFirst()
                .orElse(null);
        final LeaseItem rentItemPrev3 = leasePreview.findItemsOfType(LeaseItemType.RENT).stream()
                .filter(li -> li.getStartDate().equals(new LocalDate(2021,1,1))).findFirst()
                .orElse(null);
        assertThat(rentItemPrev1.getTerms()).hasSize(1);
        final LeaseTermForIndexable term1RentItemPrev1 = (LeaseTermForIndexable) rentItemPrev1.getTerms().first();
        assertThat(term1RentItemPrev1.getBaseValue()).isEqualTo(baseValue);
        assertThat(term1RentItemPrev1.getEffectiveIndexedValue()).isEqualTo(effectiveIndexedValueOnLastRentTerm);

        final LeaseTermForIndexable term1RentItemPrev2 = (LeaseTermForIndexable) rentItemPrev2.getTerms().first();
        assertThat(term1RentItemPrev2.getBaseValue()).isEqualTo(baseValue);
        assertThat(term1RentItemPrev2.getEffectiveIndexedValue()).isEqualTo(effectiveIndexedValueOnLastRentTerm);

        final LeaseTermForIndexable term1RentItemPrev3 = (LeaseTermForIndexable) rentItemPrev3.getTerms().first();
        assertThat(term1RentItemPrev3.getBaseValue()).isEqualTo(baseValue);
        assertThat(term1RentItemPrev3.getEffectiveIndexedValue()).isEqualTo(effectiveIndexedValueOnLastRentTerm);

        assertThat(leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT)).hasSize(1);
        final LeaseItem rent_discount_item = leasePreview.findItemsOfType(LeaseItemType.RENT_DISCOUNT).get(0);
        assertThat(rent_discount_item.getTerms()).hasSize(1);
        final LeaseTermForIndexable discountTerm = (LeaseTermForIndexable) rent_discount_item.getTerms().first();
        assertThat(discountTerm.getBaseValue()).isEqualTo(new BigDecimal("-10000.00"));
        assertThat(discountTerm.getEffectiveIndexedValue()).isEqualTo(new BigDecimal("-10650.00"));
    }

    @Test
    public void scenario_first_indexation_after_frequency_amendment_enddate() throws Exception {

        // given
        Lease oxf = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);



    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentItemRepository leaseAmendmentItemRepository;

}