package org.estatio.capex.dom.util;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

public class PeriodUtilTest {

    @Test
    public void yearFromPeriod_success() throws Exception {

        // given
        String period;

        // when
        period = "F2017M01";
        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("2016-07-01/2017-07-01");

        // when
        period = "2017Q1";
        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("2017-01-01/2018-01-01");

    }

    @Test
    public void yearFromPeriod_fail() throws Exception {

        // given
        String period;

        // when
        period = "f2017M01";
        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("----------/----------");

        // when
        period = "201Q1";
        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("----------/----------");

    }

    @Test
    public void periodFromInterval_success() throws Exception {

        // given
        LocalDate startDate;
        LocalDate endDate;
        LocalDateInterval interval;

        // when
        startDate = new LocalDate(2017,01,01);
        endDate = new LocalDate(2017, 12,31);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isEqualTo("2017");

        // when
        startDate = new LocalDate(2016,07,01);
        endDate = new LocalDate(2017, 06,30);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isEqualTo("F2017");
    }

    @Test
    public void periodFromInterval_fail() throws Exception {

        // given
        LocalDate startDate;
        LocalDate endDate;
        LocalDateInterval interval;

        // when
        startDate = new LocalDate(2017,01,02);
        endDate = new LocalDate(2017, 12,31);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isNull();

        // when
        startDate = new LocalDate(2016,07,01);
        endDate = new LocalDate(2017, 06,29);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isNull();
    }

    @Test
    public void isValidPeriod() throws Exception {

        // given
        String period;

        // when
        period = "f2017M01";

        // then
        Assertions.assertThat(PeriodUtil.isValidPeriod(period)).isEqualTo(false);

        // when
        period = "F2017M01";

        // then
        Assertions.assertThat(PeriodUtil.isValidPeriod(period)).isEqualTo(true);

    }

}