package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermRepository;

public class LeaseAmendmentService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock LeaseItemRepository mockLeaseItemRepository;

    @Mock LeaseTermRepository mockLeaseTermRepository;

    @Test
    @Ignore
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
        Assertions.assertThat(rentDiscountTerm.getValue()).isEqualTo(new BigDecimal("-50.28"));

    }

}