package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForFixedTest {

    public static class ValidateChangeValue extends LeaseTermForFixedTest {

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
            leaseTerm = newLeaseTerm(LeaseItemType.DISCOUNT);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(-1))).isNull();
        }

        @Test
        public void whenDiscountAndZero() {
            leaseTerm = newLeaseTerm(LeaseItemType.DISCOUNT);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.ZERO)).isNull();
        }

        @Test
        public void whenNotDiscountAndPositive() {
            leaseTerm = newLeaseTerm(LeaseItemType.ENTRY_FEE);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(+1))).isNull();
        }

        @Test
        public void whenDiscountAndPositive() {
            leaseTerm = newLeaseTerm(LeaseItemType.DISCOUNT);
            assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(+1))).isEqualTo("Discount should be negative or zero");
        }

    }
}