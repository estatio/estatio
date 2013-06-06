package org.estatio.dom.numerator;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

public class NumeratorTest_increment {

    private Numerator numerator;
    
    
    @Before
    public void setUp() throws Exception {
        numerator = new Numerator();
    }

    @Test
    public void happyCase() {
        assertEquals(BigInteger.ONE, numerator.increment());
        assertEquals(BigInteger.ONE, numerator.getLastIncrement());
    }

}
