package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class LeaseTermForFixedTest_validateChangeValue {

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
        assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(-1)), is(nullValue()));
    }

    @Test
    public void whenDiscountAndZero() {
        leaseTerm = newLeaseTerm(LeaseItemType.DISCOUNT);
        assertThat(leaseTerm.validateChangeValue(BigDecimal.ZERO), is(nullValue()));
    }

    @Test
    public void whenNotDiscountAndPositive() {
        leaseTerm = newLeaseTerm(LeaseItemType.ENTRY_FEE);
        assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(+1)), is(nullValue()));
    }

    @Test
    public void whenDiscountAndPositive() {
        leaseTerm = newLeaseTerm(LeaseItemType.DISCOUNT);
        assertThat(leaseTerm.validateChangeValue(BigDecimal.valueOf(+1)), is("Discount should be negative or zero"));
    }

}
