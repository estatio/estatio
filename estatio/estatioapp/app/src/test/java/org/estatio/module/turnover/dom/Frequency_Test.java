package org.estatio.module.turnover.dom;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class Frequency_Test {

    @Test
    public void hasStartDate() {

        // given
        LocalDate sat5Jan2019 = new LocalDate(2019,1,5);
        LocalDate sun6Jan2019 = new LocalDate(2019,1,6);
        LocalDate mon7Jan2019 = new LocalDate(2019,1,7);
        LocalDate start2019 = new LocalDate(2019,1,1);
        LocalDate startFeb2019 = new LocalDate(2019,2,1);

        // when, then
        Assertions.assertThat(Frequency.DAILY.hasStartDate(sat5Jan2019)).isFalse();
        Assertions.assertThat(Frequency.DAILY.hasStartDate(sun6Jan2019)).isFalse();
        Assertions.assertThat(Frequency.DAILY.hasStartDate(mon7Jan2019)).isTrue();

        Assertions.assertThat(Frequency.MONTHLY.hasStartDate(sat5Jan2019)).isFalse();
        Assertions.assertThat(Frequency.MONTHLY.hasStartDate(start2019)).isTrue();
        Assertions.assertThat(Frequency.MONTHLY.hasStartDate(startFeb2019)).isTrue();

        Assertions.assertThat(Frequency.YEARLY.hasStartDate(startFeb2019)).isFalse();
        Assertions.assertThat(Frequency.YEARLY.hasStartDate(start2019)).isTrue();

    }
}