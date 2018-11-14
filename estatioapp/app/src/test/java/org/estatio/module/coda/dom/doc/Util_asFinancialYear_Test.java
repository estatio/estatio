package org.estatio.module.coda.dom.doc;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Util_asFinancialYear_Test {

    @Test
    public void happy_cases() throws Exception {
        assertThat(Util.asFinancialYear("2019/1")).isEqualTo("F2019");
        assertThat(Util.asFinancialYear("2019/4")).isEqualTo("F2019");
        assertThat(Util.asFinancialYear("2019/999")).isEqualTo("F2019");

        assertThat(Util.asFinancialYear("2020/1")).isEqualTo("F2020");
        assertThat(Util.asFinancialYear("  2020/1")).isEqualTo("F2020");
    }

    @Test
    public void sad_cases() throws Exception {
        assertThat(Util.asFinancialYear("x020/1")).isNull();
        assertThat(Util.asFinancialYear("2020/")).isNull();
        assertThat(Util.asFinancialYear("2020")).isNull();
    }
}