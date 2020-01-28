package org.estatio.module.turnoveraggregate.dom;

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
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.dom.aggregation.AggregationPattern;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLink;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverAnalysisService_Test {

    public static class ReportsForConfigTypeAndFrequencyTests {

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

        TurnoverAnalysisService service;

        @Before
        public void setUp(){
            startDate = new LocalDate(2020, 1, 1);
            endDate = new LocalDate(2024, 12, 31);
            l = new Lease() {
                @Override public LocalDateInterval getEffectiveInterval() {
                    return LocalDateInterval.including(startDate, endDate);
                }
            };
            service = new TurnoverAnalysisService();
            service.turnoverReportingConfigRepository = mockTurnoverReportingConfigRepository;
        }

        @Test
        public void reportsForConfigTypeAndFrequency_2_occs_on_different_units_works() throws Exception {

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
                    .reportsForConfigTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, false);

            // then
            assertThat(l.getOccupancies()).hasSize(2);
            assertThat(reps).hasSize(2);
            validateReport(reps.get(0), o2cfg, 4, 0, 0, null, 0,null, 1,false);
            validateReport(reps.get(1), o1cfg, 3, 0, 0, null, 1, null, 0, false);

            // when toplevel lease
            reps = service.reportsForConfigTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, true);
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
            List<AggregationAnalysisReportForConfig> reps = service.reportsForConfigTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY, false);
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
            List<AggregationAnalysisReportForConfig> reps = service.reportsForConfigTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY,false);
            // then
            assertThat(reps).hasSize(4);
            validateReport(reps.get(0), o3cfg, 4, 2, 1, null,0, o1cfg, 0,false);
            validateReport(reps.get(1), o2cfg, 4, 2, 0, null, 0,null, 1,false);
            validateReport(reps.get(2), o4cfg, 7, 3, 2, null, 0,null, 0,false);
            validateReport(reps.get(3), o1cfg, 3, 1, 1, o3cfg, 1,null, 0,false);

            // and when same for toplevel lease
            reps = service.reportsForConfigTypeAndFrequency(l, Type.PRELIMINARY, Frequency.MONTHLY,true);
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
        TurnoverAnalysisService service = new TurnoverAnalysisService(){
            @Override
            List<AggregationAnalysisReportForConfig> reportsForConfigTypeAndFrequency(
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock ClockService mockClockService;

    @Test
    public void aggregationDatesForTurnoverReportingConfig_works() throws Exception {

        // given
        final LocalDate now = new LocalDate(2019, 2, 3);
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
        TurnoverAnalysisService service = new TurnoverAnalysisService();
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
    public void getConfigsToInclude_works(){

        // given
        TurnoverAnalysisService service = new TurnoverAnalysisService();
        TurnoverReportingConfig topConfig = new TurnoverReportingConfig();
        AggregationAnalysisReportForConfig topReport = new AggregationAnalysisReportForConfig(topConfig);
        List<AggregationAnalysisReportForConfig> reports = new ArrayList<>();
        reports.add(topReport);

        // when
        List<TurnoverReportingConfig> result = service.getAllConfigsToInclude(topReport, reports);
        // then
        assertThat(result).hasSize(0);

        // when
        TurnoverReportingConfig childConfig1 = new TurnoverReportingConfig();
        AggregationAnalysisReportForConfig childReport1 = new AggregationAnalysisReportForConfig(childConfig1);
        topReport.getConfigsToIncludeInAggregation().add(childConfig1);
        reports.add(childReport1);
        result = service.getAllConfigsToInclude(topReport, reports);
        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(childConfig1);

        TurnoverReportingConfig childConfig2 = new TurnoverReportingConfig();
        AggregationAnalysisReportForConfig childReport2 = new AggregationAnalysisReportForConfig(childConfig2);
        topReport.getConfigsToIncludeInAggregation().add(childConfig2);
        reports.add(childReport2);
        result = service.getAllConfigsToInclude(topReport, reports);
        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(childConfig1);
        assertThat(result).contains(childConfig2);

        // when
        TurnoverReportingConfig childOfChild1Config1 = new TurnoverReportingConfig();
        AggregationAnalysisReportForConfig childOfChild1Report1 = new AggregationAnalysisReportForConfig(childOfChild1Config1);
        childReport1.getConfigsToIncludeInAggregation().add(childOfChild1Config1);
        reports.add(childOfChild1Report1);
        result = service.getAllConfigsToInclude(topReport, reports);
        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains(childConfig1);
        assertThat(result).contains(childConfig2);
        assertThat(result).contains(childOfChild1Config1);

    }

    @Test
    public void setCollectionOfConfigsToInclude_works_when_no_config_links_needed() throws Exception {

        // given
        TurnoverAnalysisService service = new TurnoverAnalysisService();
        List<AggregationAnalysisReportForConfig> reports = new ArrayList<>();

        final Lease lease1 = new Lease();
        final Occupancy occ1 = new Occupancy();
        occ1.setLease(lease1);
        final TurnoverReportingConfig cfg1 = new TurnoverReportingConfig();
        cfg1.setOccupancy(occ1);
        final AggregationAnalysisReportForConfig rep1 = setupReport(cfg1, AggregationPattern.ONE_TO_ONE);
        reports.add(rep1);

        // when
        service.setCollectionOfConfigsToInclude(reports);
        // then
        assertThat(rep1.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(cfg1);

        // reset
        rep1.getConfigsToIncludeInAggregation().clear();

        // and when
        final Lease previousLease = new Lease();
        final Occupancy prevOcc = new Occupancy();
        prevOcc.setLease(previousLease);
        final TurnoverReportingConfig prevConfig = new TurnoverReportingConfig();
        prevConfig.setOccupancy(prevOcc);
        final AggregationAnalysisReportForConfig repPrev = setupReport(prevConfig, AggregationPattern.ONE_TO_ONE);
        reports.add(repPrev);
        rep1.setPreviousLease(previousLease);
        service.setCollectionOfConfigsToInclude(reports);
        // then
        assertThat(rep1.getConfigsToIncludeInAggregation()).hasSize(2);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(cfg1);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(prevConfig);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).contains(prevConfig);

        // reset
        rep1.getConfigsToIncludeInAggregation().clear();
        repPrev.getConfigsToIncludeInAggregation().clear();

        // and when (many-to-one pattern)
        final Lease previousOfPrev = new Lease();
        final Occupancy prevOfprevOcc1 = new Occupancy();
        prevOfprevOcc1.setLease(previousOfPrev);
        final TurnoverReportingConfig prevOfPrevConfig1 = new TurnoverReportingConfig();
        prevOfPrevConfig1.setOccupancy(prevOfprevOcc1);
        final Occupancy prevOfprevOcc2 = new Occupancy();
        prevOfprevOcc2.setLease(previousOfPrev);
        final TurnoverReportingConfig prevOfPrevConfig2 = new TurnoverReportingConfig();
        prevOfPrevConfig2.setOccupancy(prevOfprevOcc2);
        repPrev.setPreviousLease(previousOfPrev);
        repPrev.setAggregationPattern(AggregationPattern.MANY_TO_ONE); // we change the pattern of the parent to include both children
        final AggregationAnalysisReportForConfig repPrevOfPrev1 = setupReport(prevOfPrevConfig1, AggregationPattern.ONE_TO_ONE);
        final AggregationAnalysisReportForConfig repPrevOfPrev2 = setupReport(prevOfPrevConfig2, AggregationPattern.ONE_TO_ONE);
        reports.add(repPrevOfPrev1);
        reports.add(repPrevOfPrev2);
        service.setCollectionOfConfigsToInclude(reports);

        // then
        assertThat(rep1.getConfigsToIncludeInAggregation()).hasSize(4);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(cfg1);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(prevConfig);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig1);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig2);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).hasSize(3);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).contains(prevConfig);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig1);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig2);
        assertThat(repPrevOfPrev1.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(repPrevOfPrev1.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig1);
        assertThat(repPrevOfPrev2.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(repPrevOfPrev2.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig2);

        // reset
        rep1.getConfigsToIncludeInAggregation().clear();
        repPrev.getConfigsToIncludeInAggregation().clear();
        repPrevOfPrev1.getConfigsToIncludeInAggregation().clear();
        repPrevOfPrev2.getConfigsToIncludeInAggregation().clear();

        // and when (scenario previous on other units)
        final Occupancy prevOfprevOcc3 = new Occupancy();
        prevOfprevOcc3.setLease(previousOfPrev);
        TurnoverReportingConfig prevOfPrevConfig3 = new TurnoverReportingConfig();
        prevOfPrevConfig3.setOccupancy(prevOfprevOcc3);
        final AggregationAnalysisReportForConfig repPrevOfPrev3 = setupReport(prevOfPrevConfig3, AggregationPattern.ONE_TO_ONE);
        reports.add(repPrevOfPrev3);
        repPrev.getPreviousOnOtherUnit().add(prevOfPrevConfig3);
        service.setCollectionOfConfigsToInclude(reports);

        // then
        assertThat(rep1.getConfigsToIncludeInAggregation()).hasSize(5);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig3);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).hasSize(4);
        assertThat(repPrev.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig3);
        assertThat(repPrevOfPrev3.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(repPrevOfPrev3.getConfigsToIncludeInAggregation()).contains(prevOfPrevConfig3);

    }

    private AggregationAnalysisReportForConfig setupReport(final TurnoverReportingConfig config, final AggregationPattern aggregationPattern){
        final AggregationAnalysisReportForConfig report = new AggregationAnalysisReportForConfig(config);
        report.setAggregationPattern(aggregationPattern);
        return report;
    }

    @Test
    public void setCollectionOfConfigsToInclude_works_when_config_links_needed() throws Exception {

        // given
        List<TurnoverReportingConfigLink> links = new ArrayList<>();
        TurnoverAnalysisService service = new TurnoverAnalysisService(){
            @Override List<TurnoverReportingConfigLink> getTurnoverReportingConfigLinks(final AggregationAnalysisReportForConfig report) {
                return links;
            }
        };
        List<AggregationAnalysisReportForConfig> reports = new ArrayList<>();

        final Lease lease1 = new Lease();
        final Occupancy occ1 = new Occupancy();
        occ1.setLease(lease1);
        final TurnoverReportingConfig cfg1 = new TurnoverReportingConfig();
        cfg1.setOccupancy(occ1);
        final AggregationAnalysisReportForConfig rep1 = setupReport(cfg1, AggregationPattern.ONE_TO_MANY);
        reports.add(rep1);

        // when no links
        service.setCollectionOfConfigsToInclude(reports);
        // then
        assertThat(rep1.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(cfg1);

        // reset
        rep1.getConfigsToIncludeInAggregation().clear();
        // when
        final TurnoverReportingConfig cfgChild = new TurnoverReportingConfig();
        final AggregationAnalysisReportForConfig repChild = setupReport(cfgChild, AggregationPattern.ONE_TO_ONE);
        reports.add(repChild);
        final TurnoverReportingConfigLink link = new TurnoverReportingConfigLink(cfg1, cfgChild);
        links.add(link);
        service.setCollectionOfConfigsToInclude(reports);
        // then
        assertThat(rep1.getConfigsToIncludeInAggregation()).hasSize(2);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(cfg1);
        assertThat(rep1.getConfigsToIncludeInAggregation()).contains(cfgChild);
        assertThat(repChild.getConfigsToIncludeInAggregation()).hasSize(1);
        assertThat(repChild.getConfigsToIncludeInAggregation()).contains(cfgChild);

    }

}