package org.estatio.dom.utils;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MathUtilsTest_isZeroOrNull {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testIsZeroOrNull() {
        Assert.assertTrue(MathUtils.isZeroOrNull(null));
        Assert.assertTrue(MathUtils.isZeroOrNull(BigDecimal.valueOf(0)));
        Assert.assertFalse(MathUtils.isZeroOrNull(BigDecimal.valueOf(100)));
        Assert.assertFalse(MathUtils.isNotZeroOrNull(null));
        Assert.assertFalse(MathUtils.isNotZeroOrNull(BigDecimal.valueOf(0)));
        Assert.assertTrue(MathUtils.isNotZeroOrNull(BigDecimal.valueOf(100)));
    }

}
