package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregationService_Test {

    @Test
    public void createAggregationReports_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override
            List<AggregationReportForOccupancy> reportsForOccupancy(
                    final Lease l,
                    final boolean isToplevelLease) {
                Occupancy o = new Occupancy();
                o.setLease(l);
                final AggregationReportForOccupancy report = new AggregationReportForOccupancy(o);
                // hack to recognize if method is called with toplevelLease true
                if (isToplevelLease) report.setPreviousOnSameUnit(o);
                return Arrays.asList(report);
            }
        };

        Lease lease = new Lease();
        lease.setReference("lease");
        Lease next = new Lease();
        next.setReference("parent");
        Lease prev = new Lease();
        prev.setReference("child");
        lease.setNext(next);
        lease.setPrevious(prev);
        next.setPrevious(lease);
        prev.setNext(lease);

        // when
        final List<AggregationReportForOccupancy> aggregationReports = service
                .createAggregationReports(lease, Frequency.MONTHLY);
        // then
        assertThat(aggregationReports).hasSize(3);
        final AggregationReportForOccupancy r1 = aggregationReports.get(0);
        assertThat(r1.getOccupancy().getLease()).isEqualTo(next);
        assertThat(r1.getPreviousOnSameUnit()).isNotNull();
        final AggregationReportForOccupancy r2 = aggregationReports.get(1);
        assertThat(r2.getOccupancy().getLease()).isEqualTo(lease);
        assertThat(r2.getPreviousOnSameUnit()).isNull();
        final AggregationReportForOccupancy r3 = aggregationReports.get(2);
        assertThat(r3.getOccupancy().getLease()).isEqualTo(prev);
        assertThat(r3.getPreviousOnSameUnit()).isNull();
    }

    @Test
    public void reportsForOccupancy_works() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2024, 12, 31);
        Lease l = new Lease(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when 2 occs on different units
        final Unit u1 = newUnit("u1");
        final Occupancy o1 = newOcc(startDate, new LocalDate(2020, 3, 31), u1, l);
        l.getOccupancies().add(o1);
        final Occupancy o2 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), newUnit("u2"), l);
        l.getOccupancies().add(o2);
        List<AggregationReportForOccupancy> reps = service.reportsForOccupancy(l, false);

        // then
        assertThat(l.getOccupancies()).hasSize(2);
        assertThat(reps).hasSize(2);
        validateReport(reps.get(0),o2,4, 0, 0,null, null);
        validateReport(reps.get(1),o1,3, 0, 0,null, null);

        // when toplevel lease
        reps = service.reportsForOccupancy(l, true);
        validateReport(reps.get(0),o2,28, 0, 0,null, null);
        validateReport(reps.get(1),o1,27, 0, 0,null, null);

        //when pararallel occs but not on same unit
        final Occupancy o3 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), u1, l);
        l.getOccupancies().add(o3);
        assertThat(l.getOccupancies()).hasSize(3);
        reps = service.reportsForOccupancy(l, false);
        //then
        assertThat(reps).hasSize(3);
        validateReport(reps.get(0), o3, 4, 1, 0, null, o1);
        validateReport(reps.get(1), o2, 4, 1, 0, null, null);
        validateReport(reps.get(2), o1, 3, 0, 0, o3, null);

        //when pararallel occs on same unit
        final Occupancy o4 = newOcc(startDate.plusDays(1), new LocalDate(2020, 7, 31), u1, l);
        l.getOccupancies().add(o4);
        assertThat(l.getOccupancies()).hasSize(4);
        reps = service.reportsForOccupancy(l, false);
        // then
        assertThat(reps).hasSize(4);
        validateReport(reps.get(0), o3, 4, 2, 1, null, o1);
        validateReport(reps.get(1), o2, 4, 2, 0, null, null);
        validateReport(reps.get(2), o4, 7, 3, 2, null, null);
        validateReport(reps.get(3), o1, 3, 1, 1, o3, null);

        // and when same for toplevel lease
        reps = service.reportsForOccupancy(l, true);
        assertThat(reps).hasSize(4);
        validateReport(reps.get(0), o3, 28, 2, 1, null, o1);
        validateReport(reps.get(1), o2, 28, 2, 0, null, null);
        validateReport(reps.get(2), o4, 31, 3, 2, null, null);
        validateReport(reps.get(3), o1, 3, 1, 1, o3, null);

    }

    private void validateReport(final AggregationReportForOccupancy r, final Occupancy o, int datSize, int parSize, int parSameUnit, Occupancy next, Occupancy prev){
        assertThat(r.getOccupancy()).isEqualTo(o);
        assertThat(r.getAggregationDates().size()).isEqualTo(datSize);
        assertThat(r.getParallelOccupancies().size()).isEqualTo(parSize);
        assertThat(r.getParallelOnSameUnit().size()).isEqualTo(parSameUnit);
        assertThat(r.getNextOnSameUnit()).isEqualTo(next);
        assertThat(r.getPreviousOnSameUnit()).isEqualTo(prev);
    }

    private Unit newUnit(final String name){
        final Unit unit = new Unit();
        unit.setName(name);
        return unit;
    }

    private Occupancy newOcc(final LocalDate startDate, final LocalDate endDate, final Unit unit, final Lease lease){
        final Occupancy occupancy = new Occupancy();
        occupancy.setStartDate(startDate);
        occupancy.setEndDate(endDate);
        occupancy.setUnit(unit);
        occupancy.setLease(lease);
        return occupancy;
    }

    @Test
    public void aggregationDatesForOccupancy_works() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2019, 1, 5);
        final LocalDate endDate = new LocalDate(2020, 2, 1);
        TurnoverAggregationService service = new TurnoverAggregationService();
        Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForOccupancy(occupancy, false);
        assertThat(noToplevel).hasSize(14);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1));
        assertThat(noToplevel.get(13)).isEqualTo(endDate);
        final List<LocalDate> forTopLevel = service.aggregationDatesForOccupancy(occupancy, true);
        assertThat(forTopLevel).hasSize(38);
        assertThat(forTopLevel.get(37)).isEqualTo(endDate.plusMonths(24));
    }

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
        assertThat(aggregateForPeriod.getTurnoverCount()).isEqualTo(2);
        assertThat(aggregateForPeriod.getTurnoverCountPreviousYear()).isEqualTo(1);

        assertThat(aggregateForPeriod.getGrossAmount()).isEqualTo(new BigDecimal("123.45"));
        assertThat(aggregateForPeriod.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        assertThat(aggregateForPeriod.getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("100.23"));
        assertThat(aggregateForPeriod.getNetAmountPreviousYear()).isEqualTo(new BigDecimal("99.99"));

        assertThat(aggregateForPeriod.isNonComparableThisYear()).isEqualTo(false);
        assertThat(aggregateForPeriod.isNonComparablePreviousYear()).isEqualTo(false);
        assertThat(aggregateForPeriod.isComparable()).isEqualTo(false);

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
        assertThat(turnoverAggregateToDate.getTurnoverCount()).isEqualTo(2);
        assertThat(turnoverAggregateToDate.getTurnoverCountPreviousYear()).isEqualTo(1);

        assertThat(turnoverAggregateToDate.getGrossAmount()).isEqualTo(new BigDecimal("123.45"));
        assertThat(turnoverAggregateToDate.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        assertThat(turnoverAggregateToDate.getGrossAmountPreviousYear()).isEqualTo(new BigDecimal("100.23"));
        assertThat(turnoverAggregateToDate.getNetAmountPreviousYear()).isEqualTo(new BigDecimal("99.99"));

        assertThat(turnoverAggregateToDate.isNonComparableThisYear()).isEqualTo(false);
        assertThat(turnoverAggregateToDate.isNonComparablePreviousYear()).isEqualTo(false);
        assertThat(turnoverAggregateToDate.isComparable()).isEqualTo(false);

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
        assertThat(purchaseCountAggregateForPeriod.getCount()).isEqualTo(new BigInteger("357"));
        assertThat(purchaseCountAggregateForPeriod.getCountPreviousYear()).isEqualTo(new BigInteger("345"));
        assertThat(purchaseCountAggregateForPeriod.isComparable()).isEqualTo(false);

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
        assertThat(aggregation.getGrossAmount1MCY_1()).isEqualTo(new BigDecimal("123.45"));
        assertThat(aggregation.getNetAmount1MCY_1()).isEqualTo(new BigDecimal("111.11"));
        assertThat(aggregation.getGrossAmount1MCY_2()).isEqualTo(new BigDecimal("100.23"));
        assertThat(aggregation.getNetAmount1MCY_2()).isEqualTo(new BigDecimal("99.99"));
        assertThat(aggregation.getComments12MCY()).isEqualTo("xxxyyy");
        assertThat(aggregation.getComments12MPY()).isEqualTo("zzz");
    }

    @Test
    public void containsNonComparableTurnover_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        List<Turnover> turnoverList = Arrays.asList();
        // then
        assertThat(service.containsNonComparableTurnover(turnoverList)).isFalse();

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
        assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, false, false)).isTrue();
        assertThat(service.isComparable(AggregationPeriod.P_2M, 3, 2, false, false)).isTrue();
        assertThat(service.isComparable(AggregationPeriod.P_2M, 1, 2, false, false)).isFalse();
        assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 1, false, false)).isFalse();
        assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, true, false)).isFalse();
        assertThat(service.isComparable(AggregationPeriod.P_2M, 2, 2, false, true)).isFalse();

        assertThat(service.isComparable(AggregationPeriod.P_2M, 0, 2, false, true)).isFalse();
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
        assertThat(service.leasesToExamine(l3)).hasSize(1);
        assertThat(service.leasesToExamine(l3)).contains(l3);
        assertThat(service.leasesToExamine(l2)).hasSize(2);
        assertThat(service.leasesToExamine(l3)).doesNotContain(l1);
        assertThat(service.leasesToExamine(l1)).hasSize(3);
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
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(0);

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
        assertThat(l1.getOccupancies()).hasSize(3);
        assertThat(service.noOverlapOccupanciesOnLease(l1)).isFalse();
        leases = Arrays.asList(l1);
        // then
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(2);
        assertThat(service.occupanciesToExamine(sameUnit, leases)).doesNotContain(occOnOtherUnit1);

        // when no overlap, 2 on same unit, 1 on other unit
        occOnOtherUnit1.setEndDate(new LocalDate(2018,12, 1));
        occOnSameUnit1.setStartDate(new LocalDate(2018, 12, 2));
        occOnSameUnit1.setEndDate(new LocalDate(2018, 12, 31));
        assertThat(l1.getOccupancies()).hasSize(3);
        assertThat(service.noOverlapOccupanciesOnLease(l1)).isTrue();
        leases = Arrays.asList(l1);
        // then
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(3);
        assertThat(service.occupanciesToExamine(sameUnit, leases)).contains(occOnOtherUnit1);

        // when no occupancies on same sameUnit and exactly one occupancy on other sameUnit
        Lease l2 = new Lease();
        Occupancy occOnOtherUnit2 = new Occupancy();
        occOnOtherUnit2.setUnit(otherUnit);
        l2.getOccupancies().add(occOnOtherUnit2);
        assertThat(l2.getOccupancies()).hasSize(1);
        assertThat(service.noOverlapOccupanciesOnLease(l2)).isTrue();
        leases = Arrays.asList(l2);
        // then
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(1);

        // when multiple leases
        leases = Arrays.asList(l1, l2);
        // then
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(4);

        // when no occupancies on same sameUnit and multiple occupancies on other sameUnit and overlap
        Lease l3 = new Lease();
        Occupancy occOnOtherUnit3 = new Occupancy();
        occOnOtherUnit3.setUnit(otherUnit);
        Occupancy occOnOtherUnit4 = new Occupancy();
        occOnOtherUnit4.setUnit(otherUnit);
        occOnOtherUnit4.setStartDate(new LocalDate(2019,1,1)); // to differentiate in sorted set
        l3.getOccupancies().addAll(Arrays.asList(occOnOtherUnit3, occOnOtherUnit4));
        assertThat(l3.getOccupancies()).hasSize(2);
        assertThat(service.noOverlapOccupanciesOnLease(l3)).isFalse();
        leases = Arrays.asList(l3);
        // then
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(0);

        // when no occupancies on same sameUnit and multiple occupancies on other sameUnit and no overlap
        occOnOtherUnit3.setEndDate(new LocalDate(2018,12,31));
        assertThat(l3.getOccupancies()).hasSize(2);
        assertThat(service.noOverlapOccupanciesOnLease(l3)).isTrue();
        // then
        assertThat(service.occupanciesToExamine(sameUnit, leases)).hasSize(2);

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
        assertThat(dates).hasSize(27);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));
        assertThat(dates.get(26)).isEqualTo(new LocalDate(2021,2,1));

        // and when lease is active
        lease.setTenancyEndDate(null);
        dates = service.aggregationDatesForTurnoverReportingConfig(config, now);
        // then
        assertThat(dates).hasSize(3);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));

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
        assertThat(dates).hasSize(27);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));
        assertThat(dates.get(26)).isEqualTo(new LocalDate(2021,2,1));

        // and when lease is active
        lease.setTenancyEndDate(null);
        dates = service.aggregationDatesForTurnoverReportingConfig(config, now);
        // then
        assertThat(dates).hasSize(3);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));

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
        assertThat(turnoverValueObjects.size()).isEqualTo(3);
        TurnoverValueObject tov1 = turnoverValueObjects.get(0);
        TurnoverValueObject tov2 = turnoverValueObjects.get(1);
        TurnoverValueObject tov3 = turnoverValueObjects.get(2);
        assertThat(tov1.getDate()).isEqualTo(t4.getDate());
        assertThat(tov2.getDate()).isEqualTo(t3.getDate());
        assertThat(tov2.getDate()).isEqualTo(t2.getDate());
        assertThat(tov3.getDate()).isEqualTo(t1.getDate());

        assertThat(tov2.getComments()).isEqualTo("t3 comment | t2 comment");

        // and when
        aggregationDate = new LocalDate(2022, 3, 1);
        turnoverValueObjects = service
                .turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        // then
        assertThat(turnoverValueObjects.size()).isEqualTo(1);
        assertThat(turnoverValueObjects.get(0).getDate()).isEqualTo(t1.getDate());

        // and when
        aggregationDate = new LocalDate(2022, 3, 2);
        turnoverValueObjects = service
                .turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        // then
        assertThat(turnoverValueObjects.size()).isEqualTo(0);
    }

    @Test
    public void determineStartDate_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();

        // when
        LocalDate aggregationDate = null;
        LocalDate minStartDateOnTurnovers = null;
        // then
        assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(service.MIN_AGGREGATION_DATE);

        // and when
        minStartDateOnTurnovers = new LocalDate(2012, 1, 1);
        assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(service.MIN_AGGREGATION_DATE.minusMonths(24));

        // and when
        aggregationDate = new LocalDate(2013, 12, 31);
        assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(minStartDateOnTurnovers);

        // and when
        aggregationDate = new LocalDate(2014, 1, 1);
        assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(aggregationDate.minusMonths(24));
        assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(minStartDateOnTurnovers);

        // and when
        aggregationDate = new LocalDate(2014, 1, 2);
        assertThat(service.determineStartDate(aggregationDate, minStartDateOnTurnovers)).isEqualTo(aggregationDate.minusMonths(24));

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
        assertThat(service.determineEndDate(occupancy, aggregationDate)).isEqualTo(aggregationDate.plusMonths(24));

        // and when
        aggregationDate = new LocalDate(2010, 1, 2);
        assertThat(service.determineEndDate(occupancy, aggregationDate)).isEqualTo(occEffectiveEndDate.plusMonths(24));

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

        assertThat(turnoverValueObjects).hasSize(4);

        service.calculateAggregationForOther(aggregation, turnoverValueObjects);

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
        assertThat(afp.getGrossAmount()).isEqualTo(g);
        assertThat(afp.getNetAmount()).isEqualTo(n);
        assertThat(afp.getTurnoverCount()).isEqualTo(tc);
        assertThat(afp.isNonComparableThisYear()).isEqualTo(cC);
        assertThat(afp.getGrossAmountPreviousYear()).isEqualTo(gP);
        assertThat(afp.getNetAmountPreviousYear()).isEqualTo(nP);
        assertThat(afp.getTurnoverCountPreviousYear()).isEqualTo(tcP);
        assertThat(afp.isNonComparablePreviousYear()).isEqualTo(cP);
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
        assertThat(afp.getGrossAmount()).isEqualTo(g);
        assertThat(afp.getNetAmount()).isEqualTo(n);
        assertThat(afp.getTurnoverCount()).isEqualTo(tc);
        assertThat(afp.isNonComparableThisYear()).isEqualTo(cC);
        assertThat(afp.getGrossAmountPreviousYear()).isEqualTo(gP);
        assertThat(afp.getNetAmountPreviousYear()).isEqualTo(nP);
        assertThat(afp.getTurnoverCountPreviousYear()).isEqualTo(tcP);
        assertThat(afp.isNonComparablePreviousYear()).isEqualTo(cP);
        assertThat(afp.isComparable()).isEqualTo(c);
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