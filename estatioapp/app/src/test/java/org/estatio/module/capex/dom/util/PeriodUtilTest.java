package org.estatio.module.capex.dom.util;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

public class PeriodUtilTest {

    @Test
    public void coda_year_syntax() throws Exception {

        // given
        String period = "2019/1"; // as per Coda

        // then this produces garbage
        // really, should put a guard around this to prevent this from being accepted.
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("2019-01-01/2020-01-01");

    }

    @Test
    public void financial_year_syntax() throws Exception {

        // given
        String period = "F2019"; // represents the 2018/2019 financial year

        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("2018-07-01/2019-07-01");

    }

    @Test
    public void financial_year_syntax_2020_and_furtheron() throws Exception {

        // given
        String financialYear2020 = "F2020"; // represents the 2019/2020 financial year which is 12 months (this is the single financial year having this. After 2020 the financial year equals the calendar year)
        String financialYear2021 = "F2021"; // represents the 2021 financial year which is 6 months
        String financialYear2022 = "F2022"; // the 2022 financial year does not exist because since 1-1-2021 financial and calendar year are in sync

        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(financialYear2020).toString())
                .isEqualTo("2019-07-01/2020-07-01");
        Assertions.assertThat(PeriodUtil.yearFromPeriod(financialYear2021).toString())
                .isEqualTo("2020-07-01/2021-01-01");
        Assertions.assertThat(PeriodUtil.yearFromPeriod(financialYear2022).toString())
                .isEqualTo("----------/----------");

    }

    @Test
    public void financial_year_syntax_with_month() throws Exception {

        // given
        String period = "F2019/1"; // represents the 2018/2019 financial year

        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("2018-07-01/2019-07-01");

    }

    @Test
    public void financial_year_syntax_with_month_4() throws Exception {

        // given
        String period = "F2019/4"; // represents the 2018/2019 financial year

        // then
        Assertions.assertThat(PeriodUtil.yearFromPeriod(period).toString())
                .isEqualTo("2018-07-01/2019-07-01");

    }

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

        // when
        period = "F2022";
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

        // when
        startDate = new LocalDate(2020,07,01);
        endDate = new LocalDate(2020, 12,31);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isEqualTo("F2021");

        // when
        startDate = new LocalDate(2020,07,01);
        endDate = new LocalDate(2020, 12,31);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isEqualTo("F2021");

        // when
        startDate = new LocalDate(2020,07,01);
        endDate = new LocalDate(2021, 6,30);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isNull();

        // when
        startDate = new LocalDate(2021,01,01);
        endDate = new LocalDate(2021, 12,31);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isEqualTo("2021");

        // when
        startDate = new LocalDate(2021,07,01);
        endDate = new LocalDate(2022, 6,30);
        interval = new LocalDateInterval(startDate, endDate);

        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(interval))
                .isNull();
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
        Assertions.assertThat(PeriodUtil.reasonInvalidPeriod(period)).isEqualTo("Not a valid period; use four digits of the year with optional prefix F for a financial year (for example: F2017)");

        // when
        period = "F2017M01";

        // then
        Assertions.assertThat(PeriodUtil.isValidPeriod(period)).isEqualTo(true);
        Assertions.assertThat(PeriodUtil.reasonInvalidPeriod(period)).isNull();

        // when
        period = "";
        // then
        Assertions.assertThat(PeriodUtil.isValidPeriod(period)).isEqualTo(false);

        // when
        period = null;
        // then
        Assertions.assertThat(PeriodUtil.isValidPeriod(period)).isEqualTo(false);

    }

    @Test
    public void nine_months_financial_year_from_interval_works() throws Exception {

        // given
        LocalDateInterval localDateInterval;

        // when
        localDateInterval = LocalDateInterval.including(new LocalDate(2019,7,1), new LocalDate(2020,6,30));
        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(localDateInterval)).isEqualTo("F2020");

        // when
        localDateInterval = LocalDateInterval.including(new LocalDate(2020,7,1), new LocalDate(2020,12,31));
        // then
        Assertions.assertThat(PeriodUtil.periodFromInterval(localDateInterval)).isEqualTo("F2021");


    }


}