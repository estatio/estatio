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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSourceRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentService;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentService_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void closeOriginalAndOpenNewLeaseItem_works() throws Exception {

        final LocalDate endDateOriginalItem;
        final LocalDate firstSplitDate;
        final LocalDate secondSplitDate;

        // given
        Lease oxfLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem originalRentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem depositItem = LeaseItemForDeposit_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

        endDateOriginalItem = originalRentItem.getEndDate();
        firstSplitDate = new LocalDate(2020, 7, 1);
        secondSplitDate = new LocalDate(2021, 1, 1);

        oxfLease.verifyUntil(new LocalDate(2020,12,31));

        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).hasSize(1);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT).get(0)).isEqualTo(originalRentItem);
        assertThat(originalRentItem.getEndDate()).isNotNull();
        assertThat(leaseItemSourceRepository.findByItem(depositItem)).hasSize(1);
        assertThat(leaseItemSourceRepository.findByItem(depositItem).get(0).getSourceItem()).isEqualTo(originalRentItem);

        // when
        final LeaseItem firstNewItem = leaseAmendmentService
                .closeOriginalAndOpenNewLeaseItem(firstSplitDate, originalRentItem,
                        InvoicingFrequency.MONTHLY_IN_ADVANCE);

        // then
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).hasSize(2);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).contains(originalRentItem);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).contains(firstNewItem);
        assertThat(originalRentItem.getEndDate()).isEqualTo(firstSplitDate.minusDays(1));
        assertThat(originalRentItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        assertThat(firstNewItem.getStartDate()).isEqualTo(firstSplitDate);
        assertThat(firstNewItem.getEndDate()).isEqualTo(endDateOriginalItem);
        assertThat(firstNewItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        assertThat(leaseItemSourceRepository.findByItem(depositItem)).hasSize(2);
        assertThat(leaseItemSourceRepository.findByItem(depositItem).stream().map(s->s.getSourceItem()).collect(
                Collectors.toList())).contains(firstNewItem);
        assertThat(leaseItemSourceRepository.findByItem(depositItem).stream().map(s->s.getSourceItem()).collect(
                Collectors.toList())).contains(originalRentItem);

        // and when
        final LeaseItem secondNewItem = leaseAmendmentService
                .closeOriginalAndOpenNewLeaseItem(secondSplitDate, firstNewItem,
                        InvoicingFrequency.QUARTERLY_IN_ADVANCE);

        // then
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).hasSize(3);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).contains(originalRentItem);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).contains(firstNewItem);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT)).contains(secondNewItem);
        assertThat(firstNewItem.getEndDate()).isEqualTo(secondSplitDate.minusDays(1));
        assertThat(secondNewItem.getStartDate()).isEqualTo(secondSplitDate);
        assertThat(secondNewItem.getEndDate()).isEqualTo(endDateOriginalItem);
        assertThat(secondNewItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        assertThat(leaseItemSourceRepository.findByItem(depositItem)).hasSize(3);
        assertThat(leaseItemSourceRepository.findByItem(depositItem).stream().map(s->s.getSourceItem()).collect(
                Collectors.toList())).contains(secondNewItem);

    }

    @Test
    public void closeOriginalAndOpenNewLeaseItem_works_for_discount_fixed() throws Exception {

        final LocalDate splitDate;

        // given
        Lease oxfLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem originalDiscountItem = LeaseItemForDiscount_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        splitDate = new LocalDate(originalDiscountItem.getTerms().first().getStartDate().plusYears(1));
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT_DISCOUNT_FIXED)).hasSize(1);

        // when
        final LeaseItem firstNewItem = leaseAmendmentService
                        .closeOriginalAndOpenNewLeaseItem(splitDate, originalDiscountItem,
                                InvoicingFrequency.MONTHLY_IN_ADVANCE);

        // then
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT_DISCOUNT_FIXED)).hasSize(2);
        assertThat(oxfLease.findItemsOfType(LeaseItemType.RENT_DISCOUNT_FIXED)).contains(firstNewItem);
        assertThat(originalDiscountItem.getEndDate()).isEqualTo(splitDate.minusDays(1));
        assertThat(firstNewItem.getStartDate()).isEqualTo(splitDate);
        assertThat(firstNewItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        assertThat(firstNewItem.getTerms()).hasSize(1);
    }

    @Test
    public void getLeaseCopyForPreview_works() throws Exception {

        // given
        Lease oxfLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem originalRentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem originalDepositItem = LeaseItemForDeposit_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(leaseItemSourceRepository.findByItem(originalDepositItem)).hasSize(1);
        assertThat(leaseItemSourceRepository.findByItem(originalDepositItem).get(0).getSourceItem()).isEqualTo(originalRentItem);

        // when
        final LocalDate referenceDate = new LocalDate(2020, 3, 16);
        final LocalDate verificationDate = new LocalDate(2020, 12, 31);
        final Lease previewLease = leaseAmendmentService
                .getLeaseCopyForPreview(oxfLease, referenceDate, verificationDate, "PREVIEW_REF");

        // then
        assertThat(originalRentItem.getTerms()).hasSize(11); // original lease is verified
        assertThat(previewLease.getStatus()).isEqualTo(LeaseStatus.PREVIEW);
        assertThat(previewLease.getReference()).isEqualTo("PREVIEW_REF");
        // etc.

        assertThat(previewLease.findItemsOfType(LeaseItemType.RENT)).hasSize(1);
        assertThat(previewLease.findItemsOfType(LeaseItemType.DEPOSIT)).hasSize(1);

        final LeaseItem rentPreviewItem = previewLease.findItemsOfType(LeaseItemType.RENT).get(0);
        assertThat(rentPreviewItem.getStartDate()).isEqualTo(originalRentItem.getStartDate());
        assertThat(rentPreviewItem.getEndDate()).isEqualTo(originalRentItem.getEndDate());
        assertThat(rentPreviewItem.getTerms()).hasSize(1);

        final LeaseItem depositPreviewItem = previewLease.findItemsOfType(LeaseItemType.DEPOSIT).get(0);
        assertThat(depositPreviewItem.getStartDate()).isEqualTo(originalDepositItem.getStartDate());
        assertThat(depositPreviewItem.getEndDate()).isEqualTo(originalDepositItem.getEndDate());
        //TODO: NOTE THAT WE COPY ALL ITEM TYPES BUT NOT THEIR SOURCES; this adds complications and may not be needed for reporting / forecasting
        // ANOTHER APPROACH would be to not copy these itemtypes at all ...
        assertThat(leaseItemSourceRepository.findByItem(depositPreviewItem)).hasSize(0);
//        assertThat(leaseItemSourceRepository.findByItem(depositPreviewItem).stream().map(s->s.getSourceItem()).collect(
//                Collectors.toList())).contains(rentPreviewItem);
        assertThat(depositPreviewItem.getTerms()).hasSize(1);

    }

    @Test
    public void createTermsIfNeededForTheItemInterval_works() throws Exception {

        LocalDate startDate = new LocalDate(2020,1,1);
        LocalDate endDate = new LocalDate(2020,12,31);

        // given
        LeaseAmendmentService service = new LeaseAmendmentService();
        Lease oxfLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseItem leaseItem = oxfLease.newItem(
                LeaseItemType.RENT_DISCOUNT,
                LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbDiscount.findUsing(serviceRegistry),
                InvoicingFrequency.MONTHLY_IN_ADVANCE,
                PaymentMethod.DIRECT_DEBIT,
                startDate);
        leaseItem.setEndDate(endDate);
        assertThat(leaseItem.getTerms()).isEmpty();

        // when no terms
        service.createTermsIfNeededForTheItemInterval(leaseItem);
        // then
        assertThat(leaseItem.getTerms()).isEmpty();

        // when
        final LeaseTermForIndexable term1 = (LeaseTermForIndexable) leaseItem.newTerm(startDate, new LocalDate(2020, 2, 15));
        final BigDecimal baseValue = new BigDecimal("2000.00");
        term1.setBaseValue(baseValue);
        final LeaseTermForIndexable term2 = (LeaseTermForIndexable) term1.createNext(term1.getEndDate().plusDays(1), new LocalDate(2020, 5, 22));
        assertThat(leaseItem.getTerms()).hasSize(2);
        assertThat(term1.getBaseValue()).isEqualTo(baseValue);
        assertThat(term2.getBaseValue()).isEqualTo(baseValue);
        assertThat(term1.getNext()).isEqualTo(term2);

        service.createTermsIfNeededForTheItemInterval(leaseItem);

        // then
        assertThat(leaseItem.getTerms()).hasSize(3);
        final LeaseTermForIndexable lastTerm = (LeaseTermForIndexable) leaseItem.getTerms().last();
        assertThat(lastTerm.getBaseValue()).isEqualTo(baseValue);
        assertThat(lastTerm.getStartDate()).isEqualTo(term2.getEndDate().plusDays(1));
        assertThat(lastTerm.getEndDate()).isNull();
        assertThat(lastTerm.getPrevious()).isEqualTo(term2);
        assertThat(term2.getNext()).isEqualTo(lastTerm);

    }

    @Inject LeaseAmendmentService leaseAmendmentService;

    @Inject LeaseItemSourceRepository leaseItemSourceRepository;

}