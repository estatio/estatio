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

import org.estatio.module.agreement.dom.Agreement;
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

        // 3. calculate aggregation dates
        result.forEach(r->{
            r.getAggregationDates().addAll(aggregationDatesForTurnoverReportingConfig(r));
        });


        // 4. calculate collection of configs to include
        setCollectionOfConfigsToInclude(result);

        return result;
    }

    void setCollectionOfConfigsToInclude(final List<AggregationAnalysisReportForConfig> reports){

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
                Lease nextLease = null;
                if (config.getOccupancy().getLease().getNext()!=null) {
                    nextLease = (Lease) config.getOccupancy().getLease().getNext();
                }
                AggregationAnalysisReportForConfig report = new AggregationAnalysisReportForConfig(config);
                report.setPreviousLease(previousLease);
                report.setNextLease(nextLease);

                // find parallel occs
                if (l.hasOverlappingOccupancies()) {
                    for (Occupancy oc : l.getOccupancies()) {

                        if ((!oc.equals(occupancy)) && oc.getEffectiveInterval().overlaps(
                                occupancy.getEffectiveInterval())) {
                            final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository
                                    .findByOccupancyAndTypeAndFrequency(oc, type, frequency);
                            if (!configs.isEmpty()) {
                                report.getParallelConfigs().add(configs.get(0));
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

                boolean isToplevelOccupancy = isToplevelLease && report.getNextOnSameUnit() == null && report.getNextOnOtherUnit().isEmpty();
                report.setToplevel(isToplevelOccupancy);
                result.add(report);

            }
        }

        return result;
    }

    /**
     * NOTE: this method (and the methods called by it) should be a reflection of the talks with the users on the rules they prefer (https://docs.google.com/presentation/d/1LklFa3fdX7gl5BaHwmm7f0iERh6IGRuADxQxWy1scpA)
     * @param report
     * @return
     */
    List<LocalDate> aggregationDatesForTurnoverReportingConfig(
            final AggregationAnalysisReportForConfig report) {

        LocalDate startDateToUse = getStartDateToUse(report).withDayOfMonth(1); // the method should always return first day of month, but just in case ...

        LocalDate endDateToUse = getEndDateToUse(report);

        List<LocalDate> result = new ArrayList<>();
        while (!endDateToUse.isBefore(startDateToUse)) {
            switch (report.getTurnoverReportingConfig().getFrequency()){
            case MONTHLY:
                result.add(startDateToUse);
                startDateToUse = startDateToUse.plusMonths(1);
                break;
            case YEARLY:
            case DAILY:
                // NOOP since (should be) called only by TurnoverAnalysisService#analyze which has TurnoverAggregationService#guard
                return Collections.emptyList();
            }
        }
        return result;
    }

    LocalDate getEndDateToUse(
            final AggregationAnalysisReportForConfig report) {

        final TurnoverReportingConfig config = report.getTurnoverReportingConfig();
        final Occupancy occupancy = config.getOccupancy();
        final Lease currentLease = occupancy.getLease();

        if (report.isToplevel()) {
            return occupancy.getEffectiveEndDate() == null ? clockService.now().plusMonths(23) : occupancy.getEffectiveEndDate().plusMonths(23);
        }

        if (report.getNextLease()==null){
            if (report.getNextOnSameUnit()!=null){
                return report.getNextOnSameUnit().getEffectiveStartDate().minusDays(1);
            }

            if (report.getNextOnOtherUnit().size()==1){
                return report.getNextOnOtherUnit().get(0).getEffectiveStartDate().minusDays(1);
            }

            if (report.getParallelConfigs().size()>0) {
                final LocalDate lastAggregationDate = occupancy.getEffectiveEndDate().withDayOfMonth(1);
                // inspect the end date of each. If one of them can 'fill the gap' then do not fill it with current one
                boolean configFound = false;
                for (TurnoverReportingConfig c : report.getParallelConfigs()) {
                    if (c.getEndDate() == null || c.getOccupancy().getEffectiveEndDate().isAfter(lastAggregationDate)) {
                        configFound = true;
                    }
                }
                return configFound ? occupancy.getEffectiveEndDate() : currentLease.getEffectiveInterval().endDate(); // this is sqlAgent logic...
            }

            // default - fill the gap if any
            return currentLease.getEffectiveInterval().endDate().isAfter(occupancy.getEffectiveEndDate()) ?
                    currentLease.getEffectiveInterval().endDate() :
                    occupancy.getEffectiveEndDate();
        } else {
            // there is a next lease
            final LocalDate startDateNextLease = report.getNextLease().getEffectiveInterval().startDate();
            final LocalDate firstAggDateNextLease = startDateNextLease.withDayOfMonth(1); // agreed with users: when the next lease starts in a month - even on the last day of the month - the aggregation will be on the next lease for that month
            if (report.getNextOnSameUnit()!=null){
                final LocalDate endDateDerivedOfNext = report.getNextOnSameUnit().getEffectiveStartDate().minusDays(1);
                return endDateDerivedOfNext.isBefore(firstAggDateNextLease) ? endDateDerivedOfNext : firstAggDateNextLease.minusDays(1);
            }

            if (report.getNextOnOtherUnit().size()==1){
                final LocalDate endDateDerivedOfNext = report.getNextOnOtherUnit().get(0).getEffectiveStartDate()
                        .minusDays(1);
                return endDateDerivedOfNext.isBefore(firstAggDateNextLease) ? endDateDerivedOfNext : firstAggDateNextLease.minusDays(1);
            }

            if (report.getParallelConfigs().size()>0){
                // compare the end date of each with that of the current config. If one of them can 'fill the gap' then do not fill it with current one
                boolean configFound = false;
                for (TurnoverReportingConfig c : report.getParallelConfigs()){
                    if (c.getEndDate()==null || c.getEndDate().isAfter(report.getTurnoverReportingConfig().getEndDate())){
                        configFound = true;
                    }
                }
                return configFound ? occupancy.getEffectiveEndDate() : firstAggDateNextLease.minusDays(1);
            }

            // default fill the gap on this report
            return firstAggDateNextLease.minusDays(1);
        }

    }

    LocalDate getStartDateToUse(final AggregationAnalysisReportForConfig report){
        final Occupancy occupancy = report.getTurnoverReportingConfig().getOccupancy();
        LocalDate occStartDateOrNull = null;
        try {
            occStartDateOrNull = occupancy.getEffectiveInterval().startDate();
        } catch (Exception e) {
            LOG.warn(String.format("Problem with occupancy %s on lease %s", occupancy.toString(), occupancy.getLease().getReference()));
            LOG.warn(e.getMessage());
        }

        return occStartDateOrNull == null ? TurnoverAggregationService.MIN_AGGREGATION_DATE : occStartDateOrNull.withDayOfMonth(1);

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
