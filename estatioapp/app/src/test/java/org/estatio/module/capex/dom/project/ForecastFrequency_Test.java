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

    @Test
    public void getNextStartDateFor_works() {

        // given
        LocalDate date0 = new LocalDate(2019,12,31);
        LocalDate date1 = new LocalDate(2020,1,1);
        LocalDate date2 = new LocalDate(2020,1,2);
        LocalDate date3 = new LocalDate(2020,1,3);
        LocalDate date4 = new LocalDate(2020,3,31);
        LocalDate date5 = new LocalDate(2020,4,1);

        // when, then
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getNextStartDateFor(date0)).isEqualTo(new LocalDate(2020, 1,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getNextStartDateFor(date1)).isEqualTo(new LocalDate(2020, 4,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getNextStartDateFor(date2)).isEqualTo(new LocalDate(2020, 4,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getNextStartDateFor(date3)).isEqualTo(new LocalDate(2020, 4,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getNextStartDateFor(date4)).isEqualTo(new LocalDate(2020, 4,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getNextStartDateFor(date5)).isEqualTo(new LocalDate(2020, 7,1 ));

    }

    @Test
    public void getPreviousStartDateFor_works() {

        // given
        LocalDate date0 = new LocalDate(2019,12,31);
        LocalDate date1 = new LocalDate(2020,1,1);
        LocalDate date2 = new LocalDate(2020,1,2);
        LocalDate date3 = new LocalDate(2020,1,3);
        LocalDate date4 = new LocalDate(2020,3,31);
        LocalDate date5 = new LocalDate(2020,4,1);

        // when, then
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getPreviousStartDateFor(date0)).isEqualTo(new LocalDate(2019, 7,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getPreviousStartDateFor(date1)).isEqualTo(new LocalDate(2019, 10,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getPreviousStartDateFor(date2)).isEqualTo(new LocalDate(2019, 10,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getPreviousStartDateFor(date3)).isEqualTo(new LocalDate(2019, 10,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getPreviousStartDateFor(date4)).isEqualTo(new LocalDate(2019, 10,1 ));
        Assertions.assertThat(ForecastFrequency.QUARTERLY.getPreviousStartDateFor(date5)).isEqualTo(new LocalDate(2020, 1,1 ));

    }
}