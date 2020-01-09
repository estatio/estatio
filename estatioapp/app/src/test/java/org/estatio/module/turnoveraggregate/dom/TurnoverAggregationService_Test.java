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

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import cucumber.api.java.cs.A;

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
            @Override List<Turnover> turnoversToAggregateForPeriod(
                    final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear) {
                return prevYear ? Arrays.asList(tpy1) : Arrays.asList(t1, t2);
            }
        };

        TurnoverAggregateForPeriod aggregateForPeriod = new TurnoverAggregateForPeriod();
        final Occupancy occupancy = new Occupancy();
        final LocalDate aggregationDate = new LocalDate(2019, 1, 1);
        aggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_2M);

        // when
        service.aggregateForPeriod(aggregateForPeriod, occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY);

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
    public void aggregateToDate_works() {

        // given
        final LocalDate aggregationDate = new LocalDate(2019, 2, 1);
        Turnover t1 = new Turnover();
        t1.setGrossAmount(new BigDecimal("123.00"));
        t1.setNetAmount(new BigDecimal("111.11"));
        Turnover t2 = new Turnover();
        t2.setGrossAmount(new BigDecimal("0.45"));
        Turnover tpy1 = new Turnover();
        tpy1.setGrossAmount(new BigDecimal("100.23"));
        tpy1.setNetAmount(new BigDecimal("99.99"));

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override
            List<Turnover> turnoversToAggregate(
                    final Occupancy occupancy,
                    final LocalDate date,
                    final LocalDate periodStartDate,
                    final LocalDate periodEndDate,
                    final Type type,
                    final Frequency frequency) {
                // this year returns t1, t2, previous year tpy1
                return periodEndDate.equals(aggregationDate) ? Arrays.asList(t1, t2) : Arrays.asList(tpy1);
            }
        };

        TurnoverAggregateToDate turnoverAggregateToDate = new TurnoverAggregateToDate();
        final Occupancy occupancy = new Occupancy();

        // when
        service.aggregateToDate(turnoverAggregateToDate, occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY);

        // then
        Assertions.assertThat(turnoverAggregateToDate.getTurnoverCount()).isEqualTo(2);
        Assertions.assertThat(turnoverAggregateToDate.getTurnoverCountPreviousYear()).isEqualTo(1);

        Assertions.assertThat(turnoverAggregateToDate.getGrossAmount()).isEqualTo(new BigDecimal("123.45"));
        Assertions.assertThat(turnoverAggregateToDate.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        Assertions.assertThat(turnoverAggregateToDate.getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("100.23"));
        Assertions.assertThat(turnoverAggregateToDate.getNetAmountPreviousYear()).isEqualTo(new BigDecimal("99.99"));

        Assertions.assertThat(turnoverAggregateToDate.isNonComparableThisYear()).isEqualTo(false);
        Assertions.assertThat(turnoverAggregateToDate.isNonComparablePreviousYear()).isEqualTo(false);
        Assertions.assertThat(turnoverAggregateToDate.isComparable()).isEqualTo(false);

    }

    @Test
    public void aggregateForPurchaseCount_works() {

        // given
        Turnover t1 = new Turnover();
        t1.setPurchaseCount(new BigInteger("123"));
        Turnover t2 = new Turnover();
        t2.setPurchaseCount(new BigInteger("234"));
        Turnover tpy1 = new Turnover();
        tpy1.setPurchaseCount(new BigInteger("345"));

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override List<Turnover> turnoversToAggregateForPeriod(
                    final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear) {
                return prevYear ? Arrays.asList(tpy1) : Arrays.asList(t1, t2);
            }
        };

        PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod = new PurchaseCountAggregateForPeriod();
        final Occupancy occupancy = new Occupancy();
        final LocalDate aggregationDate = new LocalDate(2019, 1, 1);
        purchaseCountAggregateForPeriod.setAggregationPeriod(AggregationPeriod.P_2M);

        // when
        service.aggregateForPurchaseCount(purchaseCountAggregateForPeriod, occupancy, aggregationDate, Type.PRELIMINARY, Frequency.MONTHLY);

        // then
        Assertions.assertThat(purchaseCountAggregateForPeriod.getCount()).isEqualTo(new BigInteger("357"));
        Assertions.assertThat(purchaseCountAggregateForPeriod.getCountPreviousYear()).isEqualTo(new BigInteger("345"));
        Assertions.assertThat(purchaseCountAggregateForPeriod.isComparable()).isEqualTo(false);

    }

    @Test
    public void aggregate_works() throws Exception {

        // given
        final LocalDate aggregationDateM1 = new LocalDate(2019, 2, 1);
        Turnover tM1_1 = new Turnover();
        tM1_1.setGrossAmount(new BigDecimal("123.00"));
        tM1_1.setNetAmount(new BigDecimal("111.11"));
        Turnover tM1_2 = new Turnover();
        tM1_2.setGrossAmount(new BigDecimal("0.45"));
        Turnover tM2_1 = new Turnover();
        tM2_1.setGrossAmount(new BigDecimal("100.23"));
        tM2_1.setNetAmount(new BigDecimal("99.99"));
        Turnover t1 = new Turnover();
        t1.setComments("xxx");
        Turnover t2 = new Turnover();
        t2.setComments("yyy");
        Turnover tpy1 = new Turnover();
        tpy1.setComments("zzz");

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override
            List<Turnover> turnoversToAggregate(
                    final Occupancy occupancy,
                    final LocalDate date,
                    final LocalDate periodStartDate,
                    final LocalDate periodEndDate,
                    final Type type,
                    final Frequency frequency) {
                return periodEndDate.equals(aggregationDateM1) ? Arrays.asList(tM1_1, tM1_2) : Arrays.asList(tM2_1);
            }

            @Override List<Turnover> turnoversToAggregateForPeriod(
                    final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear) {
                return prevYear ? Arrays.asList(tpy1) : Arrays.asList(t1, t2);
            }
        };

        // when
        TurnoverAggregation aggregation = new TurnoverAggregation();
        aggregation.setType(Type.PRELIMINARY);
        aggregation.setFrequency(Frequency.MONTHLY);
        aggregation.setDate(aggregationDateM1.plusMonths(1));
        service.aggregateOtherAggregationProperties(aggregation);

        // then
        Assertions.assertThat(aggregation.getGrossAmount1MCY_1()).isEqualTo(new BigDecimal("123.45"));
        Assertions.assertThat(aggregation.getNetAmount1MCY_1()).isEqualTo(new BigDecimal("111.11"));
        Assertions.assertThat(aggregation.getGrossAmount1MCY_2()).isEqualTo(new BigDecimal("100.23"));
        Assertions.assertThat(aggregation.getNetAmount1MCY_2()).isEqualTo(new BigDecimal("99.99"));
        Assertions.assertThat(aggregation.getComments12MCY()).isEqualTo("xxxyyy");
        Assertions.assertThat(aggregation.getComments12MPY()).isEqualTo("zzz");
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

    @Test
    public void isComparableToDate_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2019, 2, 1);

        // when, then
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 3, 2, false, false)).isTrue();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 1, 2, false, false)).isFalse();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 1, false, false)).isFalse();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 2, true, false)).isFalse();
        Assertions.assertThat(service.isComparableToDate(aggregationDate, 2, 2, false, true)).isFalse();

        Assertions.assertThat(service.isComparableToDate(aggregationDate, 0, 2, false, true)).isFalse();
    }

    @Test
    public void getMinNumberOfTurnoversToDate_works() throws Exception {

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when, then
        Assertions.assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,1,1))).isEqualTo(1);
        Assertions.assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,2,1))).isEqualTo(2);
        //etc
        Assertions.assertThat(service.getMinNumberOfTurnoversToDate(new LocalDate(2019,12,1))).isEqualTo(12);

    }

    @Test
    public void leasesToExamine_works() throws Exception {
        //given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        Lease l1 = new Lease();
        Lease l2 = new Lease();
        Lease l3 = new Lease();
        l2.setPrevious(l3);
        l1.setPrevious(l2);

        // then
        Assertions.assertThat(service.leasesToExamine(l3)).hasSize(1);
        Assertions.assertThat(service.leasesToExamine(l3)).contains(l3);
        Assertions.assertThat(service.leasesToExamine(l2)).hasSize(2);
        Assertions.assertThat(service.leasesToExamine(l3)).doesNotContain(l1);
        Assertions.assertThat(service.leasesToExamine(l1)).hasSize(3);
    }

    @Test
    public void occupanciesToExamine_works() throws Exception {

        List<Lease> leases;

        //given
        TurnoverAggregationService service = new TurnoverAggregationService();
        Unit sameUnit = new Unit();
        Unit otherUnit = new Unit();
        otherUnit.setName("other");

        // when
        leases = new ArrayList<>();
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(0);

        // when overlap, 2 on same unit, 1 on other unit
        Lease l1 = new Lease();
        Occupancy occOnSameUnit1 = new Occupancy();
        occOnSameUnit1.setUnit(sameUnit);
        Occupancy occOnSameUnit2 = new Occupancy();
        occOnSameUnit2.setUnit(sameUnit);
        occOnSameUnit2.setStartDate(new LocalDate(2019,1,1)); // to differentiate in sorted set
        Occupancy occOnOtherUnit1 = new Occupancy();
        occOnOtherUnit1.setUnit(otherUnit);
        l1.getOccupancies().addAll(Arrays.asList(occOnSameUnit1, occOnSameUnit2, occOnOtherUnit1));
        Assertions.assertThat(l1.getOccupancies()).hasSize(3);
        Assertions.assertThat(service.noOverlapOccupanciesOnLease(l1)).isFalse();
        leases = Arrays.asList(l1);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(2);
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).doesNotContain(occOnOtherUnit1);

        // when no overlap, 2 on same unit, 1 on other unit
        occOnOtherUnit1.setEndDate(new LocalDate(2018,12, 1));
        occOnSameUnit1.setStartDate(new LocalDate(2018, 12, 2));
        occOnSameUnit1.setEndDate(new LocalDate(2018, 12, 31));
        Assertions.assertThat(l1.getOccupancies()).hasSize(3);
        Assertions.assertThat(service.noOverlapOccupanciesOnLease(l1)).isTrue();
        leases = Arrays.asList(l1);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(3);
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).contains(occOnOtherUnit1);

        // when no occupancies on same sameUnit and exactly one occupancy on other sameUnit
        Lease l2 = new Lease();
        Occupancy occOnOtherUnit2 = new Occupancy();
        occOnOtherUnit2.setUnit(otherUnit);
        l2.getOccupancies().add(occOnOtherUnit2);
        Assertions.assertThat(l2.getOccupancies()).hasSize(1);
        Assertions.assertThat(service.noOverlapOccupanciesOnLease(l2)).isTrue();
        leases = Arrays.asList(l2);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(1);

        // when multiple leases
        leases = Arrays.asList(l1, l2);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(4);

        // when no occupancies on same sameUnit and multiple occupancies on other sameUnit and overlap
        Lease l3 = new Lease();
        Occupancy occOnOtherUnit3 = new Occupancy();
        occOnOtherUnit3.setUnit(otherUnit);
        Occupancy occOnOtherUnit4 = new Occupancy();
        occOnOtherUnit4.setUnit(otherUnit);
        occOnOtherUnit4.setStartDate(new LocalDate(2019,1,1)); // to differentiate in sorted set
        l3.getOccupancies().addAll(Arrays.asList(occOnOtherUnit3, occOnOtherUnit4));
        Assertions.assertThat(l3.getOccupancies()).hasSize(2);
        Assertions.assertThat(service.noOverlapOccupanciesOnLease(l3)).isFalse();
        leases = Arrays.asList(l3);
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(0);

        // when no occupancies on same sameUnit and multiple occupancies on other sameUnit and no overlap
        occOnOtherUnit3.setEndDate(new LocalDate(2018,12,31));
        Assertions.assertThat(l3.getOccupancies()).hasSize(2);
        Assertions.assertThat(service.noOverlapOccupanciesOnLease(l3)).isTrue();
        // then
        Assertions.assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(2);

    }

    @Mock ClockService mockClockService;

    @Test
    public void aggregationDatesForTurnoverReportingConfig_works() throws Exception {

        // given
        final LocalDate now = new LocalDate(2019, 2, 3);
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate occEffectiveEndDate = new LocalDate(2019, 2, 3);

        final TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Occupancy occupancy = new Occupancy(){
            @Override public LocalDate getEffectiveEndDate() {
                return occEffectiveEndDate;
            }
        };
        final Lease lease = new Lease();
        lease.setTenancyEndDate(now); // lease is ended on aggregationDate
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);

        // when
        config.setFrequency(Frequency.MONTHLY);
        config.setStartDate(new LocalDate(2018,12,2));

        // then
        List<LocalDate> dates = service.aggregationDatesForTurnoverReportingConfig(config, now);
        Assertions.assertThat(dates).hasSize(27);
        Assertions.assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        Assertions.assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        Assertions.assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));
        Assertions.assertThat(dates.get(26)).isEqualTo(new LocalDate(2021,2,1));

        // and when lease is active
        lease.setTenancyEndDate(null);
        dates = service.aggregationDatesForTurnoverReportingConfig(config, now);
        // then
        Assertions.assertThat(dates).hasSize(3);
        Assertions.assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        Assertions.assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        Assertions.assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));

    }

    @Test
    public void aggregationDatesForTurnoverReportingConfig_works_when_no_occupancy_effective_endDate() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        service.clockService = mockClockService;
        final LocalDate now = new LocalDate(2019, 2, 3);

        final TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Occupancy occupancy = new Occupancy(){
            @Override public LocalDate getEffectiveEndDate() {
                return null;
            }
        };
        final Lease lease = new Lease();
        lease.setTenancyEndDate(now); // lease is ended on aggregationDate
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);

        // expect
        context.checking(new Expectations(){{
            allowing(mockClockService).now();
            will(returnValue(now));
        }});

        // when lease is ended on aggregationDate
        config.setFrequency(Frequency.MONTHLY);
        config.setStartDate(new LocalDate(2018,12,2));

        // then
        List<LocalDate> dates = service.aggregationDatesForTurnoverReportingConfig(config, now);
        Assertions.assertThat(dates).hasSize(27);
        Assertions.assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        Assertions.assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        Assertions.assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));
        Assertions.assertThat(dates.get(26)).isEqualTo(new LocalDate(2021,2,1));

        // and when lease is active
        lease.setTenancyEndDate(null);
        dates = service.aggregationDatesForTurnoverReportingConfig(config, now);
        // then
        Assertions.assertThat(dates).hasSize(3);
        Assertions.assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        Assertions.assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        Assertions.assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));

    }

    @Test
    public void turnoversToAggregateForOccupancySortedAsc_works() throws Exception {

        // setup
        Occupancy occupancy = new Occupancy();

        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override
            List<Occupancy> occupanciesToExamine(
                    final Unit unit, final List<Lease> leasesToExamine) {
                return Arrays.asList(occupancy);
            }

            @Override List<Lease> leasesToExamine(final Lease lease) {
                return Arrays.asList();
            }
        };
        service.turnoverRepository = mockTurnoverRepo;

        // given
        Turnover t1 = new Turnover();
        t1.setDate(new LocalDate(2020,3,1));
        Turnover t2 = new Turnover();
        t2.setDate(new LocalDate(2020, 2, 1));
        t2.setComments("t2 comment");
        Turnover t3 = new Turnover();
        t3.setDate(new LocalDate(2020, 2, 1));
        t3.setConfig(new TurnoverReportingConfig());
        t3.setComments("t3 comment");
        Turnover t4 = new Turnover();
        t4.setDate(new LocalDate(2020, 1, 1));

        List<Turnover> result = Arrays.asList(t4, t3, t1, t2);

        // expect
        context.checking(new Expectations(){{
            allowing(mockTurnoverRepo).findApprovedByOccupancyAndTypeAndFrequency(occupancy, Type.PRELIMINARY, Frequency.MONTHLY);
            will(returnValue(result));
        }});

        // when
        LocalDate aggregationDate = new LocalDate(2021, 1, 1);
        List<TurnoverValueObject> turnoverValueObjects = service
                .turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        // then
        Assertions.assertThat(turnoverValueObjects.size()).isEqualTo(3);
        TurnoverValueObject tov1 = turnoverValueObjects.get(0);
        TurnoverValueObject tov2 = turnoverValueObjects.get(1);
        TurnoverValueObject tov3 = turnoverValueObjects.get(2);
        Assertions.assertThat(tov1.getDate()).isEqualTo(t4.getDate());
        Assertions.assertThat(tov2.getDate()).isEqualTo(t3.getDate());
        Assertions.assertThat(tov2.getDate()).isEqualTo(t2.getDate());
        Assertions.assertThat(tov3.getDate()).isEqualTo(t1.getDate());

        Assertions.assertThat(tov2.getComments()).isEqualTo("t3 comment | t2 comment");

        // and when
        aggregationDate = new LocalDate(2022, 3, 1);
        turnoverValueObjects = service
                .turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        // then
        Assertions.assertThat(turnoverValueObjects.size()).isEqualTo(1);
        Assertions.assertThat(turnoverValueObjects.get(0).getDate()).isEqualTo(t1.getDate());

        // and when
        aggregationDate = new LocalDate(2022, 3, 2);
        turnoverValueObjects = service
                .turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        // then
        Assertions.assertThat(turnoverValueObjects.size()).isEqualTo(0);
    }

    @Test
    public void determineStartDate_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        LocalDate aggregationDate = null;
        LocalDate minStartDateOnTurnovers = null;
        // then
        Assertions.assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(service.MIN_AGGREGATION_DATE);

        // and when
        minStartDateOnTurnovers = new LocalDate(2012, 1, 1);
        Assertions.assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(service.MIN_AGGREGATION_DATE.minusMonths(24));

        // and when
        aggregationDate = new LocalDate(2013, 12, 31);
        Assertions.assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(minStartDateOnTurnovers);

        // and when
        aggregationDate = new LocalDate(2014, 1, 1);
        Assertions.assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(aggregationDate.minusMonths(24));
        Assertions.assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(minStartDateOnTurnovers);

        // and when
        aggregationDate = new LocalDate(2014, 1, 2);
        Assertions.assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(aggregationDate.minusMonths(24));

    }

    @Test
    public void determineEndDate_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override boolean isOccupancyForLastExpiredLease(
                    final Occupancy occupancy,
                    final LocalDate aggregationDate) {
                return true;
            }
        };
        LocalDate occEffectiveEndDate = new LocalDate(2010,1 ,1);
        Occupancy occupancy = new Occupancy(){
            @Override public LocalDate getEffectiveEndDate() {
                return occEffectiveEndDate;
            }
        };

        // when
        LocalDate aggregationDate = new LocalDate(2010, 1, 1);
        Assertions.assertThat(service.determineEndDate(occupancy, aggregationDate)).isEqualTo(aggregationDate.plusMonths(24));

        // and when
        aggregationDate = new LocalDate(2010, 1, 2);
        Assertions.assertThat(service.determineEndDate(occupancy, aggregationDate)).isEqualTo(occEffectiveEndDate.plusMonths(24));

    }

    @Test
    public void findOrCreateAggregationsForMonthly_works() throws Exception {

        TurnoverAggregationService service = new TurnoverAggregationService();

//        TODO: finish
//        service.findOrCreateAggregationsForMonthly(occupancy, turnoverValueObjects, aggregationDate, currency, type, frequency);

    }

    @Test
    public void aggregateTurnoversForOccupancy_works() throws Exception {

        TurnoverAggregationService service = new TurnoverAggregationService();

        //        TODO: finish
//                service.aggregateTurnoversForOccupancy(occupancy, turnoverValueObjects, aggregationDate, currency, type, frequency);

    }

    @Test
    public void calculateAggregationForOther_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        TurnoverAggregation aggregation = new TurnoverAggregation();
        final LocalDate date = new LocalDate(2020, 1, 1);
        aggregation.setDate(date);

        List<TurnoverValueObject> turnoverValueObjects = new ArrayList<>();

        // when
        Turnover t1 = new Turnover();
        t1.setComments("t1 comment");
        t1.setDate(date.minusMonths(1));
        t1.setGrossAmount(new BigDecimal("1.23"));
        t1.setNetAmount(new BigDecimal("1.00"));
        TurnoverValueObject o1 = new TurnoverValueObject(t1);
        turnoverValueObjects.add(o1);

        Turnover t2 = new Turnover();
        t2.setComments("t2 comment");
        t2.setDate(date.minusMonths(2));
        t2.setGrossAmount(new BigDecimal("2.34"));
        TurnoverValueObject o2 = new TurnoverValueObject(t2);
        turnoverValueObjects.add(o2);

        Turnover t3 = new Turnover();
        t3.setComments("t3 comment");
        t3.setDate(date.minusMonths(11));
        TurnoverValueObject o3 = new TurnoverValueObject(t3);
        turnoverValueObjects.add(o3);

        Turnover t4 = new Turnover();
        t4.setComments("t4 comment");
        t4.setDate(date.minusMonths(12));
        TurnoverValueObject o4 = new TurnoverValueObject(t4);
        turnoverValueObjects.add(o4);

        Assertions.assertThat(turnoverValueObjects).hasSize(4);

        service.calculateAggregationForOther(aggregation, turnoverValueObjects);

        // then
        Assertions.assertThat(aggregation.getGrossAmount1MCY_1()).isEqualTo(t1.getGrossAmount());
        Assertions.assertThat(aggregation.getNetAmount1MCY_1()).isEqualTo(t1.getNetAmount());
        Assertions.assertThat(aggregation.getGrossAmount1MCY_2()).isEqualTo(t2.getGrossAmount());
        Assertions.assertThat(aggregation.getNetAmount1MCY_2()).isNull();
        Assertions.assertThat(aggregation.getComments12MCY()).isEqualTo("t1 comment | t2 comment | t3 comment");
        Assertions.assertThat(aggregation.getComments12MPY()).isEqualTo("t4 comment");

    }

    @Test
    public void calculateTurnoverAggregateForPeriod_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        final TurnoverAggregateForPeriod afp = new TurnoverAggregateForPeriod();

        // when
        afp.setAggregationPeriod(AggregationPeriod.P_2M);
        List<TurnoverValueObject> objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objects);

        // then
        assertAggregateForPeriod(afp, new BigDecimal("1"), new BigDecimal("0.50"),1,false,
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when only this year
        List<TurnoverValueObject> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objectsCurYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when only last year
        List<TurnoverValueObject> objectsPreviousYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objectsPreviousYear);
        // then
        assertAggregateForPeriod(afp,  BigDecimal.ZERO,  BigDecimal.ZERO,0,false,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, false);

        // and when both this and last year
        List<TurnoverValueObject> currentAndPrevYear = new ArrayList<>();
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
            final boolean cC,
            final BigDecimal gP,
            final BigDecimal nP,
            final Integer tcP,
            final boolean cP,
            final boolean c
    ){
        Assertions.assertThat(afp.getGrossAmount()).isEqualTo(g);
        Assertions.assertThat(afp.getNetAmount()).isEqualTo(n);
        Assertions.assertThat(afp.getTurnoverCount()).isEqualTo(tc);
        Assertions.assertThat(afp.isNonComparableThisYear()).isEqualTo(cC);
        Assertions.assertThat(afp.getGrossAmountPreviousYear()).isEqualTo(gP);
        Assertions.assertThat(afp.getNetAmountPreviousYear()).isEqualTo(nP);
        Assertions.assertThat(afp.getTurnoverCountPreviousYear()).isEqualTo(tcP);
        Assertions.assertThat(afp.isNonComparablePreviousYear()).isEqualTo(cP);
        Assertions.assertThat(afp.isComparable()).isEqualTo(c);
    }

    @Test
    public void calculatePurchaseCountAggregateForPeriod_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        final PurchaseCountAggregateForPeriod pafp = new PurchaseCountAggregateForPeriod();

        // when
        pafp.setAggregationPeriod(AggregationPeriod.P_2M);
        List<TurnoverValueObject> objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objects);

        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("1"), BigInteger.ZERO, false);

        // and when only this year
        List<TurnoverValueObject> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objectsCurYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("23"), BigInteger.ZERO, false);

        // and when only last year
        List<TurnoverValueObject> objectsPreviousYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objectsPreviousYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, BigInteger.ZERO, new BigInteger("23"), false);

        // and when both this and last year
        List<TurnoverValueObject> currentAndPrevYear = new ArrayList<>();
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
        Assertions.assertThat(pafp.getCount()).isEqualTo(cnt);
        Assertions.assertThat(pafp.getCountPreviousYear()).isEqualTo(cntPY);
        Assertions.assertThat(pafp.isComparable()).isEqualTo(c);
    }

    @Test
    public void calculateTurnoverAggregateToDate_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate aggregationDate = new LocalDate(2020, 1, 1);
        final TurnoverAggregateToDate tad = new TurnoverAggregateToDate();

        // when
        List<TurnoverValueObject> objects = prepareTestObjects(LocalDateInterval.including(aggregationDate, aggregationDate));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate, objects);

        // then
        assertAggregateToDate(tad, new BigDecimal("1"), new BigDecimal("0.50"),1,false,
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when 2 M only this year
        final LocalDate aggregationDate2M = new LocalDate(2020, 2, 1);
        List<TurnoverValueObject> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate2M.minusMonths(11), aggregationDate2M));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, objectsCurYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when 2 M only last year
        List<TurnoverValueObject> objectsLastYear = prepareTestObjects(LocalDateInterval.including(aggregationDate2M.minusYears(1).minusMonths(11), aggregationDate2M.minusYears(1)));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, objectsLastYear);
        // then
        assertAggregateToDate(tad,  BigDecimal.ZERO,  BigDecimal.ZERO, 0,false,
                new BigDecimal("23"), new BigDecimal("22.00"),2, false, false);

        // and when 2M both this and last year
        List<TurnoverValueObject> currentAndPrevYear = new ArrayList<>();
        currentAndPrevYear.addAll(objectsLastYear);
        currentAndPrevYear.addAll(objectsCurYear);
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, currentAndPrevYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                new BigDecimal("23"),new BigDecimal("22.00"),2, false, true);

        // and when for 12 M
        final LocalDate aggregationDate12M = new LocalDate(2020, 12, 1);
        List<TurnoverValueObject> currentAndPrevYearAll = new ArrayList<>();
        List<TurnoverValueObject> objectsLastYearAll = prepareTestObjects(LocalDateInterval.including(aggregationDate12M.minusYears(1).minusMonths(11), aggregationDate12M.minusYears(1)));
        List<TurnoverValueObject> objectsCurrentYearAll = prepareTestObjects(LocalDateInterval.including(aggregationDate12M.minusMonths(11), aggregationDate12M));
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
            final boolean cC,
            final BigDecimal gP,
            final BigDecimal nP,
            final Integer tcP,
            final boolean cP,
            final boolean c
    ){
        Assertions.assertThat(afp.getGrossAmount()).isEqualTo(g);
        Assertions.assertThat(afp.getNetAmount()).isEqualTo(n);
        Assertions.assertThat(afp.getTurnoverCount()).isEqualTo(tc);
        Assertions.assertThat(afp.isNonComparableThisYear()).isEqualTo(cC);
        Assertions.assertThat(afp.getGrossAmountPreviousYear()).isEqualTo(gP);
        Assertions.assertThat(afp.getNetAmountPreviousYear()).isEqualTo(nP);
        Assertions.assertThat(afp.getTurnoverCountPreviousYear()).isEqualTo(tcP);
        Assertions.assertThat(afp.isNonComparablePreviousYear()).isEqualTo(cP);
        Assertions.assertThat(afp.isComparable()).isEqualTo(c);
    }

    private List<TurnoverValueObject> prepareTestObjects(final LocalDateInterval interval){
        List<TurnoverValueObject> result = new ArrayList<>();
        LocalDate date = interval.startDate();
        int cnt = 1;
        while (!date.isAfter(interval.endDate())){
            Turnover t = new Turnover();
            t.setDate(date);
            t.setGrossAmount(BigDecimal.valueOf(cnt));
            t.setNetAmount(BigDecimal.valueOf(cnt).subtract(new BigDecimal("0.50")));
            t.setNonComparable(false);
            t.setPurchaseCount(BigInteger.valueOf(cnt));
            TurnoverValueObject val = new TurnoverValueObject(t);
            result.add(val);

            date = date.plusMonths(1);
            cnt++;
        }
        return result;
    }



}