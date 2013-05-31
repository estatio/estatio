package org.estatio.dom.lease;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LeaseTermForTurnoverRentTest {

    
    private LeaseTermForTurnoverRent term;

    @Before
    public void setup()
    {
        term = new LeaseTermForTurnoverRent();
        term.setTurnoverRentRule("7.00");
        
    }
    
    
    @Test
    public void testValidation() {
        Assert.assertNull(term.validateTurnoverRentRule("7.00"));
        
        
    }

}
