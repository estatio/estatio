package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocLine_getEffectiveDocValue_Test {

    private CodaDocLine codaDocLine;

    @Before
    public void setUp() throws Exception {
        codaDocLine = new CodaDocLine();
        codaDocLine.setDocValue(bd("101.23"));
    }

    @Test
    public void given_analysis_and_proforma_then_zero() throws Exception {
        // given
        codaDocLine.setLineType(LineType.ANALYSIS);
        codaDocLine.setAccountCodeEl5(CodaDocLine.EL5_FOR_PRO_FORMA);

        // when, then
        assertThat(codaDocLine.getEffectiveDocValue()).isEqualTo(bd("0.00"));
    }

    @Test
    public void given_analysis_and_not_proforma() throws Exception {
        // given
        codaDocLine.setLineType(LineType.ANALYSIS);
        codaDocLine.setAccountCodeEl5("ABCDEF");

        // when, then
        assertThat(codaDocLine.getEffectiveDocValue()).isEqualTo(codaDocLine.getDocValue());
    }

    @Test
    public void given_summary_and_proforma() throws Exception {
        // given
        codaDocLine.setLineType(LineType.SUMMARY);
        codaDocLine.setAccountCodeEl5(CodaDocLine.EL5_FOR_PRO_FORMA);

        // when, then
        assertThat(codaDocLine.getEffectiveDocValue()).isEqualTo(codaDocLine.getDocValue());
    }

    private static BigDecimal bd(final String val) {
        return new BigDecimal(val);
    }

}