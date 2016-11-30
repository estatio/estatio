package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Fraction_Test {

    @Test
    public void testFractionOf() throws Exception {

        //given, when
        BigDecimal value = new BigDecimal("12000.66");

        // then
        assertThat(Fraction.M1.fractionOf(value)).isEqualTo(new BigDecimal("1000.06"));
        assertThat(Fraction.M2.fractionOf(value)).isEqualTo(new BigDecimal("2000.11"));
        assertThat(Fraction.M3.fractionOf(value)).isEqualTo(new BigDecimal("3000.17"));
        assertThat(Fraction.M6.fractionOf(value)).isEqualTo(new BigDecimal("6000.33"));
        assertThat(Fraction.MANUAL.fractionOf(value)).isEqualTo(new BigDecimal("12000.66"));

    }
}