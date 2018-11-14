package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Util_subtract_Test {

    @Test
    public void both_non_null_positive_result() throws Exception {
        final BigDecimal m = new BigDecimal("10.10");
        final BigDecimal n = new BigDecimal( "1.01");
        final BigDecimal x = new BigDecimal( "9.09");

        assertThat(Util.subtract(m, n)).isEqualTo(x);
    }

    @Test
    public void both_non_null_negative_result() throws Exception {
        final BigDecimal m = new BigDecimal( "1.01");
        final BigDecimal n = new BigDecimal("10.10");
        final BigDecimal x = new BigDecimal("-9.09");

        assertThat(Util.subtract(m, n)).isEqualTo(x);
    }

    @Test
    public void m_is_null() throws Exception {
        final BigDecimal m = null;
        final BigDecimal n = new BigDecimal("1.01");

        assertThat(Util.subtract(m, n)).isNull();
    }

    @Test
    public void n_is_null() throws Exception {
        final BigDecimal m = new BigDecimal("10.10");
        final BigDecimal n = null;
        final BigDecimal x = new BigDecimal("10.10");

        assertThat(Util.subtract(m, n)).isEqualTo(x);
    }

    @Test
    public void both_are_null() throws Exception {
        final BigDecimal m = null;
        final BigDecimal n = null;
        final BigDecimal x = null;

        assertThat(Util.subtract(m, n)).isNull();
    }

}