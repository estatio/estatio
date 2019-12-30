package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

public class TurnoverAggregationService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock TurnoverRepository mockTurnoverRepo;

    @Test
    public void aggregateForPeriod_works() {

        // given
        Turnover t1 = new Turnover();
        t1.setGrossAmount(new BigDecimal("123.00"));
        t1.setNetAmount(new BigDecimal("111.11"));
        Turnover t2 = new Turnover();
        t2.setGrossAmount(new BigDecimal("0.45"));
        Turnover tpy1 = new Turnover();
        tpy1.setGrossAmount(new BigDecimal("100.23"));
        tpy1.setNetAmount(new BigDecimal("99.99"));

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override List<Turnover> turnoversToAggregate(
                    final TurnoverAggregateForPeriod turnoverAggregateForPeriod, final boolean prevYear) {
                return prevYear ? Arrays.asList(tpy1) : Arrays.asList(t1, t2);
            }
        };

        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();
        final Occupancy occupancy = new Occupancy();
        final LocalDate aggregationDate = new LocalDate(2019, 1, 1);
        TurnoverAggregation aggregation = new TurnoverAggregation(occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY, new Currency());
        aggregateForPeriod.setAggregation(aggregation);
        aggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_2M);

        // when
        service.aggregateForPeriod(aggregateForPeriod);

        // then
        Assertions.assertThat(aggregateForPeriod.getTurnoverCount()).isEqualTo(2);
        Assertions.assertThat(aggregateForPeriod.getTurnoverCountPreviousYear()).isEqualTo(1);

        Assertions.assertThat(aggregateForPeriod.getGrossAmount()).isEqualTo(new BigDecimal("123.45"));
        Assertions.assertThat(aggregateForPeriod.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        Assertions.assertThat(aggregateForPeriod.getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("100.23"));
        Assertions.assertThat(aggregateForPeriod.getNetAmountPreviousYear()).isEqualTo(new BigDecimal("99.99"));

        Assertions.assertThat(aggregateForPeriod.isNonComparableThisYear()).isEqualTo(false);
        Assertions.assertThat(aggregateForPeriod.isNonComparablePreviousYear()).isEqualTo(false);
        Assertions.assertThat(aggregateForPeriod.isComparable()).isEqualTo(false);

    }

    @Test
    public void containsNonComparableTurnover_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        List<Turnover> turnoverList = Arrays.asList();
        // then
        Assertions.assertThat(service.containsNonComparableTurnover(turnoverList)).isFalse();

        // when
        Turnover t1 = new Turnover();
        t1.setNonComparable(false);
        turnoverList = Arrays.asList(t1);
        // then
        Assertions.assertThat(service.containsNonComparableTurnover(turnoverList)).isFalse();

        // when
        Turnover t2 = new Turnover();
        t2.setNonComparable(true);
        turnoverList = Arrays.asList(t1, t2);
        // then
        Assertions.assertThat(service.containsNonComparableTurnover(turnoverList)).isTrue();

    }

    @Test
    public void isComparable_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when, then
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 3, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 1, 2, false, false)).isFalse();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 1, false, false)).isFalse();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, true, false)).isFalse();
        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, false, true)).isFalse();

        Assertions.assertThat(service.isComparable(AggregationPeriod.P_2M, 0, 2, false, true)).isFalse();
    }
}