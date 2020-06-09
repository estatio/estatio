package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock LeaseItemRepository mockLeaseItemRepository;

    @Mock LeaseTermRepository mockLeaseTermRepository;

    @Mock Lease mockLease;

    @Test
    public void createFixedDiscountItem_works() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2020, 3, 1);
        final BigDecimal value = new BigDecimal("123.45");
        final LeaseAgreementRoleTypeEnum landlord = LeaseAgreementRoleTypeEnum.LANDLORD;
        final Charge charge = new Charge();
        final InvoicingFrequency quarterlyInAdvance = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
        final PaymentMethod directDebit = PaymentMethod.DIRECT_DEBIT;

        final LeaseTermForFixed fixedTerm = new LeaseTermForFixed();

        final LeaseItem discountItem = new LeaseItem(){
            @Override public LeaseTerm newTerm(final LocalDate startDate, final LocalDate endDate) {
                return fixedTerm;
            }
        };

        LeaseAmendmentService service = new LeaseAmendmentService();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).newItem(LeaseItemType.RENT_DISCOUNT_FIXED,landlord, charge, quarterlyInAdvance, directDebit, startDate);
            will(returnValue(discountItem));
        }});

        // when
        service.createFixedDiscountItem(mockLease, landlord, charge, quarterlyInAdvance, directDebit, startDate, endDate, value);

        // then
        assertThat(discountItem.getEndDate()).isEqualTo(endDate);
        assertThat(fixedTerm.getValue()).isEqualTo(value);

    }


    @Test
    @Ignore("Too lazy ...?")
    public void createDiscountItemsAndTerms_works() {

        // given
        final LocalDate leaseStartDate = new LocalDate(2020, 1, 1);
        final LocalDate amendmentItemStartDate = new LocalDate(2020, 1, 10);
        final LocalDate amendmentItemEndDate = new LocalDate(2020, 2, 15);
        final BigDecimal currentRentTermValue = new BigDecimal("100.55");
        final BigDecimal discountPercentage = new BigDecimal("50.00");

        LeaseAmendmentService service = new LeaseAmendmentService();

        Lease lease = new Lease();
        lease.leaseItemRepository = mockLeaseItemRepository;
        lease.setStartDate(leaseStartDate);

        LeaseAmendmentItemForDiscount amendmentItem = new LeaseAmendmentItemForDiscount();
        amendmentItem.setApplicableTo("RENT");
        amendmentItem.setStartDate(amendmentItemStartDate);
        amendmentItem.setEndDate(amendmentItemEndDate);
        amendmentItem.setDiscountPercentage(discountPercentage);

        LeaseItem rentItem = new LeaseItem(){
            @Override
            public BigDecimal valueForDate(final LocalDate date) {
                return currentRentTermValue;
            }
        };
        rentItem.setType(LeaseItemType.RENT);
        rentItem.setStartDate(leaseStartDate);
        rentItem.setLease(lease);
        lease.getItems().add(rentItem);

        LeaseItem discountItemForRent = new LeaseItem();
        discountItemForRent.setType(LeaseItemType.RENT_DISCOUNT_FIXED);
        discountItemForRent.leaseTermRepository = mockLeaseTermRepository;
        discountItemForRent.setStartDate(amendmentItemStartDate);

        LeaseTermForFixed rentDiscountTerm = new LeaseTermForFixed();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseItemRepository).newLeaseItem(lease, LeaseItemType.RENT_DISCOUNT_FIXED, null, null, InvoicingFrequency.FIXED_IN_ARREARS, null, amendmentItemStartDate);
            will(returnValue(discountItemForRent));
            oneOf(mockLeaseTermRepository).newLeaseTerm(discountItemForRent, null, amendmentItemStartDate, amendmentItemEndDate);
            will(returnValue(rentDiscountTerm));
        }});

        // when
        service.applyDiscount(lease, amendmentItem);

        // then
        assertThat(rentDiscountTerm.getValue()).isEqualTo(new BigDecimal("-50.28"));

    }

    @Test
    public void findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate_when_no_items_on_lease() throws Exception {

        // given
        LeaseAmendmentService service = new LeaseAmendmentService();
        Lease lease = new Lease();

        // when
        final LeaseAmendmentType.Tuple<InvoicingFrequency, InvoicingFrequency> tuple = service
                .findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(lease, LeaseAmendmentType.COVID_FRA_50_PERC);

        // then
        assertThat(tuple).isNull();

    }

    @Test
    public void findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate_when_applicable_items_on_lease() throws Exception {

        // given
        LeaseAmendmentService service = new LeaseAmendmentService();
        LeaseItem rentItem = new LeaseItem(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(LeaseAmendmentType.COVID_FRA_50_PERC.getDiscountStartDate(), LeaseAmendmentType.COVID_FRA_50_PERC.getFrequencyChangeEndDate());
            }
        };
        rentItem.setType(LeaseItemType.RENT);
        rentItem.setInvoicingFrequency(LeaseAmendmentType.COVID_FRA_50_PERC.getFrequencyChanges().get(1).oldValue);
        Lease lease = new Lease(){
            @Override public SortedSet<LeaseItem> getItems() {
                return new TreeSet<>(Arrays.asList(
                        rentItem
                ));
            }
        };

        // when
        final LeaseAmendmentType.Tuple<InvoicingFrequency, InvoicingFrequency> tuple = service
                .findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(lease, LeaseAmendmentType.COVID_FRA_50_PERC);
        // then
        assertThat(tuple).isEqualTo(LeaseAmendmentType.COVID_FRA_50_PERC.getFrequencyChanges().get(1));

    }

    @Mock ChargeRepository mockChargeRepository;

    @Test
    public void chargeDerivedFromAmendmentTypeAndChargeSourceItem_works() throws Exception {

        // given
        LeaseAmendmentService service = new LeaseAmendmentService();
        service.chargeRepository = mockChargeRepository;
        Charge sourceCharge1 = new Charge();
        sourceCharge1.setReference("6001");
        Charge sourceCharge2 = new Charge();
        sourceCharge2.setReference("6002");
        Charge sourceCharge3 = new Charge();
        sourceCharge3.setReference("6031");
        Charge sourceCharge4 = new Charge();
        sourceCharge4.setReference("6032");
        Charge unmentionedCharge = new Charge();
        unmentionedCharge.setReference("xxxx");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockChargeRepository).findByReference(LeaseAmendmentType.COVID_ITA_100_PERC_1M.getChargeReferenceForDiscountItem().get(0).newValue);
            oneOf(mockChargeRepository).findByReference(LeaseAmendmentType.COVID_ITA_100_PERC_1M.getChargeReferenceForDiscountItem().get(1).newValue);
            oneOf(mockChargeRepository).findByReference(LeaseAmendmentType.COVID_ITA_100_PERC_1M.getChargeReferenceForDiscountItem().get(2).newValue);
            oneOf(mockChargeRepository).findByReference(LeaseAmendmentType.COVID_ITA_100_PERC_1M.getChargeReferenceForDiscountItem().get(3).newValue);
            oneOf(mockChargeRepository).findByReference(LeaseAmendmentType.COVID_ITA_100_PERC_1M.getChargeReferenceForDiscountItem().get(4).newValue);
        }});

        // when
        service.chargeDerivedFromAmendmentTypeAndChargeSourceItem(sourceCharge1, LeaseAmendmentType.COVID_ITA_100_PERC_1M);
        service.chargeDerivedFromAmendmentTypeAndChargeSourceItem(sourceCharge2, LeaseAmendmentType.COVID_ITA_100_PERC_1M);
        service.chargeDerivedFromAmendmentTypeAndChargeSourceItem(sourceCharge3, LeaseAmendmentType.COVID_ITA_100_PERC_1M);
        service.chargeDerivedFromAmendmentTypeAndChargeSourceItem(sourceCharge4, LeaseAmendmentType.COVID_ITA_100_PERC_1M);
        service.chargeDerivedFromAmendmentTypeAndChargeSourceItem(unmentionedCharge, LeaseAmendmentType.COVID_ITA_100_PERC_1M);

    }

}