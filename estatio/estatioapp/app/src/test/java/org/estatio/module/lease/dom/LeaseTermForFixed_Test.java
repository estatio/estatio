package org.estatio.module.lease.dom;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForFixed_Test {

    public static class ValidateChangeValue extends LeaseTermForFixed_Test {

        private LeaseTermForFixed leaseTerm;
        LeaseItem leaseItem;

        @Before
        public void setUp() throws Exception {
        }

        private static LeaseTermForFixed newLeaseTerm(LeaseItemType leaseItemType) {
            LeaseItem leaseItem = new LeaseItem();
            LeaseTermForFixed leaseTerm = new LeaseTermForFixed();
            leaseTerm.setLeaseItem(leaseItem);
            leaseItem.setType(leaseItemType);
            return leaseTerm;
        }

        @Test
        public void whenDiscountAndNegative() {
            leaseTerm = newLeaseTerm(LeaseItemType.RENT_DISCOUNT_FIXED);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(-1))).isNull();
        }

        @Test
        public void whenDiscountAndZero() {
            leaseTerm = newLeaseTerm(LeaseItemType.RENT_DISCOUNT_FIXED);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.ZERO)).isNull();
        }

        @Test
        public void whenNotDiscountAndPositive() {
            leaseTerm = newLeaseTerm(LeaseItemType.ENTRY_FEE);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(+1))).isNull();
        }

        @Test
        public void whenDiscountAndPositive() {
            leaseTerm = newLeaseTerm(LeaseItemType.RENT_DISCOUNT_FIXED);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(+1))).isEqualTo("Discount should be negative or zero");
        }

    }
}