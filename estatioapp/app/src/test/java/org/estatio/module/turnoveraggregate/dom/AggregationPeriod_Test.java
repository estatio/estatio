package org.estatio.module.turnoveraggregate.dom;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class AggregationPeriod_Test {

    @Test
    public void periodStartDateFor() {

        // given
        LocalDate date = new LocalDate(2019,1,2);

        // when, then
        Assertions.assertThat(AggregationPeriod.P_1M.periodStartDateFor(date)).isEqualTo(new LocalDate(2019,1,1));
        Assertions.assertThat(AggregationPeriod.P_2M.periodStartDateFor(date)).isEqualTo(new LocalDate(2018,12,1));
        Assertions.assertThat(AggregationPeriod.P_3M.periodStartDateFor(date)).isEqualTo(new LocalDate(2018,11,1));
        Assertions.assertThat(AggregationPeriod.P_6M.periodStartDateFor(date)).isEqualTo(new LocalDate(2018,8,1));
        Assertions.assertThat(AggregationPeriod.P_9M.periodStartDateFor(date)).isEqualTo(new LocalDate(2018,5,1));
        Assertions.assertThat(AggregationPeriod.P_12M.periodStartDateFor(date)).isEqualTo(new LocalDate(2018,2,1));
    }
}