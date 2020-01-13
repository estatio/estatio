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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.AggregationStrategy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAggregationService_Test {

    public static class ReportsForOccupancyTypeAndFrequencyTests {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        TurnoverReportingConfigRepository mockTurnoverReportingConfigRepository;

        LocalDate startDate;
        LocalDate endDate;
        Lease l;
        Unit u1;
        Unit u2;
        Occupancy o1;
        TurnoverReportingConfig o1cfg;
        Occupancy o2;
        TurnoverReportingConfig o2cfg;
        Occupancy o3;
        TurnoverReportingConfig o3cfg;
        Occupancy o4;
        TurnoverReportingConfig o4cfg;

        TurnoverAggregationService service;

        @Before
        public void setUp(){
            startDate = new LocalDate(2020, 1, 1);
            endDate = new LocalDate(2024, 12, 31);
            l = new Lease() {
                @Override public LocalDateInterval getEffectiveInterval() {
                    return LocalDateInterval.including(startDate, endDate);
                }
            };
            service = new TurnoverAggregationService();
            service.turnoverReportingConfigRepository = mockTurnoverReportingConfigRepository;
        }

        @Test
        public void reportsForOccupancyTypeAndFrequency_2_occs_on_different_units_works() throws Exception {

            // given
            u1 = newUnit("u1");
            o1 = newOcc(startDate, new LocalDate(2020, 3, 31), u1, l);
            l.getOccupancies().add(o1);
            o1cfg = new TurnoverReportingConfig();
            o1cfg.setOccupancy(o1);
            o2 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), newUnit("u2"), l);
            l.getOccupancies().add(o2);
            o2cfg = new TurnoverReportingConfig();
            o2cfg.setOccupancy(o2);

            //expect
            context.checking(new Expectations() {{
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o1, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o1cfg)));
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o2, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o2cfg)));
            }});

            // when 2 occs on different units
            List<AggregationReportForConfig> reps = service
                    .reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, false);

            // then
            assertThat(l.getOccupancies()).hasSize(2);
            assertThat(reps).hasSize(2);
            validateReport(reps.get(0), o2cfg, 4, 0, 0, null, null, false);
            validateReport(reps.get(1), o1cfg, 3, 0, 0, null, null, false);

            // when toplevel lease
            reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, true);
            validateReport(reps.get(0), o2cfg, 28, 0, 0, null, null, true);
            validateReport(reps.get(1), o1cfg, 27, 0, 0, null, null, true);

        }

        @Test
        public void reportsForOccupancyTypeAndFrequency_par_occs_on_different_units_works() throws Exception {

            // given
            u1 = newUnit("u1");
            o1 = newOcc(startDate, new LocalDate(2020, 3, 31), u1, l);
            l.getOccupancies().add(o1);
            o1cfg = new TurnoverReportingConfig();
            o1cfg.setOccupancy(o1);
            u2 = newUnit("u2");
            o2 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), u2, l);
            l.getOccupancies().add(o2);
            o2cfg = new TurnoverReportingConfig();
            o2cfg.setOccupancy(o2);
            o3 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), u1, l);
            l.getOccupancies().add(o3);
            o3cfg = new TurnoverReportingConfig();
            o3cfg.setOccupancy(o3);

            //expect
            context.checking(new Expectations() {{
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o1, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o1cfg)));
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o2, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o2cfg)));
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o3, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o3cfg)));
            }});

            // when pararallel occs but not on same unit
            assertThat(l.getOccupancies()).hasSize(3);
            List<AggregationReportForConfig> reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, false);
            //then
            assertThat(reps).hasSize(3);
            validateReport(reps.get(0), o3cfg, 4, 1, 0, null, o1cfg, false);
            validateReport(reps.get(1), o2cfg, 4, 1, 0, null, null, false);
            validateReport(reps.get(2), o1cfg, 3, 0, 0, o3cfg, null, false);
        }

        @Test
        public void reportsForOccupancyTypeAndFrequency_par_occs_on_same_unit_works() throws Exception {

            // given
            u1 = newUnit("u1");
            o1 = newOcc(startDate, new LocalDate(2020, 3, 31), u1, l);
            l.getOccupancies().add(o1);
            o1cfg = new TurnoverReportingConfig();
            o1cfg.setOccupancy(o1);
            u2 = newUnit("u2");
            o2 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), u2, l);
            l.getOccupancies().add(o2);
            o2cfg = new TurnoverReportingConfig();
            o2cfg.setOccupancy(o2);
            o3 = newOcc(new LocalDate(2020, 4, 1), new LocalDate(2020, 7, 31), u1, l);
            l.getOccupancies().add(o3);
            o3cfg = new TurnoverReportingConfig();
            o3cfg.setOccupancy(o3);
            o4 = newOcc(startDate.plusDays(1), new LocalDate(2020, 7, 31), u1, l);
            l.getOccupancies().add(o4);
            o4cfg = new TurnoverReportingConfig();
            o4cfg.setOccupancy(o4);

            //expect
            context.checking(new Expectations() {{
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o1, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o1cfg)));
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o2, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o2cfg)));
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o3, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o3cfg)));
                allowing(mockTurnoverReportingConfigRepository)
                        .findByOccupancyAndTypeAndFrequency(o4, Type.PRELIMINARY, Frequency.MONTHLY);
                will(returnValue(Arrays.asList(o4cfg)));
            }});

            //when pararallel occs on same unit
            assertThat(l.getOccupancies()).hasSize(4);
            List<AggregationReportForConfig> reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY,false);
            // then
            assertThat(reps).hasSize(4);
            validateReport(reps.get(0), o3cfg, 4, 2, 1, null, o1cfg, false);
            validateReport(reps.get(1), o2cfg, 4, 2, 0, null, null, false);
            validateReport(reps.get(2), o4cfg, 7, 3, 2, null, null, false);
            validateReport(reps.get(3), o1cfg, 3, 1, 1, o3cfg, null, false);

            // and when same for toplevel lease
            reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY,true);
            assertThat(reps).hasSize(4);
            validateReport(reps.get(0), o3cfg, 28, 2, 1, null, o1cfg, true);
            validateReport(reps.get(1), o2cfg, 28, 2, 0, null, null, true);
            validateReport(reps.get(2), o4cfg, 31, 3, 2, null, null, true);
            validateReport(reps.get(3), o1cfg, 3, 1, 1, o3cfg, null, false);
        }

        private void validateReport(final AggregationReportForConfig r, final TurnoverReportingConfig cfg, int datSize, int parSize, int parSameUnit, TurnoverReportingConfig next, TurnoverReportingConfig prev, final boolean toplevel){
            assertThat(r.getTurnoverReportingConfig()).isEqualTo(cfg);
            assertThat(r.getAggregationDates().size()).isEqualTo(datSize);
            assertThat(r.getParallelOccupancies().size()).isEqualTo(parSize);
            assertThat(r.getParallelOnSameUnit().size()).isEqualTo(parSameUnit);
            assertThat(r.getNextOnSameUnit()).isEqualTo(next);
            assertThat(r.getPreviousOnSameUnit()).isEqualTo(prev);
            assertThat(r.isToplevel()).isEqualTo(toplevel);
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
    }


    @Test
    public void createAggregationReports_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService(){
            @Override
            List<AggregationReportForConfig> reportsForOccupancyTypeAndFrequency(
                    final Lease l,
                    final Type type,
                    final Frequency frequency,
                    final boolean isToplevelLease) {
                Occupancy o = new Occupancy();
                o.setLease(l);
                TurnoverReportingConfig config = new TurnoverReportingConfig();
                config.setOccupancy(o);
                final AggregationReportForConfig report = new AggregationReportForConfig(config);
                if (isToplevelLease) report.setToplevel(true);
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
        final List<AggregationReportForConfig> aggregationReports = service
                .createAggregationReports(lease, Type.PRELIMINARY, Frequency.MONTHLY);
        // then
        assertThat(aggregationReports).hasSize(3);
        final AggregationReportForConfig r1 = aggregationReports.get(0);
        assertThat(r1.getTurnoverReportingConfig().getOccupancy().getLease()).isEqualTo(next);
        assertThat(r1.isToplevel()).isTrue();
        final AggregationReportForConfig r2 = aggregationReports.get(1);
        assertThat(r2.getTurnoverReportingConfig().getOccupancy().getLease()).isEqualTo(lease);
        assertThat(r2.isToplevel()).isFalse();
        final AggregationReportForConfig r3 = aggregationReports.get(2);
        assertThat(r3.getTurnoverReportingConfig().getOccupancy().getLease()).isEqualTo(prev);
        assertThat(r3.isToplevel()).isFalse();
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
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setOccupancy(occupancy);
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForTurnoverReportingConfig(config, false);
        assertThat(noToplevel).hasSize(14);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1));
        assertThat(noToplevel.get(13)).isEqualTo(endDate);
        final List<LocalDate> forTopLevel = service.aggregationDatesForTurnoverReportingConfig(config, true);
        assertThat(forTopLevel).hasSize(38);
        assertThat(forTopLevel.get(37)).isEqualTo(endDate.plusMonths(24));
    }

    @Test
    public void determineApplicationStrategyForConfig_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        List<AggregationReportForConfig> reports = new ArrayList<>();

        // when, then
        assertThat(service.determineApplicationStrategyForConfig(reports , config)).isNull();

        // and when
        Occupancy occupancy = new Occupancy();
        Lease lease = new Lease();
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);
        AggregationReportForConfig rep = new AggregationReportForConfig(config);
        reports.add(rep);

        // then no previous lease
        assertThat(service.determineApplicationStrategyForConfig(reports , config)).isEqualTo(AggregationStrategy.SIMPLE);

        // and when prev without
        Lease prev = new Lease();
        lease.setPrevious(prev);
        // then still
        assertThat(service.determineApplicationStrategyForConfig(reports , config)).isEqualTo(AggregationStrategy.SIMPLE);

        // and when prev with one then still
        TurnoverReportingConfig prevOcc1Cfg = new TurnoverReportingConfig();
        Occupancy prevOcc1 = new Occupancy();
        prevOcc1.setLease(prev);
        prevOcc1Cfg.setOccupancy(prevOcc1);
        AggregationReportForConfig repForPrev = new AggregationReportForConfig(prevOcc1Cfg);
        reports.add(repForPrev);
        // then
        assertThat(service.determineApplicationStrategyForConfig(reports , config)).isEqualTo(AggregationStrategy.SIMPLE);

        // and when prev with many then
        repForPrev.getParallelOccupancies().add(new TurnoverReportingConfig());
        // then
        assertThat(service.determineApplicationStrategyForConfig(reports , config)).isEqualTo(AggregationStrategy.PREVIOUS_MANY_OCCS_TO_ONE);

        // and when current has many and prev as well
        rep.getParallelOccupancies().add(new TurnoverReportingConfig());
        // then
        assertThat(service.determineApplicationStrategyForConfig(reports , config)).isEqualTo(AggregationStrategy.PREVIOUS_MANY_OCCS_TO_MANY);

        // and case current has many and prev has one
        List<AggregationReportForConfig> reports2 = new ArrayList<>();
        Lease prev2 = new Lease();
        Occupancy occForPrev = new Occupancy();
        occForPrev.setLease(prev2);
        TurnoverReportingConfig configPrev2 = new TurnoverReportingConfig();
        configPrev2.setOccupancy(occForPrev);
        AggregationReportForConfig repForPrev2 = new AggregationReportForConfig(configPrev2);
        lease.setPrevious(prev2);
        reports2.add(rep);
        reports2.add(repForPrev2);
        // then
        assertThat(service.determineApplicationStrategyForConfig(reports2 , config)).isEqualTo(AggregationStrategy.PREVIOUS_ONE_OCC_TO_MANY);

    }

    @Mock TurnoverAggregationRepository mockturnoverAggregationRepository;

    @Mock TurnoverAggregation mockAggregation;

    @Test
    public void maintainTurnoverAggregationsForConfig_works() throws Exception {

        // when
        TurnoverAggregationService service = new TurnoverAggregationService();
        service.turnoverAggregationRepository = mockturnoverAggregationRepository;
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        AggregationReportForConfig report = new AggregationReportForConfig(config);
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

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock TurnoverRepository mockTurnoverRepo;

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
    @Ignore
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
        List<Turnover> turnoverValueObjects = service
                .turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        // then
        assertThat(turnoverValueObjects.size()).isEqualTo(3);
        Turnover tov1 = turnoverValueObjects.get(0);
        Turnover tov2 = turnoverValueObjects.get(1);
        Turnover tov3 = turnoverValueObjects.get(2);
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
    @Ignore
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
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when only this year
        List<Turnover> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objectsCurYear);
        // then
        assertAggregateForPeriod(afp, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when only last year
        List<Turnover> objectsPreviousYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculateTurnoverAggregateForPeriod(afp, aggregationDate, objectsPreviousYear);
        // then
        assertAggregateForPeriod(afp,  BigDecimal.ZERO,  BigDecimal.ZERO,0,false,
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
    @Ignore
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
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("1"), BigInteger.ZERO, false);

        // and when only this year
        List<Turnover> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusMonths(11), aggregationDate));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objectsCurYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, new BigInteger("23"), BigInteger.ZERO, false);

        // and when only last year
        List<Turnover> objectsPreviousYear = prepareTestObjects(LocalDateInterval.including(aggregationDate.minusYears(1).minusMonths(11), aggregationDate.minusYears(1)));
        service.calculatePurchaseCountAggregateForPeriod(pafp, aggregationDate, objectsPreviousYear);
        // then
        assertPurchaseCountAggregateForPeriod(pafp, BigInteger.ZERO, new BigInteger("23"), false);

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
    @Ignore
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
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when 2 M only this year
        final LocalDate aggregationDate2M = new LocalDate(2020, 2, 1);
        List<Turnover> objectsCurYear = prepareTestObjects(LocalDateInterval.including(aggregationDate2M.minusMonths(11), aggregationDate2M));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, objectsCurYear);
        // then
        assertAggregateToDate(tad, new BigDecimal("23"), new BigDecimal("22.00"),2,false,
                BigDecimal.ZERO, BigDecimal.ZERO,0, false, false);

        // and when 2 M only last year
        List<Turnover> objectsLastYear = prepareTestObjects(LocalDateInterval.including(aggregationDate2M.minusYears(1).minusMonths(11), aggregationDate2M.minusYears(1)));
        service.calculateTurnoverAggregateToDate(tad, aggregationDate2M, objectsLastYear);
        // then
        assertAggregateToDate(tad,  BigDecimal.ZERO,  BigDecimal.ZERO, 0,false,
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