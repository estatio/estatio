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

                boolean isToplevelOccupancy = isToplevelLease && report.getNextOnSameUnit() == null && report.getNextOnOtherUnit().isEmpty();
                report.setToplevel(isToplevelOccupancy);
                result.add(report);

            }
        }

        return result;
    }

    /**
     * NOTE: this method mimics the behaviour of the current SQL Aggregation Agent. (SEE: adocs/documentation/src/main/pptx/Turnover-Aggregation-decisiontree-sql-agent.pptx)
     * TODO: Possibly rationalyze with users?
     * @param report
     * @return
     */
    List<LocalDate> aggregationDatesForTurnoverReportingConfig(
            final AggregationAnalysisReportForConfig report) {

        LocalDate startDateToUse;
        LocalDate endDateToUse;

        final TurnoverReportingConfig config = report.getTurnoverReportingConfig();
        final Occupancy occupancy = config.getOccupancy();
        final Lease currentLease = occupancy.getLease();

        startDateToUse = getStartDateToUse(occupancy);

        if (report.getNextLease()!=null){
            final LocalDate effectiveEndDateCurrentLease = currentLease.getEffectiveInterval().endDate();
            LocalDate endOfTheMonthCurrentLeaseEnd = effectiveEndDateCurrentLease.withDayOfMonth(1).plusMonths(1).minusDays(1);
            final LocalDate startDateNextLease = report.getNextLease().getEffectiveInterval().startDate();
            // this is logic from the old SQL agent
            LocalDate firstAggDateNextLease = startDateNextLease.equals(endOfTheMonthCurrentLeaseEnd.plusDays(1)) ? startDateNextLease.withDayOfMonth(1) : startDateNextLease.withDayOfMonth(1).plusMonths(1);
            if (report.getNextOnSameUnit()!=null){
                endDateToUse = report.getNextOnSameUnit().getEffectiveStartDate().minusDays(1);
            } else {
                if (report.getNextOnOtherUnit().size()==1){
                    endDateToUse = report.getNextOnOtherUnit().get(0).getEffectiveStartDate().minusDays(1);
                } else {
                    if (report.getParallelConfigs().size()>0){
                        // inspect the end date of each. If one of them can 'fill the gap' then do not fill it with current one
                        boolean occFound = false;
                        for (TurnoverReportingConfig c : report.getParallelConfigs()){
                            if (!c.getOccupancy().getEffectiveEndDate().isBefore(firstAggDateNextLease)){
                                occFound = true;
                            }
                        }
                        endDateToUse = occFound ? occupancy.getEffectiveEndDate() : firstAggDateNextLease.minusDays(1);
                    } else {
                        // fill the gap with next lease first aggregation date if any
                        endDateToUse = firstAggDateNextLease.minusDays(1);
                    }
                }
            }
        } else {
            if (report.isToplevel()) {
                // TODO: what about turnovers reported after the occupancy effective end date? F.e. AM-J RIU B2
                endDateToUse = occupancy.getEffectiveInterval().endDate() == null ? clockService.now().plusMonths(23) : occupancy.getEffectiveInterval().endDate().plusMonths(23);
            } else {
                final LocalDate lastAggregationDate = occupancy.getEffectiveEndDate().withDayOfMonth(1);
                LocalDate nextAggregationDateAfterOccupancyEndDate = lastAggregationDate.plusMonths(1);
                if (report.getNextOnSameUnit()!=null){
                    endDateToUse = report.getNextOnSameUnit().getEffectiveStartDate().minusDays(1);
                } else {
                    if (report.getNextOnOtherUnit().size()==1){
                        endDateToUse = report.getNextOnOtherUnit().get(0).getOccupancy().getEffectiveInterval().startDate().minusDays(1);
                    } else {
                        if (report.getParallelConfigs().size()>0){
                            // inspect the end date of each. If one of them can 'fill the gap' then do not fill it with current one
                            boolean occFound = false;
                            for (TurnoverReportingConfig c : report.getParallelConfigs()){
                                if (!c.getOccupancy().getEffectiveEndDate().isBefore(nextAggregationDateAfterOccupancyEndDate)){
                                    occFound = true;
                                }
                            }
                            endDateToUse = occFound ? lastAggregationDate.minusDays(1) : currentLease.getEffectiveInterval().endDate(); // this is sqlAgent logic...
                        } else {
                            // fill the gap if any
                            endDateToUse = currentLease.getEffectiveInterval().endDate()
                                    .isAfter(occupancy.getEffectiveEndDate()) ?
                                    currentLease.getEffectiveInterval().endDate() :
                                    occupancy.getEffectiveEndDate();
                        }
                    }
                }
            }
        }

        List<LocalDate> result = new ArrayList<>();
        while (!endDateToUse.isBefore(startDateToUse)) {
            result.add(startDateToUse);
            startDateToUse = startDateToUse.plusMonths(1);
        }
        return result;
    }

    private LocalDate getStartDateToUse(final Occupancy occupancy){
        LocalDate occStartDateOrNull = null;
        try {
            occStartDateOrNull = occupancy.getEffectiveInterval().startDate();
        } catch (Exception e) {
            LOG.warn(String.format("Problem with occupancy %s on lease %s", occupancy.toString(), occupancy.getLease().getReference()));
            LOG.warn(e.getMessage());
        }

        return occStartDateOrNull == null ? TurnoverAggregationService.MIN_AGGREGATION_DATE : occStartDateOrNull.withDayOfMonth(1);

    }

    List<LocalDate> aggregationDatesForTurnoverReportingConfigDEPRECATED(
            final TurnoverReportingConfig config,
            final boolean toplevel) {

        LocalDate startDateToUse;
        LocalDate startDateOrNull = null;
        final Occupancy occupancy = config.getOccupancy();
        try {
            startDateOrNull = occupancy.getEffectiveInterval().startDate();
        } catch (Exception e) {
            LOG.warn(String.format("Problem with config %s", config.toString()));
            LOG.warn(e.getMessage());
        }

        final Lease currentLease = occupancy.getLease();

        if (startDateOrNull == null) {
            startDateToUse = TurnoverAggregationService.MIN_AGGREGATION_DATE;
        } else {
                startDateToUse = startDateOrNull.withDayOfMonth(1);
        }

        LocalDate effectiveEndDateOcc = occupancy.getEffectiveEndDate();

        LocalDate endDateToUse;
        final Agreement nextIfAny = currentLease.getNext();
        if (nextIfAny != null
                && effectiveEndDateOcc != null) { // second check should not be needed!!

            Lease nextLease = (Lease) nextIfAny;
            final LocalDate startDateNextLease = nextIfAny.getEffectiveInterval().startDate();

            if (startDateNextLease.isBefore(effectiveEndDateOcc)){
                LOG.warn(String.format("The effective start date of lease %s is before the enddate of the previous %s", nextLease.getReference(), currentLease
                        .getReference()));
            }
            final LocalDate effectiveEndDateCurrentLease = currentLease.getEffectiveInterval().endDate();
            LocalDate endOfTheMonthLeaseEnd = effectiveEndDateCurrentLease.withDayOfMonth(1).plusMonths(1).minusDays(1);
            LocalDate firstAggDateNextLease = startDateNextLease.equals(endOfTheMonthLeaseEnd.plusDays(1)) ? startDateNextLease.withDayOfMonth(1) : startDateNextLease.withDayOfMonth(1).plusMonths(1);
            // fill the gap
            endDateToUse = firstAggDateNextLease.minusDays(1);

        } else {

            // if other occupancy on same lease has end-date > effectiveEndDateOcc
            if (effectiveEndDateOcc!=null){
                Occupancy occOnSameLeaseWithEndDateAfterEffectiveEndDateOcc = null;
                for (Occupancy o : currentLease.getOccupancies()){
                    if (!o.equals(occupancy)){
                            if (o.getEffectiveEndDate()==null || o.getEffectiveEndDate().isAfter(effectiveEndDateOcc)){
                                occOnSameLeaseWithEndDateAfterEffectiveEndDateOcc = o;
                            }
                    }
                }
                if (occOnSameLeaseWithEndDateAfterEffectiveEndDateOcc!=null){
                    // another occupancy will pick this up
                    endDateToUse = effectiveEndDateOcc.minusMonths(1);
                } else {
                    // always put on this occupancy
                    final LocalDate endDateCurrentLease = currentLease.getEffectiveInterval().endDate();
                    endDateToUse = endDateCurrentLease ==null ? clockService.now() : endDateCurrentLease;
                }
            } else {

                effectiveEndDateOcc = clockService.now();
                endDateToUse = effectiveEndDateOcc;

            }

            endDateToUse = toplevel ? endDateToUse.plusMonths(23) : endDateToUse;

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
