package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class TurnoverRentRuleHelperTest {

    @Test
    public void testIsValid() {
        Assert.assertFalse(new TurnoverRentRuleHelper("50000;5").isValid());
        Assert.assertFalse(new TurnoverRentRuleHelper(null).isValid());
        Assert.assertFalse(new TurnoverRentRuleHelper("").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("5").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("5.00").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("50000;5;7").isValid());
    }

    @Test
    public void testCalculateRent() throws Exception {
        test(null, 1000000d, 0.0);
        test("0", 1000000d, 0.0);
        test("5", 1000000d, 50000d);
        test("500000;5;750000;6;7", 500000d, 25000d);
        test("500000;5;750000;6;7", 600000d, 31000d);
        test("500000;5;750000;6;7", 1000000d, 57500d);
        test("", 1000000d, 0d);
        test("500000;5;750000;6;7", 0.0, 0.0);
        test("500000;5;750000;6;7", null, 0.0);

    }

    protected void test(String rule, Double in, Double out) {
        Assert.assertThat(new TurnoverRentRuleHelper(rule).calculateRent(in == null ? null : BigDecimal.valueOf(in)), Is.is(BigDecimal.valueOf(out).setScale(2)));
    }

}
