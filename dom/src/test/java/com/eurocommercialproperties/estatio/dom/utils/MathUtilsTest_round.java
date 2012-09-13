package com.eurocommercialproperties.estatio.dom.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class MathUtilsTest_round {

    @Test
    public void roundUp() throws Exception {
        assertThat(new BigDecimal("4.6"), is(MathUtils.round(new BigDecimal("4.55"), 1)));
    }

    @Test
    public void roundDown() throws Exception {
        assertThat(new BigDecimal("4.5"), is(MathUtils.round(new BigDecimal("4.54"), 1)));
    }

    @Test
    public void noRounding() throws Exception {
        assertThat(new BigDecimal("4.54"), is(MathUtils.round(new BigDecimal("4.54"), 2)));
    }

}
