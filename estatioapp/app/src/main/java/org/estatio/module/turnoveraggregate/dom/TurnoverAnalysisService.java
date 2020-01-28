package org.estatio.module.turnoveraggregate.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.client.util.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.dom.aggregation.AggregationPattern;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLink;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLinkRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TurnoverAnalysisService {

    public static Logger LOG = LoggerFactory.getLogger(TurnoverAnalysisService.class);

    /**
     * This method returns a collection of reports on turnover config level intended for aggregation maintenance and calculation
     * From the config (t.i. occupancy) point of view it tries to find the toplevel parent lease and 'walk the graph' over all previous child leases
     *
     * @param lease
     * @param frequency
     * @return
     */
    List<AggregationAnalysisReportForConfig> analyze(final Lease lease, final Type type, final Frequency frequency){
        if (TurnoverAggregationService.guard(type, frequency, "No create-aggregation-reports implementation for type %s found.",
                "No create-aggregation-reports for frequency %s found."))
            return Collections.EMPTY_LIST;

        List<AggregationAnalysisReportForConfig> result = new ArrayList<>();

        // 1. generate reports

        // find top level lease
        Lease l = lease;
        while (l.getNext() != null){
            l = (Lease) l.getNext();
        }

        result.addAll(reportsForConfigTypeAndFrequency(l, type, frequency, true));

        l = (Lease) l.getPrevious();
        while (l!=null){
            result.addAll(reportsForConfigTypeAndFrequency(l, type, frequency,false));
            l = (Lease) l.getPrevious();
        }

        // 2. determine aggregation pattern

        for (AggregationAnalysisReportForConfig report : result){
            final AggregationPattern aggregationPattern = determineAggregationPatternForConfig(result,
                    report.getTurnoverReportingConfig());
            report.setAggregationPattern(aggregationPattern);
        }

        // 3. calculate aggregation date TODO: .. is now done in step 1

        // 4. calculate collection of configs to include
        setCollectionOfConfigsToInclude(result);

        return result;
    }

    public void setCollectionOfConfigsToInclude(final List<AggregationAnalysisReportForConfig> reports){

        // first add the direct predecessors
        for (AggregationAnalysisReportForConfig report : reports){

            switch (report.getAggregationPattern()){

            case ONE_TO_ONE:
                if (report.getPreviousLease()!=null){
                    final AggregationAnalysisReportForConfig reportPrevLease = reports.stream()
                            .filter(r -> r.getTurnoverReportingConfig().getOccupancy().getLease().equals(report.getPreviousLease())).findFirst().orElse(null);
                    if (reportPrevLease!=null){ // should not happen
                        report.getConfigsToIncludeInAggregation().add(reportPrevLease.getTurnoverReportingConfig());
                    }
                }
                if (report.getPreviousOnSameUnit()!=null && !report.getConfigsToIncludeInAggregation().contains(report.getPreviousOnSameUnit())) report.getConfigsToIncludeInAggregation().add(report.getPreviousOnSameUnit());
                if (!report.getPreviousOnOtherUnit().isEmpty()) {
                    report.getPreviousOnOtherUnit().forEach(r->{
                        if (!report.getConfigsToIncludeInAggregation().contains(r)) report.getConfigsToIncludeInAggregation().add(r);
                    });
                }
                break;

            case MANY_TO_ONE:
                if (report.getPreviousLease()!=null){ // should not happen
                    final List<AggregationAnalysisReportForConfig> reportsPrevLease = reports.stream()
                            .filter(r -> r.getTurnoverReportingConfig().getOccupancy().getLease().equals(report.getPreviousLease())).collect(Collectors.toList());
                    if (!reportsPrevLease.isEmpty()){ // should not happen
                        report.getConfigsToIncludeInAggregation().addAll(
                                reportsPrevLease.stream().map(r->r.getTurnoverReportingConfig()).collect(Collectors.toList())
                        );
                    }
                }
                if (report.getPreviousOnSameUnit()!=null && !report.getConfigsToIncludeInAggregation().contains(report.getPreviousOnSameUnit())) report.getConfigsToIncludeInAggregation().add(report.getPreviousOnSameUnit());
                if (!report.getPreviousOnOtherUnit().isEmpty()) {
                    report.getPreviousOnOtherUnit().forEach(r->{
                        if (!report.getConfigsToIncludeInAggregation().contains(r)) report.getConfigsToIncludeInAggregation().add(r);
                    });
                }
                break;

            case ONE_TO_MANY_SAME_LEASE:
            case ONE_TO_MANY:
            case MANY_TO_MANY:
                getTurnoverReportingConfigLinks(report).forEach(l->{
                    report.getConfigsToIncludeInAggregation().add(l.getAggregationChild());
                });
                break;

            default:
                LOG.warn(String.format("No implementation found for aggregation pattern %s on report for configuration %s", report.getAggregationPattern(), report.getTurnoverReportingConfig().toString()));
                break;

            }

        }

        // "walk the graph" and add own config
        for (AggregationAnalysisReportForConfig report : reports){
            getAllConfigsToInclude(report, reports).forEach(c->{
                if (!report.getConfigsToIncludeInAggregation().contains(c)){
                    report.getConfigsToIncludeInAggregation().add(c);
                }
            });
            if (!report.getConfigsToIncludeInAggregation().contains(report.getTurnoverReportingConfig())) report.getConfigsToIncludeInAggregation().add(report.getTurnoverReportingConfig());
        }

    }

    /**
     * NOTE: recursive method
     */
    List<TurnoverReportingConfig> getAllConfigsToInclude(
            final AggregationAnalysisReportForConfig report,
            final List<AggregationAnalysisReportForConfig> reports
            ) {
        List<TurnoverReportingConfig> result = new ArrayList<>();
        result.addAll(report.getConfigsToIncludeInAggregation());
        for (TurnoverReportingConfig c : report.getConfigsToIncludeInAggregation()){
            final AggregationAnalysisReportForConfig reportForC = reportFor(c, reports);
            final List<TurnoverReportingConfig> childConfigsForC = getAllConfigsToInclude(reportForC, reports);
            if (!childConfigsForC.isEmpty()) result.addAll(childConfigsForC);
        }
        return result;
    }

    AggregationAnalysisReportForConfig reportFor(final TurnoverReportingConfig config, final List<AggregationAnalysisReportForConfig> reports){
        return reports.stream().filter(r->r.getTurnoverReportingConfig().equals(config)).findFirst().orElse(null);
    }

    List<TurnoverReportingConfigLink> getTurnoverReportingConfigLinks(final AggregationAnalysisReportForConfig report) {
        return turnoverReportingConfigLinkRepository
                .findByTurnoverReportingConfig(report.getTurnoverReportingConfig());
    }

    List<AggregationAnalysisReportForConfig> reportsForConfigTypeAndFrequency(final Lease l, final Type type, final Frequency frequency, final boolean isToplevelLease) {
        List<AggregationAnalysisReportForConfig> result = new ArrayList<>();
        final List<List<TurnoverReportingConfig>> listOfLists = Lists.newArrayList(l.getOccupancies()).stream()
                .map(o -> turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(o, type, frequency))
                .collect(Collectors.toList());
        for (List<TurnoverReportingConfig> list : listOfLists) {

            if (!list.isEmpty()) {

                final TurnoverReportingConfig config = list.get(0);
                final Occupancy occupancy = config.getOccupancy();
                final Lease previousLease = (Lease) config.getOccupancy().getLease().getPrevious();

                AggregationAnalysisReportForConfig report = new AggregationAnalysisReportForConfig(config);
                report.setPreviousLease(previousLease);

                // find parallel occs
                if (l.hasOverlappingOccupancies()) {
                    for (Occupancy oc : l.getOccupancies()) {

                        if ((!oc.equals(occupancy)) && oc.getEffectiveInterval().overlaps(
                                occupancy.getEffectiveInterval())) {
                            final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository
                                    .findByOccupancyAndTypeAndFrequency(oc, type, frequency);
                            if (!configs.isEmpty()) {
                                report.getParallelConfigs().add(configs.get(0));
                                if (configs.get(0).getOccupancy().getUnit().equals(occupancy.getUnit())) {
                                    report.getParallelOnSameUnit().add(configs.get(0));
                                }
                            }
                        }
                    }
                }

                // find previous
                if (occupancy.getStartDate() != null && occupancy
                        .getStartDate().isAfter(l.getEffectiveInterval().startDate())) {
                    final List<Occupancy> prevOnOtherUnit = Lists.newArrayList(l.getOccupancies()).stream()
                            .filter(occ -> !occ.equals(occupancy))
                            .filter(occ -> !occ.getUnit().equals(occupancy.getUnit()))
                            .filter(occ -> occ.getEndDate() != null)
                            .filter(occ -> occ.getEndDate().isBefore(occupancy.getStartDate()))
                            .collect(Collectors.toList());
                    prevOnOtherUnit.forEach(o->{
                        final List<TurnoverReportingConfig> configsForO = turnoverReportingConfigRepository
                                .findByOccupancyAndTypeAndFrequency(o, type, frequency);
                        if (!configsForO.isEmpty()) report.getPreviousOnOtherUnit().addAll(configsForO);
                    });
                    final Optional<Occupancy> prevOnSameUnit = Lists.newArrayList(l.getOccupancies()).stream()
                            .filter(occ -> !occ.equals(occupancy))
                            .filter(occ -> occ.getUnit().equals(occupancy.getUnit()))
                            .filter(occ -> occ.getEndDate() != null)
                            .filter(occ -> occ.getEndDate().isBefore(occupancy.getStartDate())).findFirst();
                    if (prevOnSameUnit.isPresent()) {
                        final List<TurnoverReportingConfig> configs2 = turnoverReportingConfigRepository
                                .findByOccupancyAndTypeAndFrequency(prevOnSameUnit.get(), type, frequency);
                        if (!configs2.isEmpty()) report.setPreviousOnSameUnit(configs2.get(0));
                    }
                }
                // find next
                if (occupancy.getEndDate() != null) {
                    final List<Occupancy> nextOnOtherUnit = Lists.newArrayList(l.getOccupancies()).stream()
                            .filter(occ -> !occ.equals(occupancy))
                            .filter(occ -> !occ.getUnit().equals(occupancy.getUnit()))
                            .filter(occ -> occ.getStartDate() != null)
                            .filter(occ -> occ.getStartDate().isAfter(occupancy.getEndDate()))
                            .collect(Collectors.toList());
                    nextOnOtherUnit.forEach(o->{
                        final List<TurnoverReportingConfig> configsForO = turnoverReportingConfigRepository
                                .findByOccupancyAndTypeAndFrequency(o, type, frequency);
                        if (!configsForO.isEmpty()) report.getNextOnOtherUnit().addAll(configsForO);
                    });
                    final Optional<Occupancy> next = Lists.newArrayList(l.getOccupancies()).stream()
                            .filter(occ -> !occ.equals(occupancy))
                            .filter(occ -> occ.getUnit().equals(occupancy.getUnit()))
                            .filter(occ -> occ.getStartDate() != null)
                            .filter(occ -> occ.getStartDate().isAfter(occupancy.getEndDate()))
                            .findFirst();
                    if (next.isPresent()){
                        final List<TurnoverReportingConfig> configs3 = turnoverReportingConfigRepository
                                .findByOccupancyAndTypeAndFrequency(next.get(), type, frequency);
                        if (!configs3.isEmpty()) report.setNextOnSameUnit(configs3.get(0));
                    }
                }

                // determine dates for aggregations
                // TODO: check with users! Currently within a lease, we check the effective enddate of the occupancy. If the occupancy end date is on or later than the first of the next month we include
                // However: when checking with a previous lease, the current SQL agent logic is different: when a lease terminates in the month we examine, then the date is included (Actially there is a mistake in de sql
                // that makes a lease ending on the last day of the month not to include the date
                // The SQL is in fn_GetLeaseDetailForTurnover; de selectie in de code WHEN l.LeaseTerminationDate < DATEADD(d,-1,DATEADD(mm,1,@Date)) --Terminate date is before
                // @DATE is the calculation date, so in our case the first of the month
                // example: @Date = 1-8-2019 Then lease with a next ending before 31-8-2019 get 1-8-2019 aggregation; lease ending on 31-8-2019 'hands it over' to the parent lease
                boolean isToplevelOccupancy = isToplevelLease && report.getNextOnSameUnit() == null && report.getNextOnOtherUnit().isEmpty();
                report.setToplevel(isToplevelOccupancy);
                report.getAggregationDates().addAll(
                        aggregationDatesForTurnoverReportingConfig(config, isToplevelOccupancy));

                result.add(report);

            }
        }

        return result;
    }

    List<LocalDate> aggregationDatesForTurnoverReportingConfig(
            final TurnoverReportingConfig config,
            final boolean toplevel) {

        //TODO: also check the aggegration strategy of the config!!! (Especially in case previous lease...)

        LocalDate startDateToUse;
        LocalDate startDateOrNull = null;
        try {
            startDateOrNull = config.getOccupancy().getEffectiveInterval().startDate();
        } catch (Exception e) {
            LOG.warn(String.format("Problem with config %s", config.toString()));
            LOG.warn(e.getMessage());
        }
        if (startDateOrNull == null) {
            startDateToUse = TurnoverAggregationService.MIN_AGGREGATION_DATE;
        } else {
            Lease previousLease = (Lease) config.getOccupancy().getLease().getPrevious();
            LocalDate effectiveEndPreviousLease = previousLease!=null ? previousLease.getEffectiveInterval().endDate() : null;
            if (config.getOccupancy().getLease().getPrevious() != null && effectiveEndPreviousLease != null){  // second check should not be needed!!
                // we may need to exclude an aggregation date unless the previous lease ends on the last of the month
                LocalDate endOfPreviousMonth = startDateOrNull.withDayOfMonth(1).minusDays(1);
                if (effectiveEndPreviousLease.equals(endOfPreviousMonth)){
                    startDateToUse = startDateOrNull.withDayOfMonth(1);
                } else {
                    startDateToUse = startDateOrNull.withDayOfMonth(1).plusMonths(1);
                }
            } else {
                startDateToUse = startDateOrNull.withDayOfMonth(1);
            }
        }
        if (startDateToUse.isBefore(TurnoverAggregationService.MIN_AGGREGATION_DATE)) {
            startDateToUse = TurnoverAggregationService.MIN_AGGREGATION_DATE;
        }

        LocalDate effectiveEndDateOcc = config.getOccupancy().getEffectiveEndDate();

        LocalDate endDateToUse;
        if (config.getOccupancy().getLease().getNext() != null
                && effectiveEndDateOcc != null) { // second check should not be needed!!
            // We need to check the effective enddate of the lease for a 'handover' to the next lease

            // if effectiveEndDateOcc is on the last of the month or later, do not include (the next lease should pick it up)
            // f.e.
            // end date to use for 31-8-2019 should be 31-7-2019
            // end date to use for 30-8-2019 should be any date this month (we pick effectiveEndDateOcc)
            LocalDate endOfTheMonth = effectiveEndDateOcc.withDayOfMonth(1).plusMonths(1).minusDays(1);
            if (effectiveEndDateOcc.equals(endOfTheMonth)) {
                endDateToUse = effectiveEndDateOcc.minusMonths(1);
            } else {
                endDateToUse = effectiveEndDateOcc;
            }

        } else {

            if (effectiveEndDateOcc == null)
                effectiveEndDateOcc = clockService.now();
            endDateToUse = toplevel ? effectiveEndDateOcc.plusMonths(23) : effectiveEndDateOcc;
        }

        List<LocalDate> result = new ArrayList<>();
        while (!endDateToUse.isBefore(startDateToUse)) {
            result.add(startDateToUse);
            startDateToUse = startDateToUse.plusMonths(1);
        }
        return result;
    }

    AggregationPattern determineAggregationPatternForConfig(final List<AggregationAnalysisReportForConfig> reports, final TurnoverReportingConfig turnoverReportingConfig){

        final AggregationAnalysisReportForConfig reportForConfig = reports.stream()
                .filter(r -> r.getTurnoverReportingConfig().equals(turnoverReportingConfig)).findFirst().orElse(null);

        if (reportForConfig==null){
            // should not happen
            return null;
        }

        final Lease previous = (Lease) reportForConfig.getPreviousLease();
        final AggregationAnalysisReportForConfig reportForPrevious = reports.stream()
                .filter(r -> r.getTurnoverReportingConfig().getOccupancy().getLease().equals(previous))
                .findFirst().orElse(null);

        if (reportForConfig.getParallelConfigs().isEmpty()){

            // no par configs on this lease

            if (previous ==null || reportForPrevious ==null){
                // no previous lease or no prev occs on prev lease
                return AggregationPattern.ONE_TO_ONE;
            }
            if (reportForPrevious.getParallelConfigs().isEmpty()){
                // no par configs (occs) on prev lease
                return AggregationPattern.ONE_TO_ONE;
            } else {
                // par configs (occs) on prev lease
                return AggregationPattern.MANY_TO_ONE;
            }

        } else {
            if (previous ==null || reportForPrevious ==null){
                // check if there is a  previous config (occ) on the same lease for both this and the parallel one
                if (!reportForConfig.getPreviousOnOtherUnit().isEmpty()){
                    //TODO: refine this
                    // Check if at least 1 occ ends before at least two start
                    return AggregationPattern.ONE_TO_MANY_SAME_LEASE;
                } else {
                    return AggregationPattern.ONE_TO_ONE;
                }
            }
            // multiple par occs on this lease
            if (reportForPrevious.getParallelConfigs().isEmpty()){
                // no par occs on prev lease
                return AggregationPattern.ONE_TO_MANY;
            } else {
                // par occs on prev lease
                return AggregationPattern.MANY_TO_MANY;
            }

        }

    }


    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject ClockService clockService;

    @Inject TurnoverReportingConfigLinkRepository turnoverReportingConfigLinkRepository;
}
