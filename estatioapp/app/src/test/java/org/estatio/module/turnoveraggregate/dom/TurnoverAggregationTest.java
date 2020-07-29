package org.estatio.module.turnoveraggregate.dom;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class TurnoverAggregationTest {

    @Test
    public void calculationPeriod_works() {

        // given
        TurnoverAggregation aggregation = new TurnoverAggregation();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        aggregation.setDate(aggregationDate);
        // when, then
        Assertions.assertThat(aggregation.calculationPeriod().toString()).isEqualTo("2018-02-01/2020-01-02");
        Assertions.assertThat(aggregation.calculationPeriod().endDate()).isEqualTo(aggregationDate);
    }
}