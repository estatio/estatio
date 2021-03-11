package org.estatio.module.turnover.dom;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

public class Turnover_Test {

    @Test
    public void getInterval_works() {

        // given
        Turnover turnover = new Turnover();
        turnover.setDate(new LocalDate(2020, 2,1));

        // when
        turnover.setFrequency(Frequency.DAILY);
        // then
        Assertions.assertThat(turnover.getInterval().toString()).isEqualTo("2020-02-01/2020-02-02");

        // when
        turnover.setFrequency(Frequency.MONTHLY);
        // then
        Assertions.assertThat(turnover.getInterval().toString()).isEqualTo("2020-02-01/2020-03-01");

        // when
        turnover.setFrequency(Frequency.YEARLY);
        // then
        Assertions.assertThat(turnover.getInterval().toString()).isEqualTo("2020-02-01/2021-02-01");
        
    }
}