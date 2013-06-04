package org.estatio.dom.lease;

import org.junit.Assert;
import org.junit.Test;

public class TurnoverRentRuleHelperTest_isValid {

    @Test
    public void whenIsNot() {
        Assert.assertFalse(new TurnoverRentRuleHelper("50000;5").isValid());
        Assert.assertFalse(new TurnoverRentRuleHelper(null).isValid());
        Assert.assertFalse(new TurnoverRentRuleHelper("").isValid());
    }

    @Test
    public void whenIs() {
        Assert.assertTrue(new TurnoverRentRuleHelper("5").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("5.00").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("50000;5;7").isValid());
    }


}
