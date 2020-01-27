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
            List<AggregationAnalysisReportForConfig> reps = service
                    .reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, false);

            // then
            assertThat(l.getOccupancies()).hasSize(2);
            assertThat(reps).hasSize(2);
            validateReport(reps.get(0), o2cfg, 4, 0, 0, null, 0,null, 1,false);
            validateReport(reps.get(1), o1cfg, 3, 0, 0, null, 1, null, 0, false);

            // when toplevel lease
            reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, true);
            validateReport(reps.get(0), o2cfg, 27, 0, 0, null, 0, null, 1, true);
            validateReport(reps.get(1), o1cfg, 3, 0, 0, null, 1, null, 0, false);

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
            List<AggregationAnalysisReportForConfig> reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, false);
            //then
            assertThat(reps).hasSize(3);
            validateReport(reps.get(0), o3cfg, 4, 1, 0,  null,0, o1cfg, 0, false);
            validateReport(reps.get(1), o2cfg, 4, 1, 0, null, 0, null, 1, false);
            validateReport(reps.get(2), o1cfg, 3, 0, 0, o3cfg, 1, null, 0, false);
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
            List<AggregationAnalysisReportForConfig> reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY,false);
            // then
            assertThat(reps).hasSize(4);
            validateReport(reps.get(0), o3cfg, 4, 2, 1, null,0, o1cfg, 0,false);
            validateReport(reps.get(1), o2cfg, 4, 2, 0, null, 0,null, 1,false);
            validateReport(reps.get(2), o4cfg, 7, 3, 2, null, 0,null, 0,false);
            validateReport(reps.get(3), o1cfg, 3, 1, 1, o3cfg, 1,null, 0,false);

            // and when same for toplevel lease
            reps = service.reportsForOccupancyTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY,true);
            assertThat(reps).hasSize(4);
            validateReport(reps.get(0), o3cfg, 27, 2, 1, null, 0,o1cfg, 0,true);
            validateReport(reps.get(1), o2cfg, 27, 2, 0, null, 0,null, 1,true);
            validateReport(reps.get(2), o4cfg, 30, 3, 2, null, 0,null, 0,true);
            validateReport(reps.get(3), o1cfg, 3, 1, 1, o3cfg, 1,null, 0,false);
        }

        private void validateReport(final AggregationAnalysisReportForConfig r, final TurnoverReportingConfig cfg, int datSize, int parSize, int parSameUnit, TurnoverReportingConfig next, int nextSize, TurnoverReportingConfig prev, int prevSize, final boolean toplevel){
            assertThat(r.getTurnoverReportingConfig()).isEqualTo(cfg);
            assertThat(r.getAggregationDates().size()).isEqualTo(datSize);
            assertThat(r.getParallelConfigs().size()).isEqualTo(parSize);
            assertThat(r.getParallelOnSameUnit().size()).isEqualTo(parSameUnit);
            assertThat(r.getNextOnSameUnit()).isEqualTo(next);
            assertThat(r.getNextOnOtherUnit().size()).isEqualTo(nextSize);
            assertThat(r.getPreviousOnSameUnit()).isEqualTo(prev);
            assertThat(r.getPreviousOnOtherUnit().size()).isEqualTo(prevSize);
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
            List<AggregationAnalysisReportForConfig> reportsForOccupancyTypeAndFrequency(
                    final Lease l,
                    final Type type,
                    final Frequency frequency,
                    final boolean isToplevelLease) {
                Occupancy o = new Occupancy();
                o.setLease(l);
                TurnoverReportingConfig config = new TurnoverReportingConfig();
                config.setOccupancy(o);
                final AggregationAnalysisReportForConfig report = new AggregationAnalysisReportForConfig(config);
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
        final List<AggregationAnalysisReportForConfig> aggregationReports = service
                .analyze(lease, Type.PRELIMINARY, Frequency.MONTHLY);
        // then
        assertThat(aggregationReports).hasSize(3);
        final AggregationAnalysisReportForConfig r1 = aggregationReports.get(0);
        assertThat(r1.getTurnoverReportingConfig().getOccupancy().getLease()).isEqualTo(next);
        assertThat(r1.isToplevel()).isTrue();
        final AggregationAnalysisReportForConfig r2 = aggregationReports.get(1);
        assertThat(r2.getTurnoverReportingConfig().getOccupancy().getLease()).isEqualTo(lease);
        assertThat(r2.isToplevel()).isFalse();
        final AggregationAnalysisReportForConfig r3 = aggregationReports.get(2);
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
        Lease lease = new Lease();
        occupancy.setLease(lease);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setOccupancy(occupancy);
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForTurnoverReportingConfig(config, false);
        assertThat(noToplevel).hasSize(14);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1));
        assertThat(noToplevel.get(13)).isEqualTo(endDate.withDayOfMonth(1));
        final List<LocalDate> forTopLevel = service.aggregationDatesForTurnoverReportingConfig(config, true);
        assertThat(forTopLevel).hasSize(37);
        assertThat(forTopLevel.get(36)).isEqualTo(endDate.plusMonths(23).withDayOfMonth(1));
    }

    @Test
    public void aggregationDatesForOccupancy_works_with_previous_lease_not_ending_on_last_day_of_previous_month() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2019, 1, 5);
        final LocalDate endDate = new LocalDate(2020, 2, 1);
        TurnoverAggregationService service = new TurnoverAggregationService();
        Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        Lease lease = new Lease();
        Lease previous = new Lease(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(null, new LocalDate(2018,11,30));
            }
        };
        lease.setPrevious(previous);
        occupancy.setLease(lease);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setOccupancy(occupancy);
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForTurnoverReportingConfig(config, false);
        assertThat(noToplevel).hasSize(13);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1).plusMonths(1));
        assertThat(noToplevel.get(12)).isEqualTo(endDate.withDayOfMonth(1));
        final List<LocalDate> forTopLevel = service.aggregationDatesForTurnoverReportingConfig(config, true);
        assertThat(forTopLevel).hasSize(36);
        assertThat(forTopLevel.get(35)).isEqualTo(endDate.plusMonths(23).withDayOfMonth(1));
    }

    @Test
    public void aggregationDatesForOccupancy_works_with_previous_lease_ending_on_last_day_of_previous_month() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2019, 1, 5);
        final LocalDate endDate = new LocalDate(2020, 2, 1);
        TurnoverAggregationService service = new TurnoverAggregationService();
        Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        Lease lease = new Lease();
        Lease previous = new Lease(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(null, new LocalDate(2018,12,31));
            }
        };
        lease.setPrevious(previous);
        occupancy.setLease(lease);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setOccupancy(occupancy);
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForTurnoverReportingConfig(config, false);
        assertThat(noToplevel).hasSize(14);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1));
        assertThat(noToplevel.get(13)).isEqualTo(endDate.withDayOfMonth(1));
        final List<LocalDate> forTopLevel = service.aggregationDatesForTurnoverReportingConfig(config, true);
        assertThat(forTopLevel).hasSize(37);
        assertThat(forTopLevel.get(36)).isEqualTo(endDate.plusMonths(23).withDayOfMonth(1));
    }

    @Test
    public void aggregationDatesForOccupancy_works_with_next_lease_and_current_occ_not_ending_on_last_day_of_month() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2019, 1, 5);
        final LocalDate endDate = new LocalDate(2020, 2, 1);
        TurnoverAggregationService service = new TurnoverAggregationService();
        Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        Lease lease = new Lease();
        Lease next = new Lease();
        lease.setNext(next);
        occupancy.setLease(lease);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setOccupancy(occupancy);
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForTurnoverReportingConfig(config, false);
        assertThat(noToplevel).hasSize(14);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1));
        assertThat(noToplevel.get(13)).isEqualTo(endDate.withDayOfMonth(1));
    }

    @Test
    public void aggregationDatesForOccupancy_works_with_next_lease_and_occ_ending_on_last_day_of_month() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2019, 1, 5);
        final LocalDate endDate = new LocalDate(2020, 2, 29);
        TurnoverAggregationService service = new TurnoverAggregationService();
        Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        Lease lease = new Lease();
        Lease next = new Lease();
        lease.setNext(next);
        occupancy.setLease(lease);
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setOccupancy(occupancy);
        // when, then
        final List<LocalDate> noToplevel = service.aggregationDatesForTurnoverReportingConfig(config, false);
        assertThat(noToplevel).hasSize(13);
        assertThat(noToplevel.get(0)).isEqualTo(startDate.withDayOfMonth(1));
        assertThat(noToplevel.get(12)).isEqualTo(endDate.withDayOfMonth(1).minusMonths(1));
    }

    @Test
    public void determineApplicationStrategyForConfig_works() throws Exception {

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        List<AggregationAnalysisReportForConfig> reports = new ArrayList<>();

        // when, then
        assertThat(service.determineAggregationPatternForConfig(reports , config)).isNull();

        // and when
        Occupancy occupancy = new Occupancy();
        Lease lease = new Lease();
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);
        AggregationAnalysisReportForConfig rep = new AggregationAnalysisReportForConfig(config);
        reports.add(rep);

        // then no previous lease
        assertThat(service.determineAggregationPatternForConfig(reports , config)).isEqualTo(AggregationPattern.ONE_TO_ONE);

        // and when prev without
        Lease prev = new Lease();
        lease.setPrevious(prev);
        // then still
        assertThat(service.determineAggregationPatternForConfig(reports , config)).isEqualTo(AggregationPattern.ONE_TO_ONE);

        // and when prev with one then still
        TurnoverReportingConfig prevOcc1Cfg = new TurnoverReportingConfig();
        Occupancy prevOcc1 = new Occupancy();
        prevOcc1.setLease(prev);
        prevOcc1Cfg.setOccupancy(prevOcc1);
        rep.setPreviousLease(prev);
        AggregationAnalysisReportForConfig repForPrev = new AggregationAnalysisReportForConfig(prevOcc1Cfg);
        reports.add(repForPrev);
        // then
        assertThat(service.determineAggregationPatternForConfig(reports , config)).isEqualTo(AggregationPattern.ONE_TO_ONE);

        // and when prev with many then
        repForPrev.getParallelConfigs().add(new TurnoverReportingConfig());
        // then
        assertThat(service.determineAggregationPatternForConfig(reports , config)).isEqualTo(AggregationPattern.MANY_TO_ONE);

        // and when current has many and prev as well
        rep.getParallelConfigs().add(new TurnoverReportingConfig());
        // then
        assertThat(service.determineAggregationPatternForConfig(reports , config)).isEqualTo(AggregationPattern.MANY_TO_MANY);

        // and case current has many and prev has one
        List<AggregationAnalysisReportForConfig> reports2 = new ArrayList<>();
        Lease prev2 = new Lease();
        Occupancy occForPrev = new Occupancy();
        occForPrev.setLease(prev2);
        TurnoverReportingConfig configPrev2 = new TurnoverReportingConfig();
        configPrev2.setOccupancy(occForPrev);
        AggregationAnalysisReportForConfig repForPrev2 = new AggregationAnalysisReportForConfig(configPrev2);
        lease.setPrevious(prev2);
        rep.setPreviousLease(prev2);
        reports2.add(rep);
        reports2.add(repForPrev2);
        // then
        assertThat(service.determineAggregationPatternForConfig(reports2 , config)).isEqualTo(AggregationPattern.ONE_TO_MANY);

    }

    @Mock TurnoverAggregationRepository mockturnoverAggregationRepository;

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

    @Mock ClockService mockClockService;

    @Test
    public void aggregationDatesForTurnoverReportingConfig_works() throws Exception {

        // given
        final LocalDate now = new LocalDate(2019, 2, 3);
        TurnoverAggregationService service = new TurnoverAggregationService();
        final LocalDate occEffectiveEndDate = new LocalDate(2019, 2, 3);
        final LocalDate occStartDate = new LocalDate(2018,12,2);

        final TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(occStartDate, occEffectiveEndDate);
            }
        };
        final Lease lease = new Lease();
        lease.setTenancyEndDate(now); // lease is ended on aggregationDate
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);

        // when
        config.setFrequency(Frequency.MONTHLY);

        // then when config is toplevel
        List<LocalDate> dates = service.aggregationDatesForTurnoverReportingConfig(config, true);
        assertThat(dates).hasSize(26);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));
        assertThat(dates.get(25)).isEqualTo(new LocalDate(2021,1,1));

        // and when config is not toplevel
        dates = service.aggregationDatesForTurnoverReportingConfig(config, false);
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
        final LocalDate occStartDate = new LocalDate(2018,12,2);

        final TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Occupancy occupancy = new Occupancy(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(occStartDate, null);
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

        // then
        List<LocalDate> dates = service.aggregationDatesForTurnoverReportingConfig(config, true);
        assertThat(dates).hasSize(26);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));
        assertThat(dates.get(25)).isEqualTo(new LocalDate(2021,1,1));

        // and when
        dates = service.aggregationDatesForTurnoverReportingConfig(config, false);
        // then
        assertThat(dates).hasSize(3);
        assertThat(dates.get(0)).isEqualTo(new LocalDate(2018,12,1));
        assertThat(dates.get(1)).isEqualTo(new LocalDate(2019,1,1));
        assertThat(dates.get(2)).isEqualTo(new LocalDate(2019,2,1));

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

    @Test
    public void getCalculationPeriodForAggregations_works() throws Exception {

        LocalDate startDate;
        LocalDate endDate;

        // given
        TurnoverAggregationService service = new TurnoverAggregationService();
        service.clockService = mockClockService;

        // when
        startDate = new LocalDate(2020, 1, 2);
        endDate = new LocalDate(2020, 3, 10);
        // then
        assertThat(service.getCalculationPeriodForAggregations(startDate, endDate).toString()).isEqualTo("2020-01-01/2020-03-02");

        // when
        startDate=null;
        // then
        assertThat(service.getCalculationPeriodForAggregations(startDate, endDate)).isEqualTo(LocalDateInterval.including(TurnoverAggregationService.MIN_AGGREGATION_DATE, endDate.withDayOfMonth(1)));

        // expect
        final LocalDate now = new LocalDate(2020, 2, 10);
        context.checking(new Expectations(){{
            allowing(mockClockService).now();
            will(returnValue(now));
        }});

        // when
        startDate=new LocalDate(2020, 1, 2);
        endDate=null;
        // then
        assertThat(service.getCalculationPeriodForAggregations(startDate, endDate).toString()).isEqualTo("2020-01-01/2022-01-02");

        // when
        startDate = null;
        // then
        assertThat(service.getCalculationPeriodForAggregations(startDate, endDate)).isEqualTo(LocalDateInterval.including(TurnoverAggregationService.MIN_AGGREGATION_DATE, now.plusMonths(23).withDayOfMonth(1)));

        // when
        endDate = TurnoverAggregationService.MIN_AGGREGATION_DATE.minusMonths(1);
        // then
        assertThat(service.getCalculationPeriodForAggregations(startDate, endDate)).isEqualTo(LocalDateInterval.including(TurnoverAggregationService.MIN_AGGREGATION_DATE, TurnoverAggregationService.MIN_AGGREGATION_DATE));
    }

}