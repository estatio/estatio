package org.estatio.module.turnover.imports;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TurnoverImport_Test {

    @Test
    public void getAmountDivPercentage() {

        // given
        TurnoverImport turnoverImport = new TurnoverImport();
        // when
        turnoverImport.setNetAmountPreviousYear(new BigDecimal("1000.00"));
        turnoverImport.setGrossAmountPreviousYear(new BigDecimal("1200.00"));
        // then
        Assertions.assertThat(turnoverImport.getNetAmountDivPercentage()).isEqualTo(new BigDecimal("-100"));
        Assertions.assertThat(turnoverImport.getGrossAmountDivPercentage()).isEqualTo(new BigDecimal("-100"));

        // and when
        turnoverImport.setNetAmount(new BigDecimal("1234.56"));
        turnoverImport.setGrossAmount(new BigDecimal("1481.47"));
        turnoverImport.setNetAmountPreviousYear(null);
        turnoverImport.setGrossAmountPreviousYear(null);
        // then
        Assertions.assertThat(turnoverImport.getNetAmountDivPercentage()).isEqualTo(new BigDecimal("100"));
        Assertions.assertThat(turnoverImport.getGrossAmountDivPercentage()).isEqualTo(new BigDecimal("100"));

        // and when
        turnoverImport.setNetAmountPreviousYear(new BigDecimal("1234.56"));
        turnoverImport.setGrossAmountPreviousYear(new BigDecimal("1481.47"));
        // then
        Assertions.assertThat(turnoverImport.getNetAmountDivPercentage()).isEqualTo(new BigDecimal("0"));
        Assertions.assertThat(turnoverImport.getGrossAmountDivPercentage()).isEqualTo(new BigDecimal("0"));

        // and when
        turnoverImport.setNetAmountPreviousYear(new BigDecimal("1000.00"));
        turnoverImport.setGrossAmountPreviousYear(new BigDecimal("1200.00"));
        // then
        Assertions.assertThat(turnoverImport.getNetAmountDivPercentage()).isEqualTo(new BigDecimal("23"));
        Assertions.assertThat(turnoverImport.getGrossAmountDivPercentage()).isEqualTo(new BigDecimal("23"));

        // and when
        turnoverImport.setNetAmountPreviousYear(new BigDecimal("1234.56"));
        turnoverImport.setNetAmount(new BigDecimal("1000.00"));
        turnoverImport.setGrossAmountPreviousYear(new BigDecimal("1481.47"));
        turnoverImport.setGrossAmount(new BigDecimal("1200.00"));
        // then
        Assertions.assertThat(turnoverImport.getNetAmountDivPercentage()).isEqualTo(new BigDecimal("-19"));
        Assertions.assertThat(turnoverImport.getGrossAmountDivPercentage()).isEqualTo(new BigDecimal("-19"));

    }

    @Test
    public void netAmountToUse_works() throws Exception {

        // given
        TurnoverImport turnoverImport = new TurnoverImport();
        // when nothing set, then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isNull();

        // when gross amount null
        turnoverImport.setVatPercentage(new BigDecimal("25"));
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isNull();

        // when net amount calculated
        turnoverImport.setGrossAmount(new BigDecimal("500.55"));
        turnoverImport.setVatPercentage(new BigDecimal("25"));
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isEqualTo(new BigDecimal("400.44"));

        // when net amount calculated and rounded
        turnoverImport.setGrossAmount(new BigDecimal("12345.67"));
        turnoverImport.setVatPercentage(new BigDecimal("25"));
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isEqualTo(new BigDecimal("9876.54"));

        // when gross amount zero
        turnoverImport.setGrossAmount(new BigDecimal("0.00"));
        turnoverImport.setVatPercentage(new BigDecimal("25"));
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isEqualTo(new BigDecimal("0.00"));

        // when net amount set
        turnoverImport.setGrossAmount(new BigDecimal("12345.67"));
        turnoverImport.setNetAmount(new BigDecimal("123.45"));
        turnoverImport.setVatPercentage(new BigDecimal("25"));
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isEqualTo(new BigDecimal("123.45"));

        // when net amount set to 0
        turnoverImport.setGrossAmount(new BigDecimal("12345.67"));
        turnoverImport.setNetAmount(new BigDecimal("0.00"));
        turnoverImport.setVatPercentage(new BigDecimal("25"));
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isEqualTo(new BigDecimal("0.00"));

        // when no vat percentage set
        turnoverImport.setGrossAmount(new BigDecimal("12345.67"));
        turnoverImport.setNetAmount(null);
        turnoverImport.setVatPercentage(null);
        // then
        Assertions.assertThat(turnoverImport.netAmountToUse()).isNull();

    }
}