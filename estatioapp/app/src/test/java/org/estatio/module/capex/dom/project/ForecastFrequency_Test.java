package org.estatio.module.capex.dom.project;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class ForecastFrequency_Test {

    @Test
    public void getStartDateFor_works() {

        final LocalDate expectedStartDateQ1 = new LocalDate(2020, 1, 1);
        final LocalDate expectedStartDateQ2 = new LocalDate(2020, 4, 1);

        // when, then
        LocalDate date = new LocalDate(2020, 1, 1);
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getStartDateFor(date)).isEqualTo(
                expectedStartDateQ1);
        date = new LocalDate(2020, 1, 2);
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getStartDateFor(date)).isEqualTo(
                expectedStartDateQ1);
        date = new LocalDate(2020, 3, 31);
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getStartDateFor(date)).isEqualTo(
                expectedStartDateQ1);
        date = new LocalDate(2020, 4, 1);
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getStartDateFor(date)).isEqualTo(
                expectedStartDateQ2);
        date = new LocalDate(2020, 6, 30);
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getStartDateFor(date)).isEqualTo(
                expectedStartDateQ2);

        // etc...
        // This is just to convince ourselves here. See  org.estatio.module.lease.dom.util.CalendarUtils_Test


    }
}