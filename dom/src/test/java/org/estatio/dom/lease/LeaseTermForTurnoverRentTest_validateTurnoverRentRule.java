package org.estatio.dom.lease;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LeaseTermForTurnoverRentTest_validateTurnoverRentRule {

    
    private LeaseTermForTurnoverRent term;

    @Before
    public void setup()
    {
        term = new LeaseTermForTurnoverRent();
        term.setTurnoverRentRule("7.00");
    }
    
    
    @Test
    public void whenReturnsNull() {
        Assert.assertNull(term.validateTurnoverRentRule("7.00"));
    }

}
