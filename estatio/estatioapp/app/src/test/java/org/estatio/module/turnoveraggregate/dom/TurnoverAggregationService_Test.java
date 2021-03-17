package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregationService_Test {

    @Mock TurnoverAggregationRepository mockturnoverAggregationRepository;
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock TurnoverAggregation mockAggregation;

    @Test
    public void maintainTurnoverAggregationsForConfig_works() throws Exception {

        // when
        TurnoverAggregationService service = new TurnoverAggregationService();
        service.turnoverAggregationRepository = mockturnoverAggregationRepository;
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        AggregationAnalysisReportForConfig report = new AggregationAnalysisReportForConfig(config);
        final LocalDate removalDate = new LocalDate(2010, 1, 1);
        final LocalDate additionDate = new LocalDate(2020, 1, 1);
        final Currency currency = new Currency();
        config.setCurrency(currency);

        TurnoverAggregation aggregationToBeRemoved = new TurnoverAggregation();
        aggregationToBeRemoved.setDate(removalDate);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockturnoverAggregationRepository).findByTurnoverReportingConfig(config);
            will(returnValue(Arrays.asList(aggregationToBeRemoved)));
            oneOf(mockturnoverAggregationRepository).findOrCreate(config, additionDate, currency);
            oneOf(mockturnoverAggregationRepository).findUnique(config, removalDate);
            will(returnValue(mockAggregation));
            oneOf(mockAggregation).remove();
        }});

        // when
        report.getAggregationDates().add(additionDate);
        service.maintainTurnoverAggregationsForConfig(report);

    }

    @Test
    public void containsNonComparableTurnover_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        List<Turnover> turnoverList = Arrays.asList();
        // then
        assertThat(service.containsNonComparableTurnover(turnoverList)).isNull();

        // when
        Turnover t1 = new Turnover();
        t1.setNonComparable(false);
        turnoverList = Arrays.asList(t1);
        // then
        assertThat(service.containsNonComparableTurnover(turnoverList)).isFalse();

        // when
        Turnover t2 = new Turnover();
        t2.setNonComparable(true);
        turnoverList = Arrays.asList(t1, t2);
        // then
        assertThat(service.containsNonComparableTurnover(turnoverList)).isTrue();

    }

    @Test
    public void isComparable_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when, then
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 2, 2, false, false)).isTrue();
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 3, 2, false, false)).isTrue();
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 1, 2, false, false)).isFalse();
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 2, 1, false, false)).isFalse();
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 2, 2, true, false)).isFalse();
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 2, 2, false, true)).isFalse();

        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 0, 2, false, true)).isFalse();

        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, null, 2, false, true)).isFalse();
        assertThat(service.isComparableForPeriod(AggregationPeriod.P_2M, 0, null, false, true)).isFalse();

    }

    @Test
    public void isComparableToDate_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2019, 2, 1);

        // when, then
        assertThat(service.isComparableToDate(aggregationDate, 2, 2, false, false)).isTrue();
        assertThat(service.isComparableToDate(aggregationDate, 3, 2, false, false)).isTrue();
        assertThat(service.isComparableToDate(aggregationDate, 1, 2, false, false)).isFalse();
        assertThat(service.isComparableToDate(aggregationDate, 2, 1, false, false)).isFalse();
        assertThat(service.isComparableToDate(aggregationDate, 2, 2, true, false)).isFalse();
        assertThat(service.isComparableToDate(aggregationDate, 2, 2, false, true)).isFalse();

        assertThat(service.isComparableToDate(aggregationDate, 0, 2, false, true)).isFalse();

        assertThat(service.isComparableToDate(aggregationDate, null, 2, false, true)).isFalse();
        assertThat(service.isComparableToDate(aggregationDate, 0, null, false, true)).isFalse();
    }

    @Test
    public void getMinNumberOfTurnoversToDate_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when, then
        assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,1,1))).isEqualTo(1);
        assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,2,1))).isEqualTo(2);
        //etc
        assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,12,1))).isEqualTo(12);

    }

    @Test
    public void calculateAggregationForOther_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        TurnoverAggregation aggregation = new TurnoverAggregation();
        final LocalDate date = new LocalDate(2020, 1, 1);
        aggregation.setDate(date);

        List<Turnover> turnovers = new ArrayList<>();

        // when
        Turnover t1 = new Turnover();
        t1.setComments("t1 comment");
        t1.setDate(date.minusMonths(1));
        t1.setGrossAmount(new BigDecimal("1.23"));
        t1.setNetAmount(new BigDecimal("1.00"));
        turnovers.add(t1);

        Turnover t2 = new Turnover();
        t2.setComments("t2 comment");
        t2.setDate(date.minusMonths(2));
        t2.setGrossAmount(new BigDecimal("2.34"));
        turnovers.add(t2);

        Turnover t3 = new Turnover();
        t3.setComments("t3 comment");
        t3.setDate(date.minusMonths(11));
        turnovers.add(t3);

        Turnover t4 = new Turnover();
        t4.setComments("t4 comment");
        t4.setDate(date.minusMonths(12));
        turnovers.add(t4);

        assertThat(turnovers).hasSize(4);

        service.calculateAggregationForOther(aggregation, turnovers);

        // then
        assertThat(aggregation.getGrossAmount1MCY_1()).isEqualTo(t1.getGrossAmount());
        assertThat(aggregation.getNetAmount1MCY_1()).isEqualTo(t1.getNetAmount());
        assertThat(aggregation.getGrossAmount1MCY_2()).isEqualTo(t2.getGrossAmount());
        assertThat(aggregation.getNetAmount1MCY_2()).isNull();
        assertThat(aggregation.getComments12MCY()).isEqualTo("t1 comment | t2 comment | t3 comment");
        assertThat(aggregation.getComments12MPY()).isEqualTo("t4 comment");

    }

    @Test
    public void calculateTurnoverAggregateForPeriod_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        TurnoverAggregation aggregation = new TurnoverAggregation();
        aggregation.setDate(aggregationDate);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setFrequency(Frequency.MONTHLY);
        aggregation.setTurnoverReportingConfig(config);
        final TurnoverAggregateForPeriod afp = new TurnoverAggregateForPeriod();

        // when
        afp.setAggregationPeriod(AggregationPeriod.P_2M);
        List<Turnover> objects = prepareTestTurnovers(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregation, objects);

        // then
        assertAggregateForPeriod(afp, new BigDecimal("1"), new BigDecimal("0.50"),1,false,
                null, null,null, null, false);

        // when turnover values 0
        afp.setAggregationPeriod(AggregationPeriod.P_2M);
        objects = prepareTestTurnovers(LocalDateInterval.including(aggregationDate, aggregationDate));
        objects.forEach(t->{
            t.setGrossAmount(BigDecimal.ZERO);
            t.setNetAmount(BigDecimal.ZERO);
        });
        service.calculateTurnoverAggregateForPeriod(afp, aggregation, objects);

        // then
        assertAggregateForPeriod(afp, new BigDecimal("0"), new BigDecimal("0"),0,false,
                null, null,null, null, false);

        // and when only this year
        List<Turnover> objectsCurYear = prepareTestTurnovers(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregation, objectsCurYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                null, null,null, null, false);

        // and when only last year
        List<Turnover> objectsPreviousYear = prepareTestTurnovers(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculateTurnoverAggregateForPeriod(afp, aggregation, objectsPreviousYear);
        // then
        assertAggregateForPeriod(afp,  null,  null,null,null,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, false);

        // and when both this and last year
        List<Turnover> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsPreviousYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculateTurnoverAggregateForPeriod(afp, aggregation, currentAndPrevYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, true);

        // and when for 12 M
        afp.setAggregationPeriod(AggregationPeriod.P_12M);
        service.calculateTurnoverAggregateForPeriod(afp, aggregation, currentAndPrevYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("78"), new BigDecimal("72.00"),12,false,
                new BigDecimal("78"),new BigDecimal("72.00"),12, false, true);

    }

    @Test
    public void null_and_0_handling_aggregate_for_period() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();
        aggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_1M);
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        TurnoverAggregation aggregation = new TurnoverAggregation();
        aggregation.setDate(aggregationDate);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setFrequency(Frequency.MONTHLY);
        aggregation.setTurnoverReportingConfig(config);
        List<Turnover> turnovers = new ArrayList<>();

        // when
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);

        // then
        assertAggregateForPeriod(aggregateForPeriod, null, null,null,null, null,null,null, null,false);

        // and when
        Turnover turnoverWithNUlls = setUpTurnover(null, null, null, aggregationDate);
        turnovers.add(turnoverWithNUlls);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, null, null,0,false, null,null,null, null,false);

        // and when
        Turnover turnoverWithNUllsPY = setUpTurnover(null, null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithNUllsPY);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, null, null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroCount = setUpTurnover(null, null, BigInteger.valueOf(0), aggregationDate);
        turnovers.add(turnoverWithZeroCount);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, null, null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroCountPY = setUpTurnover(null, null, BigInteger.valueOf(0), aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroCountPY);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, null, null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroGross = setUpTurnover(new BigDecimal("0.00"), null, null, aggregationDate);
        turnovers.add(turnoverWithZeroGross);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, new BigDecimal("0.00"), null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroNetPY = setUpTurnover(null, new BigDecimal("0.00"), null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroNetPY);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, new BigDecimal("0.00"), null,0,false, null,new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithZeroGrossPY = setUpTurnover(new BigDecimal("0.00"), null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroGrossPY);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, new BigDecimal("0.00"), null,0,false, new BigDecimal("0.00"),new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithZeroNet = setUpTurnover(null, new BigDecimal("0.00"), null, aggregationDate);
        turnovers.add(turnoverWithZeroNet);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, new BigDecimal("0.00"), new BigDecimal("0.00"),0,false, new BigDecimal("0.00"),new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithNonZeroGross = setUpTurnover(new BigDecimal("0.01"), null, null, aggregationDate);
        turnovers.add(turnoverWithNonZeroGross);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, new BigDecimal("0.01"), new BigDecimal("0.00"),1,false, new BigDecimal("0.00"),new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithNonZeroGrossPY = setUpTurnover(new BigDecimal("0.01"), null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithNonZeroGrossPY);
        service.calculateTurnoverAggregateForPeriod(aggregateForPeriod, aggregation, turnovers);
        // then
        assertAggregateForPeriod(aggregateForPeriod, new BigDecimal("0.01"), new BigDecimal("0.00"),1,false, new BigDecimal("0.01"),new BigDecimal("0.00"),1, false,true);


    }

    private void assertAggregateForPeriod(
            final TurnoverAggregateForPeriod afp,
            final BigDecimal g,
            final BigDecimal n,
            final Integer tc,
            final Boolean cC,
            final BigDecimal gP,
            final BigDecimal nP,
            final Integer tcP,
            final Boolean cP,
            final boolean c
    ){
        assertThat(afp.getGrossAmount()).isEqualTo(g);
        assertThat(afp.getNetAmount()).isEqualTo(n);
        assertThat(afp.getTurnoverCount()).isEqualTo(tc);
        assertThat(afp.getNonComparableThisYear()).isEqualTo(cC);
        assertThat(afp.getGrossAmountPreviousYear()).isEqualTo(gP);
        assertThat(afp.getNetAmountPreviousYear()).isEqualTo(nP);
        assertThat(afp.getTurnoverCountPreviousYear()).isEqualTo(tcP);
        assertThat(afp.getNonComparablePreviousYear()).isEqualTo(cP);
        assertThat(afp.isComparable()).isEqualTo(c);
    }

    private Turnover setUpTurnover(final BigDecimal grossAmount, final BigDecimal netAmount, final BigInteger count, final LocalDate date){
        Turnover turnover = new Turnover();
        turnover.setGrossAmount(grossAmount);
        turnover.setNetAmount(netAmount);
        turnover.setPurchaseCount(count);
        turnover.setDate(date);
        return turnover;
    }

    @Test
    public void getTurnoversForAggregationPeriod_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        final AggregationPeriod aggregationPeriod = AggregationPeriod.P_2M;
        List<Turnover> turnovers = new ArrayList<>();

        // when
        final Turnover turnoverThisYear = new Turnover(null, aggregationDate, null, null, null, null);
        final Turnover turnoverPlus1Month = new Turnover(null, aggregationDate.plusMonths(1), null, null, null, null);
        final Turnover turnoverMinus1Month = new Turnover(null, aggregationDate.minusMonths(1), null, null, null, null);
        turnovers.add(turnoverThisYear);
        turnovers.add(turnoverPlus1Month);
        turnovers.add(turnoverMinus1Month);

        // then
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).hasSize(2);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).contains(turnoverThisYear);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).contains(turnoverMinus1Month);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).doesNotContain(turnoverPlus1Month);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).isEmpty();

        // and when
        final Turnover turnoverPreviousYear = new Turnover(null, aggregationDate.minusYears(1), null, null, null, null);
        final Turnover turnoverPYPlus1Month = new Turnover(null, aggregationDate.minusYears(1).plusMonths(1), null, null, null, null);
        final Turnover turnoverPYMinus1Month = new Turnover(null, aggregationDate.minusYears(1).minusMonths(1), null, null, null, null);
        turnovers.add(turnoverPreviousYear);
        turnovers.add(turnoverPYPlus1Month);
        turnovers.add(turnoverPYMinus1Month);

        // then
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).hasSize(2);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).contains(turnoverThisYear);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).contains(turnoverMinus1Month);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).doesNotContain(turnoverPlus1Month);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).hasSize(2);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).contains(turnoverPreviousYear);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).contains(turnoverPYMinus1Month);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).doesNotContain(turnoverPYPlus1Month);

    }

    @Test
    public void getTurnoversForAggregationPeriod_works_when_Covid() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 6, 1);
        final AggregationPeriod aggregationPeriod = AggregationPeriod.P_12M_COVID;
        List<Turnover> turnovers = new ArrayList<>();

        // when
        final Turnover turnoverJuly2020 = new Turnover(null, aggregationDate.plusMonths(1), null, null, null, null);
        final Turnover turnoverJune2020 = new Turnover(null, aggregationDate, null, null, null, null);
        final Turnover turnoverMay2020 = new Turnover(null, aggregationDate.minusMonths(1), null, null, null, null);
        final Turnover turnoverApril2020 = new Turnover(null, aggregationDate.minusMonths(2), null, null, null, null);
        final Turnover turnoverMarch2020 = new Turnover(null, aggregationDate.minusMonths(3), null, null, null, null);
        final Turnover turnoverFeb2020 = new Turnover(null, aggregationDate.minusMonths(4), null, null, null, null);
        turnovers.addAll(Arrays.asList(turnoverJuly2020, turnoverJune2020, turnoverMay2020, turnoverApril2020, turnoverMarch2020, turnoverFeb2020));

        // then
        Assertions.assertThat(turnovers).hasSize(6);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).hasSize(2);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).doesNotContain(turnoverJuly2020);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).contains(turnoverJune2020);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).contains(turnoverFeb2020);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).doesNotContain(turnoverMay2020);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).doesNotContain(turnoverApril2020);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, false)).doesNotContain(turnoverMarch2020);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).isEmpty();

        // and when
        final Turnover turnoverJuly2019 = new Turnover(null, aggregationDate.minusYears(1).plusMonths(1), null, null, null, null);
        final Turnover turnoverJune2019 = new Turnover(null, aggregationDate.minusYears(1), null, null, null, null);
        final Turnover turnoverMay2019 = new Turnover(null, aggregationDate.minusYears(1).minusMonths(1), null, null, null, null);
        final Turnover turnoverApril2019 = new Turnover(null, aggregationDate.minusYears(1).minusMonths(2), null, null, null, null);
        final Turnover turnoverMarch2019 = new Turnover(null, aggregationDate.minusYears(1).minusMonths(3), null, null, null, null);
        final Turnover turnoverFeb2019 = new Turnover(null, aggregationDate.minusYears(1).minusMonths(4), null, null, null, null);
        turnovers.addAll(Arrays.asList(turnoverJuly2019, turnoverJune2019, turnoverMay2019, turnoverApril2019, turnoverMarch2019, turnoverFeb2019));

        // then
        Assertions.assertThat(turnovers).hasSize(12);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).hasSize(5);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).doesNotContain(turnoverJuly2019);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).contains(turnoverMay2019);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).contains(turnoverApril2019);
        Assertions.assertThat(service.getTurnoversForAggregationPeriod(aggregationPeriod, aggregationDate, turnovers, true)).contains(turnoverMarch2019);


    }


    @Test
    public void calculatePurchaseCountAggregateForPeriod_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        TurnoverAggregation aggregation = new TurnoverAggregation();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        aggregation.setDate(aggregationDate);
        final PurchaseCountAggregateForPeriod pafp = new PurchaseCountAggregateForPeriod();

        // when
        pafp.setAggregationPeriod(AggregationPeriod.P_2M);
        List<Turnover> objects = prepareTestTurnovers(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregation, objects);

        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("1"), null, false);

        // and when only this year
        List<Turnover> objectsCurYear = prepareTestTurnovers(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregation, objectsCurYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("23"), null, false);

        // and when only last year
        List<Turnover> objectsPreviousYear = prepareTestTurnovers(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregation, objectsPreviousYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, null, new BigInteger("23"), false);

        // and when both this and last year
        List<Turnover> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsPreviousYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregation, currentAndPrevYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("23"), new BigInteger("23"), true);

        // and when for 12 M
        pafp.setAggregationPeriod(AggregationPeriod.P_12M);
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregation, currentAndPrevYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("78"), new BigInteger("78"), true);

    }

    @Test
    public void null_and_0_handling_purchaseCount_for_period() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final PurchaseCountAggregateForPeriod countAggregateForPeriod = new PurchaseCountAggregateForPeriod();
        countAggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_1M);
        TurnoverAggregation aggregation = new TurnoverAggregation();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        aggregation.setDate(aggregationDate);
        List<Turnover> turnovers = new ArrayList<>();

        // when
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);

        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, null, null,false);

        // and when
        Turnover turnoverWithNUlls = setUpTurnover(null, null, null, aggregationDate);
        turnovers.add(turnoverWithNUlls);
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);
        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, null, null,false);

        // and when
        Turnover turnoverWithNUllsPY = setUpTurnover(null, null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithNUllsPY);
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);
        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, null, null,false);

        // and when
        Turnover turnoverWithZeroCount = setUpTurnover(null, null, BigInteger.valueOf(0), aggregationDate);
        turnovers.add(turnoverWithZeroCount);
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);
        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, BigInteger.valueOf(0), null,false);

        // and when
        Turnover turnoverWithZeroCountPY = setUpTurnover(null, null, BigInteger.valueOf(0), aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroCountPY);
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);
        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, BigInteger.valueOf(0), BigInteger.valueOf(0),false);

        // and when
        Turnover turnoverWithNonZeroCount = setUpTurnover(null, null, BigInteger.valueOf(1), aggregationDate);
        turnovers.add(turnoverWithNonZeroCount);
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);
        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, BigInteger.valueOf(1), BigInteger.valueOf(0),false);

        // and when
        Turnover turnoverWithNonZeroCountPY = setUpTurnover(null, null, BigInteger.valueOf(1), aggregationDate.minusYears(1));
        turnovers.add(turnoverWithNonZeroCountPY);
        service.calculatePurchaseCountAggregateForPeriod(countAggregateForPeriod, aggregation, turnovers);
        // then
        assertPurchaseCountAggregateForPeriod(countAggregateForPeriod, BigInteger.valueOf(1), BigInteger.valueOf(1),true);

    }

    private void assertPurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod pafp,
            final BigInteger cnt,
            final BigInteger cntPY,
            final boolean c
    ){
        assertThat(pafp.getCount()).isEqualTo(cnt);
        assertThat(pafp.getCountPreviousYear()).isEqualTo(cntPY);
        assertThat(pafp.isComparable()).isEqualTo(c);
    }

    @Test
    public void calculateTurnoverAggregateToDate_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        final TurnoverAggregateToDate tad = new TurnoverAggregateToDate();
        TurnoverAggregation aggregation = new TurnoverAggregation();
        aggregation.setDate(aggregationDate);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setFrequency(Frequency.MONTHLY);
        aggregation.setTurnoverReportingConfig(config);

        // when
        List<Turnover> objects = prepareTestTurnovers(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculateTurnoverAggregateToDate(tad, aggregation, objects);

        // then
        assertAggregateToDate(tad, new BigDecimal("1"), new BigDecimal("0.50"),1,false,
                null, null,null, null, false);

        // and when 2 M only this year
        final LocalDate aggregationDate2M = new LocalDate(2020, 2, 1);
        aggregation.setDate(aggregationDate2M);
        List<Turnover> objectsCurYear = prepareTestTurnovers(LocalDateInterval.including(aggregationDate2M.minusMonths(11), aggregationDate2M));
        service.calculateTurnoverAggregateToDate(tad, aggregation, objectsCurYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                null, null,null, null, false);

        // and when 2 M only last year
        List<Turnover> objectsLastYear = prepareTestTurnovers(LocalDateInterval.including(aggregationDate2M.minusYears(1).minusMonths(11), aggregationDate2M.minusYears(1)));
        service.calculateTurnoverAggregateToDate(tad, aggregation, objectsLastYear);
        // then
        assertAggregateToDate(tad,  null,  null, null,null,
                new BigDecimal("23"), new BigDecimal("22.00"),2, false, false);

        // and when 2M both this and last year
        List<Turnover> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsLastYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculateTurnoverAggregateToDate(tad, aggregation, currentAndPrevYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, true);

        // and when for 12 M
        final LocalDate aggregationDate12M = new LocalDate(2020, 12, 1);
        aggregation.setDate(aggregationDate12M);
        List<Turnover> currentAndPrevYearAll = new ArrayList<>();
        List<Turnover> objectsLastYearAll = prepareTestTurnovers(LocalDateInterval.including(aggregationDate12M.minusYears(1).minusMonths(11), aggregationDate12M.minusYears(1)));
        List<Turnover> objectsCurrentYearAll = prepareTestTurnovers(LocalDateInterval.including(aggregationDate12M.minusMonths(11), aggregationDate12M));
        currentAndPrevYearAll.addAll(objectsLastYearAll);
        currentAndPrevYearAll.addAll(objectsCurrentYearAll);
        service.calculateTurnoverAggregateToDate(tad, aggregation, currentAndPrevYearAll);
        // then
        assertAggregateToDate(tad, new BigDecimal("78"), new BigDecimal("72.00"),12,false,
                new BigDecimal("78"),new BigDecimal("72.00"),12, false, true);

    }

    @Test
    public void null_and_0_handling_aggregate_to_date() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final TurnoverAggregateToDate aggregateToDate = new TurnoverAggregateToDate();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        TurnoverAggregation aggregation = new TurnoverAggregation();
        aggregation.setDate(aggregationDate);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setFrequency(Frequency.MONTHLY);
        aggregation.setTurnoverReportingConfig(config);
        List<Turnover> turnovers = new ArrayList<>();

        // when
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);

        // then
        assertAggregateToDate(aggregateToDate, null, null,null,null, null,null,null, null,false);

        // and when
        Turnover turnoverWithNUlls = setUpTurnover(null, null, null, aggregationDate);
        turnovers.add(turnoverWithNUlls);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, null, null,0,false, null,null,null, null,false);

        // and when
        Turnover turnoverWithNUllsPY = setUpTurnover(null, null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithNUllsPY);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, null, null,0,false, null,null,0, false,false);


        // and when
        Turnover turnoverWithZeroCount = setUpTurnover(null, null, BigInteger.valueOf(0), aggregationDate);
        turnovers.add(turnoverWithZeroCount);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, null, null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroCountPY = setUpTurnover(null, null, BigInteger.valueOf(0), aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroCountPY);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, null, null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroGross = setUpTurnover(new BigDecimal("0.00"), null, null, aggregationDate);
        turnovers.add(turnoverWithZeroGross);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, new BigDecimal("0.00"), null,0,false, null,null,0, false,false);

        // and when
        Turnover turnoverWithZeroNetPY = setUpTurnover(null, new BigDecimal("0.00"), null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroNetPY);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, new BigDecimal("0.00"), null,0,false, null,new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithZeroGrossPY = setUpTurnover(new BigDecimal("0.00"), null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithZeroGrossPY);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, new BigDecimal("0.00"), null,0,false, new BigDecimal("0.00"),new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithZeroNet = setUpTurnover(null, new BigDecimal("0.00"), null, aggregationDate);
        turnovers.add(turnoverWithZeroNet);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, new BigDecimal("0.00"), new BigDecimal("0.00"),0,false, new BigDecimal("0.00"),new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithNonZeroGross = setUpTurnover(new BigDecimal("0.01"), null, null, aggregationDate);
        turnovers.add(turnoverWithNonZeroGross);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, new BigDecimal("0.01"), new BigDecimal("0.00"),1,false, new BigDecimal("0.00"),new BigDecimal("0.00"),0, false,false);

        // and when
        Turnover turnoverWithNonZeroGrossPY = setUpTurnover(new BigDecimal("0.01"), null, null, aggregationDate.minusYears(1));
        turnovers.add(turnoverWithNonZeroGrossPY);
        service.calculateTurnoverAggregateToDate(aggregateToDate, aggregation, turnovers);
        // then
        assertAggregateToDate(aggregateToDate, new BigDecimal("0.01"), new BigDecimal("0.00"),1,false, new BigDecimal("0.01"),new BigDecimal("0.00"),1, false,true);


    }



    private void assertAggregateToDate(
            final TurnoverAggregateToDate afp,
            final BigDecimal g,
            final BigDecimal n,
            final Integer tc,
            final Boolean cC,
            final BigDecimal gP,
            final BigDecimal nP,
            final Integer tcP,
            final Boolean cP,
            final boolean c
    ){
        assertThat(afp.getGrossAmount()).isEqualTo(g);
        assertThat(afp.getNetAmount()).isEqualTo(n);
        assertThat(afp.getTurnoverCount()).isEqualTo(tc);
        assertThat(afp.getNonComparableThisYear()).isEqualTo(cC);
        assertThat(afp.getGrossAmountPreviousYear()).isEqualTo(gP);
        assertThat(afp.getNetAmountPreviousYear()).isEqualTo(nP);
        assertThat(afp.getTurnoverCountPreviousYear()).isEqualTo(tcP);
        assertThat(afp.getNonComparablePreviousYear()).isEqualTo(cP);
        assertThat(afp.isComparable()).isEqualTo(c);
    }

    private List<Turnover> prepareTestTurnovers(final LocalDateInterval interval){
        List<Turnover> result = new ArrayList<>();
        LocalDate date = interval.startDate();
        int cnt = 1;
        while (!date.isAfter(interval.endDate())){
            Turnover t = new Turnover();
            t.setDate(date);
            t.setGrossAmount(BigDecimal.valueOf(cnt));
            t.setNetAmount(BigDecimal.valueOf(cnt).subtract(new BigDecimal("0.50")));
            t.setNonComparable(false);
            t.setPurchaseCount(BigInteger.valueOf(cnt));
            result.add(t);

            date = date.plusMonths(1);
            cnt++;
        }
        return result;
    }

    @Test
    public void determine_turnover_count_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        List<Turnover> turnovers = new ArrayList<>();
        // when
        Integer turnoverCount = service.determineTurnoverCount(turnovers, Frequency.MONTHLY);
        // then
        assertThat(turnoverCount).isNull();

        // and when
        Turnover t1 = new Turnover();
        t1.setDate(new LocalDate(2020,1,15));
        turnovers.add(t1);
        turnoverCount = service.determineTurnoverCount(turnovers, Frequency.MONTHLY);
        // then
        assertThat(turnoverCount).isNull();

        // and when
        t1.setGrossAmount(new BigDecimal("0.01"));
        turnoverCount = service.determineTurnoverCount(turnovers, Frequency.MONTHLY);
        // then
        assertThat(turnoverCount).isEqualTo(1);

        // and when
        t1.setGrossAmount(null);
        t1.setNetAmount(new BigDecimal("0.01"));
        turnoverCount = service.determineTurnoverCount(turnovers, Frequency.MONTHLY);
        // then
        assertThat(turnoverCount).isEqualTo(1);

        // and when
        Turnover t2 = new Turnover();
        t2.setDate(new LocalDate(2020,2,1));
        t2.setGrossAmount(new BigDecimal("0.01"));
        turnovers.add(t2);
        turnoverCount = service.determineTurnoverCount(turnovers, Frequency.MONTHLY);
        // then
        assertThat(turnoverCount).isEqualTo(2);

        // and when turnover dates in same month
        t2.setDate(new LocalDate(2020,1,31));
        turnoverCount = service.determineTurnoverCount(turnovers, Frequency.MONTHLY);
        // then no double count
        assertThat(turnoverCount).isEqualTo(1);
    }

}