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
}