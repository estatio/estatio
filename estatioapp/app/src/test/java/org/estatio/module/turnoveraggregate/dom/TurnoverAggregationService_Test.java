package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.aggregation.AggregationPattern;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

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
        final TurnoverAggregateForPeriod afp = new TurnoverAggregateForPeriod();

        // when
        afp.setAggregationPeriod(AggregationPeriod.P_2M);
        List<Turnover> objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objects);

        // then
        assertAggregateForPeriod(afp, new BigDecimal("1"), new BigDecimal("0.50"),1,false,
                null, null,null, null, false);

        // when turnover values 0
        afp.setAggregationPeriod(AggregationPeriod.P_2M);
        objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        objects.forEach(t->{
            t.setGrossAmount(BigDecimal.ZERO);
            t.setNetAmount(BigDecimal.ZERO);
        });
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objects);

        // then
        assertAggregateForPeriod(afp, new BigDecimal("0"), new BigDecimal("0"),null,false,
                null, null,null, null, false);

        // and when only this year
        List<Turnover> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objectsCurYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                null, null,null, null, false);

        // and when only last year
        List<Turnover> objectsPreviousYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objectsPreviousYear);
        // then
        assertAggregateForPeriod(afp,  null,  null,null,null,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, false);

        // and when both this and last year
        List<Turnover> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsPreviousYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, currentAndPrevYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, true);

        // and when for 12 M
        afp.setAggregationPeriod(AggregationPeriod.P_12M);
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, currentAndPrevYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("78"), new BigDecimal("72.00"),12,false,
                new BigDecimal("78"),new BigDecimal("72.00"),12, false, true);

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

    @Test
    public void calculatePurchaseCountAggregateForPeriod_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        final PurchaseCountAggregateForPeriod pafp = new PurchaseCountAggregateForPeriod();

        // when
        pafp.setAggregationPeriod(AggregationPeriod.P_2M);
        List<Turnover> objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objects);

        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("1"), null, false);

        // and when only this year
        List<Turnover> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objectsCurYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("23"), null, false);

        // and when only last year
        List<Turnover> objectsPreviousYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objectsPreviousYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, null, new BigInteger("23"), false);

        // and when both this and last year
        List<Turnover> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsPreviousYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, currentAndPrevYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("23"), new BigInteger("23"), true);

        // and when for 12 M
        pafp.setAggregationPeriod(AggregationPeriod.P_12M);
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, currentAndPrevYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("78"), new BigInteger("78"), true);

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

        // when
        List<Turnover> objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate, objects);

        // then
        assertAggregateToDate(tad, new BigDecimal("1"), new BigDecimal("0.50"),1,false,
                null, null,null, null, false);

        // and when 2 M only this year
        final LocalDate aggregationDate2M = new LocalDate(2020, 2, 1);
        List<Turnover> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate2M.minusMonths(11), aggregationDate2M));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, objectsCurYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                null, null,null, null, false);

        // and when 2 M only last year
        List<Turnover> objectsLastYear = prepareTestObjects(LocalDateInterval.including(aggregationDate2M.minusYears(1).minusMonths(11), aggregationDate2M.minusYears(1)));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, objectsLastYear);
        // then
        assertAggregateToDate(tad,  null,  null, null,null,
                new BigDecimal("23"), new BigDecimal("22.00"),2, false, false);

        // and when 2M both this and last year
        List<Turnover> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsLastYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, currentAndPrevYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, true);

        // and when for 12 M
        final LocalDate aggregationDate12M = new LocalDate(2020, 12, 1);
        List<Turnover> currentAndPrevYearAll = new ArrayList<>();
        List<Turnover> objectsLastYearAll = prepareTestObjects(LocalDateInterval.including(aggregationDate12M.minusYears(1).minusMonths(11), aggregationDate12M.minusYears(1)));
        List<Turnover> objectsCurrentYearAll = prepareTestObjects(LocalDateInterval.including(aggregationDate12M.minusMonths(11), aggregationDate12M));
        currentAndPrevYearAll.addAll(objectsLastYearAll);
        currentAndPrevYearAll.addAll(objectsCurrentYearAll);
        service.calculateTurnoverAggregateToDate(tad, aggregationDate12M, currentAndPrevYearAll);
        // then
        assertAggregateToDate(tad, new BigDecimal("78"), new BigDecimal("72.00"),12,false,
                new BigDecimal("78"),new BigDecimal("72.00"),12, false, true);

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

    private List<Turnover> prepareTestObjects(final LocalDateInterval interval){
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

}