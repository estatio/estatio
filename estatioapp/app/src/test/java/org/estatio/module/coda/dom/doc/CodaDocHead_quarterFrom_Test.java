package org.estatio.module.coda.dom.doc;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_quarterFrom_Test {

    @Test
    public void normal_case() throws Exception {
        assertThatQuarterFrom("2018/1", "2018q1");
        assertThatQuarterFrom("2018/2", "2018q1");
        assertThatQuarterFrom("2018/3", "2018q1");
        assertThatQuarterFrom("2018/4", "2018q2");
        assertThatQuarterFrom("2018/5", "2018q2");
        assertThatQuarterFrom("2018/6", "2018q2");
        assertThatQuarterFrom("2018/7", "2018q3");
        assertThatQuarterFrom("2018/8", "2018q3");
        assertThatQuarterFrom("2018/9", "2018q3");
        assertThatQuarterFrom("2018/10", "2018q4");
        assertThatQuarterFrom("2018/11", "2018q4");
        assertThatQuarterFrom("2018/12", "2018q4");
    }
    @Test
    public void white_space() throws Exception {
        assertThatQuarterFrom(" 2018/1", "2018q1");
        assertThatQuarterFrom("2018 /1", "2018q1");
        assertThatQuarterFrom(" 2018 /1", "2018q1");
        assertThatQuarterFrom("2018/ 1", "2018q1");
        assertThatQuarterFrom("2018/1 ", "2018q1");
        assertThatQuarterFrom("2018/ 1 ", "2018q1");
        assertThatQuarterFrom(" 2018  /  1 ", "2018q1");
    }
    @Test
    public void exceptional_periods() throws Exception {
        assertThatQuarterFrom("2018/13", "2018q5");
        assertThatQuarterFrom("2018/14", "2018q5");
        assertThatQuarterFrom("2018/15", "2018q5");
        assertThatQuarterFrom("2018/16", "2018q6");
        assertThatQuarterFrom("2018/17", "2018q6");
        assertThatQuarterFrom("2018/18", "2018q6");
    }
    @Test
    public void too_big() throws Exception {
        assertThatQuarterFrom("2018/19", "2018q99");
        assertThatQuarterFrom("2018/99", "2018q99");
        assertThatQuarterFrom("2018/9999", "2018q99");
    }
    @Test
    public void garbage() throws Exception {
        assertThatQuarterFrom("2018x/1", "unknown");
        assertThatQuarterFrom("garbage", "unknown");
    }

    void assertThatQuarterFrom(final String codaPeriod, final String expected) {
        assertThat(CodaDocHead.quarterFrom(codaPeriod)).isEqualTo(expected);
    }
}