package org.estatio.dom.utils;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class FinancialAmountUtilTest {

    @Test
    public void subtractHandlingNulls() throws Exception {

        BigDecimal amount;
        BigDecimal amountToSubtract;
        BigDecimal result;

        // given
        amount = new BigDecimal("100");
        amountToSubtract = new BigDecimal("10.11");
        // when
        result = FinancialAmountUtil.subtractHandlingNulls(amount, amountToSubtract);
        // then
        Assertions.assertThat(result).isEqualTo(new BigDecimal("89.89"));

        // and when
        amount = new BigDecimal("100");
        amountToSubtract = null;
        // when
        result = FinancialAmountUtil.subtractHandlingNulls(amount, amountToSubtract);
        // then
        Assertions.assertThat(result).isEqualTo(new BigDecimal("100"));

        // and when
        amount = null;
        amountToSubtract = new BigDecimal("10.11");
        // when
        result = FinancialAmountUtil.subtractHandlingNulls(amount, amountToSubtract);
        // then
        Assertions.assertThat(result).isEqualTo(new BigDecimal("-10.11"));

        // and when
        amount = null;
        amountToSubtract = null;
        // when
        result = FinancialAmountUtil.subtractHandlingNulls(amount, amountToSubtract);
        // then
        Assertions.assertThat(result).isNull();

    }

    @Test
    public void addHandlingNulls() throws Exception {

        BigDecimal amount;
        BigDecimal amountToAdd;
        BigDecimal result;

        // given
        amount = new BigDecimal("100");
        amountToAdd = new BigDecimal("10.11");
        // when
        result = FinancialAmountUtil.addHandlingNulls(amount, amountToAdd);
        // then
        Assertions.assertThat(result).isEqualTo(new BigDecimal("110.11"));

        // and when
        amount = new BigDecimal("100");
        amountToAdd = null;
        // when
        result = FinancialAmountUtil.addHandlingNulls(amount, amountToAdd);
        // then
        Assertions.assertThat(result).isEqualTo(new BigDecimal("100"));

        // and when
        amount = null;
        amountToAdd = new BigDecimal("10.11");
        // when
        result = FinancialAmountUtil.addHandlingNulls(amount, amountToAdd);
        // then
        Assertions.assertThat(result).isEqualTo(new BigDecimal("10.11"));

        // and when
        amount = null;
        amountToAdd = null;
        // when
        result = FinancialAmountUtil.addHandlingNulls(amount, amountToAdd);
        // then
        Assertions.assertThat(result).isNull();

    }

}