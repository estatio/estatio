package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.api.client.util.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.AggregationStrategy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import jdk.nashorn.internal.objects.NativeUint8Array;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TurnoverAggregationService {

    public static Logger LOG = LoggerFactory.getLogger(TurnoverAggregationService.class);

    // TODO: candidate for configuration property?
    public static LocalDate MIN_AGGREGATION_DATE = new LocalDate(2010, 1,1);

    public static List<AggregationStrategy> STRATEGIES_IMPLEMENTED = Arrays.asList(AggregationStrategy.SIMPLE, AggregationStrategy.PREVIOUS_MANY_OCCS_TO_ONE);

    /**
     * This method analyzes the reporting configs involved for the lease, determines and sets the aggregation strategy on each of them
     * Then maintains the aggregations on each of them
     * Then (re-)calculates the aggregations with a date between start date and end date (both included)
     *
     *  @param lease
     * @param type
     * @param frequency
     * @param startDate
     * @param endDate
     * @param maintainOnly
     */
    public void aggregateTurnoversForLease(
            final Lease lease,
            final Type type,
            final Frequency frequency,
            @Nullable
            final LocalDate startDate,
            @Nullable
            final LocalDate endDate,
            final boolean maintainOnly){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No aggregate-turnovers-for-lease implementation for type %s found.",
                    type));
            return;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No aggregate-turnovers-for-lease for frequency %s found.",
                    frequency));
            return;
        }

        // Process step 1: analyze  ////////////////////////////////////////////////////////////////////////////////////
        final List<AggregationAnalysisReportForConfig> analysisReports = analyze(lease, type,
                frequency);


        // Process step 2: maintain  ///////////////////////////////////////////////////////////////////////////////////
        analysisReports.stream().forEach(r -> {
            // 2a: determine and set strategy
            final TurnoverReportingConfig config = r.getTurnoverReportingConfig();
            config.setAggregationStrategy(determineApplicationStrategyForConfig(analysisReports, config));

            // 2b: create / delete aggregations
            maintainTurnoverAggregationsForConfig(r);
        });

        if (maintainOnly)
            return;

        // Process step 3: calculate  //////////////////////////////////////////////////////////////////////////////////

        final LocalDateInterval calculationPeriodForAggregations = getCalculationPeriodForAggregations(startDate, endDate);
        final LocalDateInterval periodForTurnoverSelection = LocalDateInterval.including(
                calculationPeriodForAggregations.startDate().minusMonths(23),
                calculationPeriodForAggregations.endDate()
        );

        List<ConfigReportTuple> configReportTuplesForCalculationPeriod = new ArrayList<>();
        analysisReports.stream().forEach(r -> {
            final List<LocalDate> datesIfAny = r.getAggregationDates().stream()
                    .filter(d -> calculationPeriodForAggregations.contains(d))
                    .collect(Collectors.toList());
            if (!datesIfAny.isEmpty()) {
                final List<TurnoverReportingConfig> configs = configReportTuplesForCalculationPeriod.stream()
                        .map(t -> t.getConfig())
                        .filter(c -> c.equals(r.getTurnoverReportingConfig())).collect(Collectors.toList());
                if (configs.isEmpty()) {
                    configReportTuplesForCalculationPeriod
                            .add(new ConfigReportTuple(r.getTurnoverReportingConfig(), r));
                }
            }
            // ALSO ADD ALL CONFIGS THAT ARE PREVIOUS ON THE SAME LEASE TODO: this should not be needed ...
            if (!r.getPreviousOnOtherUnit().isEmpty() || r.getPreviousOnSameUnit()!=null){
                r.getPreviousOnOtherUnit().forEach(prevConfig->{
                    final List<TurnoverReportingConfig> configs = configReportTuplesForCalculationPeriod.stream()
                            .map(t -> t.getConfig())
                            .filter(c -> c.equals(prevConfig)).collect(Collectors.toList());
                    final AggregationAnalysisReportForConfig reportForPrevConfig = analysisReports.stream().filter(ar->ar.getTurnoverReportingConfig().equals(prevConfig)).findFirst().orElse(null);
                    if (reportForPrevConfig!=null && configs.isEmpty()) configReportTuplesForCalculationPeriod.add(new ConfigReportTuple(prevConfig, reportForPrevConfig));
                });
            }
        });

        List<TurnoverReportingConfig> configsToCalculate = configReportTuplesForCalculationPeriod.stream()
                .map(c -> c.getConfig())
                .collect(Collectors.toList());

        List<Turnover> turnoversToAggregate = new ArrayList<>();
        configsToCalculate.forEach(c -> {
            final List<Turnover> turnoversInCalculationPeriod = turnoverRepository
                    .findApprovedByConfigAndTypeAndFrequency(c, c.getType(), c.getFrequency()).stream()
                    .filter(t -> periodForTurnoverSelection.contains(t.getDate()))
                    .collect(Collectors.toList());
            turnoversToAggregate.addAll(turnoversInCalculationPeriod);
        });

        for (TurnoverReportingConfig c : configsToCalculate){

            List<TurnoverAggregation> aggsForC = turnoverAggregationRepository
                    .findByTurnoverReportingConfig(c).stream()
                    .filter(a -> calculationPeriodForAggregations.contains(a.getDate()))
                    .collect(Collectors.toList());

            for (TurnoverAggregation a : aggsForC){
                calculateAggregation(a, turnoversToAggregate, configReportTuplesForCalculationPeriod);
            }

        }

    }

    final LocalDateInterval getCalculationPeriodForAggregations(final LocalDate startDate, final LocalDate endDate){
        final LocalDate startDateToUse = startDate == null || startDate.isBefore(MIN_AGGREGATION_DATE) ? MIN_AGGREGATION_DATE : startDate.withDayOfMonth(1);
        final LocalDate endDateToUse = endDate == null ? clockService.now().withDayOfMonth(1).plusMonths(23): endDate.withDayOfMonth(1);
        if (endDateToUse.isBefore(startDateToUse)) return LocalDateInterval.including(startDateToUse, startDateToUse); // Safety net in case end date is before min aggregation date while start date is null
        return LocalDateInterval.including(startDateToUse, endDateToUse);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class ConfigReportTuple {

        private TurnoverReportingConfig config;

        private AggregationAnalysisReportForConfig report;
    }
    public void calculateAggregation(final TurnoverAggregation aggregation, final List<Turnover> turnovers, final List<ConfigReportTuple> configReportTuples) {

        if (aggregation.getTurnoverReportingConfig().getAggregationStrategy()==null || !STRATEGIES_IMPLEMENTED.contains(aggregation.getTurnoverReportingConfig().getAggregationStrategy())) return;

        ////////////////////////////////////////////////TODO///////////////////////////////////////////////////////////////
        // TODO: Implement logic for strategies - filter turnovers !!!

        // we need to filter turnovers on 'parent' configs
        Lease l = aggregation.getTurnoverReportingConfig().getOccupancy().getLease();
        List<Lease> previousLeasesToExamineForCalculation = new ArrayList<>();
        while (l.getPrevious()!=null){
            l = (Lease) l.getPrevious();
            previousLeasesToExamineForCalculation.add(l);
        }

        final AggregationAnalysisReportForConfig report = configReportTuples.stream()
                .filter(t -> t.getConfig().equals(aggregation.getTurnoverReportingConfig()))
                .map(t->t.getReport()).findFirst().orElse(null);

        List<TurnoverReportingConfig> configsToAggregateTurnoversFor = new ArrayList<>();
        configsToAggregateTurnoversFor.add(aggregation.getTurnoverReportingConfig());

        if (report.getNextOnSameUnit()!=null) configsToAggregateTurnoversFor.add(report.getNextOnSameUnit());
        if (report.getPreviousOnSameUnit()!=null) configsToAggregateTurnoversFor.add(report.getPreviousOnSameUnit());
        // also for this strategy look at previous on other unit
        if (!report.getPreviousOnOtherUnit().isEmpty()){
            configsToAggregateTurnoversFor.addAll(report.getPreviousOnOtherUnit());
        }

        previousLeasesToExamineForCalculation.forEach(pl->{
            final List<TurnoverReportingConfig> prevConfigs = configReportTuples.stream()
                    .filter(t -> t.getConfig().getOccupancy().getLease().equals(pl))
                    .map(t -> t.getConfig())
                    .collect(Collectors.toList());
            configsToAggregateTurnoversFor.addAll(prevConfigs);
        });

        List<Turnover> turnoversToAggregate = turnovers.stream()
                .filter(t->configsToAggregateTurnoversFor.contains(t.getConfig()))
                .sorted()
                .collect(Collectors.toList());

        ////////////////////////////////////////////////TODO///////////////////////////////////////////////////////////////

        calculateAggregationForOther(aggregation, turnoversToAggregate);

        aggregation.getAggregate1Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getAggregate2Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getAggregate3Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getAggregate6Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getAggregate9Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getAggregate12Month().calculate(aggregation, turnoversToAggregate);

        aggregation.getAggregateToDate().calculate(aggregation, turnoversToAggregate);

        aggregation.getPurchaseCountAggregate1Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getPurchaseCountAggregate3Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getPurchaseCountAggregate6Month().calculate(aggregation, turnoversToAggregate);
        aggregation.getPurchaseCountAggregate12Month().calculate(aggregation, turnoversToAggregate);

        aggregation.setCalculatedOn(clockService.now());

    }

    void calculateAggregationForOther(final TurnoverAggregation aggregation, final List<Turnover> turnovers){

        // reset
        aggregation.setGrossAmount1MCY_1(null);
        aggregation.setNetAmount1MCY_1(null);
        aggregation.setGrossAmount1MCY_2(null);
        aggregation.setNetAmount1MCY_2(null);
        aggregation.setComments12MCY(null);
        aggregation.setComments12MPY(null);

        LocalDate aggDate = aggregation.getDate();
        List<Turnover> toList = turnovers.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(1))).collect(Collectors.toList());
        toList.forEach(t->{
            aggregation.setGrossAmount1MCY_1(aggAmount(aggregation.getGrossAmount1MCY_1(), t.getGrossAmount()));
            aggregation.setNetAmount1MCY_1(aggAmount(aggregation.getNetAmount1MCY_1(), t.getNetAmount()));
        });

        toList = turnovers.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(2))).collect(Collectors.toList());
        toList.forEach(t->{
            aggregation.setGrossAmount1MCY_2(aggAmount(aggregation.getGrossAmount1MCY_2(), t.getGrossAmount()));
            aggregation.setNetAmount1MCY_2(aggAmount(aggregation.getNetAmount1MCY_2(), t.getNetAmount()));
        });

        LocalDateInterval I12MCY = LocalDateInterval.including(aggDate.minusMonths(11), aggDate);
        LocalDateInterval I12MPY = LocalDateInterval.including(aggDate.minusYears(1).minusMonths(11), aggDate.minusYears(1));
        String ccy = null;
        String cpy = null;
        for (Turnover t : turnovers){
            if (t.getComments()!=null && I12MCY.contains(t.getDate())){
                if (ccy==null ){
                    ccy = t.getComments();
                } else {
                    ccy = ccy.concat(" | ").concat(t.getComments());
                }
            }
            if (t.getComments()!=null && I12MPY.contains(t.getDate())){
                if (cpy==null ){
                    cpy = t.getComments();
                } else {
                    cpy = cpy.concat(" | ").concat(t.getComments());
                }
            }
        }
        aggregation.setComments12MCY(ccy!=null && ccy.length()>1022 ? ccy.substring(0,1022) : ccy);
        aggregation.setComments12MPY(cpy!=null && cpy.length()>1022 ? cpy.substring(0,1022) : cpy);

    }

    void calculateTurnoverAggregateForPeriod(
            final TurnoverAggregateForPeriod turnoverAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers) {
        final LocalDate periodStartDate = turnoverAggregateForPeriod.getAggregationPeriod()
                .periodStartDateFor(aggregationDate);
        final LocalDate periodEndDate = aggregationDate;
        if (periodStartDate.isAfter(periodEndDate)) return;

        final LocalDateInterval intervalCY = LocalDateInterval.including(periodStartDate, periodEndDate);
        final List<Turnover> valuesCurrentYear = getTurnoversForInterval(turnovers,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(periodStartDate.minusYears(1), periodEndDate.minusYears(1));
        final List<Turnover> valuesPreviousYear = getTurnoversForInterval(turnovers,
                intervalPY);

        if (valuesCurrentYear.isEmpty() && valuesPreviousYear.isEmpty()) return;

        resetTurnoverAggregateForPeriod(turnoverAggregateForPeriod);

        final List<Turnover> toCY = turnovers.stream().filter(t -> intervalCY.contains(t.getDate()))
                .collect(Collectors.toList());
        final List<Turnover> toPY = turnovers.stream().filter(t -> intervalPY.contains(t.getDate()))
                .collect(Collectors.toList());

        toCY.forEach(t->{
            turnoverAggregateForPeriod.setGrossAmount(aggAmount(turnoverAggregateForPeriod.getGrossAmount(), t.getGrossAmount()));
            turnoverAggregateForPeriod.setNetAmount(aggAmount(turnoverAggregateForPeriod.getNetAmount(), t.getNetAmount()));
            if (t.getGrossAmount()!=null || t.getNetAmount()!=null) {
                turnoverAggregateForPeriod.setTurnoverCount(turnoverAggregateForPeriod.getTurnoverCount() != null ?
                        turnoverAggregateForPeriod.getTurnoverCount() + 1 :
                        1);
            }
        });
        turnoverAggregateForPeriod.setNonComparableThisYear(containsNonComparableTurnover(toCY));

        toPY.forEach(t->{
            turnoverAggregateForPeriod.setGrossAmountPreviousYear(aggAmount(turnoverAggregateForPeriod.getGrossAmountPreviousYear(), t.getGrossAmount()));
            turnoverAggregateForPeriod.setNetAmountPreviousYear(aggAmount(turnoverAggregateForPeriod.getNetAmountPreviousYear(), t.getNetAmount()));
            if (t.getGrossAmount()!=null || t.getNetAmount()!=null) {
                turnoverAggregateForPeriod.setTurnoverCountPreviousYear(
                        turnoverAggregateForPeriod.getTurnoverCountPreviousYear() != null ?
                                turnoverAggregateForPeriod.getTurnoverCountPreviousYear() + 1 :
                                1);
            }
        });
        turnoverAggregateForPeriod.setNonComparablePreviousYear(containsNonComparableTurnover(toPY));

        turnoverAggregateForPeriod.setComparable(isComparableForPeriod(
                turnoverAggregateForPeriod.getAggregationPeriod(),
                turnoverAggregateForPeriod.getTurnoverCount(),
                turnoverAggregateForPeriod.getTurnoverCountPreviousYear(),
                turnoverAggregateForPeriod.getNonComparableThisYear(),
                turnoverAggregateForPeriod.getNonComparablePreviousYear()
                ));

    }

    public void calculateTurnoverAggregateToDate(
            final TurnoverAggregateToDate turnoverAggregateToDate,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers){

        final LocalDate startOfTheYear = new LocalDate(aggregationDate.getYear(), 1, 1);
        final LocalDateInterval intervalCY = LocalDateInterval.including(startOfTheYear, aggregationDate);
        final List<Turnover> toCY = getTurnoversForInterval(turnovers,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(startOfTheYear.minusYears(1), aggregationDate.minusYears(1));
        final List<Turnover> toPY = getTurnoversForInterval(turnovers,
                intervalPY);

        if (toCY.isEmpty() && toPY.isEmpty()) return;

        resetTurnoverAggregateToDate(turnoverAggregateToDate);

        toCY.forEach(t->{
            turnoverAggregateToDate.setGrossAmount(aggAmount(turnoverAggregateToDate.getGrossAmount(), t.getGrossAmount()));
            turnoverAggregateToDate.setNetAmount(aggAmount(turnoverAggregateToDate.getNetAmount(), t.getNetAmount()));
            if (t.getGrossAmount()!=null || t.getNetAmount()!=null) {
                turnoverAggregateToDate.setTurnoverCount(turnoverAggregateToDate.getTurnoverCount() != null ?
                        turnoverAggregateToDate.getTurnoverCount() + 1 :
                        1);
            }
        });
        turnoverAggregateToDate.setNonComparableThisYear(containsNonComparableTurnover(toCY));

        toPY.forEach(t->{
            turnoverAggregateToDate.setGrossAmountPreviousYear(aggAmount(turnoverAggregateToDate.getGrossAmountPreviousYear(), t.getGrossAmount()));
            turnoverAggregateToDate.setNetAmountPreviousYear(aggAmount(turnoverAggregateToDate.getNetAmountPreviousYear(), t.getNetAmount()));
            if (t.getGrossAmount()!=null || t.getNetAmount()!=null) {
                turnoverAggregateToDate.setTurnoverCountPreviousYear(
                        turnoverAggregateToDate.getTurnoverCountPreviousYear() != null ?
                                turnoverAggregateToDate.getTurnoverCountPreviousYear() + 1 :
                                1);
            }
        });
        turnoverAggregateToDate.setNonComparablePreviousYear(containsNonComparableTurnover(toPY));

        turnoverAggregateToDate.setComparable(isComparableToDate(
                aggregationDate,
                turnoverAggregateToDate.getTurnoverCount(),
                turnoverAggregateToDate.getTurnoverCountPreviousYear(),
                turnoverAggregateToDate.getNonComparableThisYear(),
                turnoverAggregateToDate.getNonComparablePreviousYear()
        ));
    }

    private List<Turnover> getTurnoversForInterval(
            final List<Turnover> turnovers,
            final LocalDateInterval interval) {
        return turnovers.stream()
                .filter(t -> interval.contains(t.getDate()))
                .collect(Collectors.toList());
    }

    public void calculatePurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnovers) {
        final LocalDate periodStartDate = purchaseCountAggregateForPeriod.getAggregationPeriod()
                .periodStartDateFor(aggregationDate);
        final LocalDate periodEndDate = aggregationDate;
        if (periodStartDate.isAfter(periodEndDate)) return;

        final LocalDateInterval intervalCY = LocalDateInterval.including(periodStartDate, periodEndDate);
        final List<Turnover> toCY = getTurnoversForInterval(turnovers,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(periodStartDate.minusYears(1), periodEndDate.minusYears(1));
        final List<Turnover> toPY = getTurnoversForInterval(turnovers,
                intervalPY);

        if (toCY.isEmpty() && toPY.isEmpty()) return;

        resetPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod);

        Integer numberOfCountsCY = null;
        for (Turnover t : toCY){
            purchaseCountAggregateForPeriod.setCount(aggCount(purchaseCountAggregateForPeriod.getCount(), t.getPurchaseCount()));
            if (t.getPurchaseCount()!=null){
                numberOfCountsCY = numberOfCountsCY==null ? 1 : numberOfCountsCY+1;
            }
        }
        Integer numberOfCountsPY = null;
        for (Turnover t : toPY){
            purchaseCountAggregateForPeriod.setCountPreviousYear(aggCount(purchaseCountAggregateForPeriod.getCountPreviousYear(), t.getPurchaseCount()));
            if (t.getPurchaseCount()!=null){
                numberOfCountsPY = numberOfCountsPY==null ? 1 : numberOfCountsPY+1;
            }
        }

        purchaseCountAggregateForPeriod.setComparable(
                isComparableForPeriod(purchaseCountAggregateForPeriod.getAggregationPeriod()
                , numberOfCountsCY
                , numberOfCountsPY
                , false
                , false
        ));

    }

    /**
     * This method returns a collection of reports on turnover config level intended for aggregation maintenance and calculation
     * From the config (t.i. occupancy) point of view it tries to find the toplevel parent lease and 'walk the graph' over all previous child leases
     *
     * @param lease
     * @param frequency
     * @return
     */
     List<AggregationAnalysisReportForConfig> analyze(final Lease lease, final Type type, final Frequency frequency){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No create-aggregation-reports implementation for type %s found.",
                    type));
            return Collections.EMPTY_LIST;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No create-aggregation-reports for frequency %s found.",
                    frequency));
            return Collections.EMPTY_LIST;
        }

        List<AggregationAnalysisReportForConfig> result = new ArrayList<>();

        // find top level lease
        Lease l = lease;
        while (l.getNext() != null){
            l = (Lease) l.getNext();
        }

        result.addAll(reportsForOccupancyTypeAndFrequency(l, type, frequency, true));

        l = (Lease) l.getPrevious();
        while (l!=null){
            result.addAll(reportsForOccupancyTypeAndFrequency(l, type, frequency,false));
            l = (Lease) l.getPrevious();
        }

        return result;
    }

    List<AggregationAnalysisReportForConfig> reportsForOccupancyTypeAndFrequency(final Lease l, final Type type, final Frequency frequency, final boolean isToplevelLease) {
        List<AggregationAnalysisReportForConfig> result = new ArrayList<>();
        final List<List<TurnoverReportingConfig>> collect = Lists.newArrayList(l.getOccupancies()).stream()
                .map(o -> turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(o, type, frequency))
                .collect(Collectors.toList());
        for (List<TurnoverReportingConfig> list : collect) {

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
            startDateToUse = MIN_AGGREGATION_DATE;
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
        if (startDateToUse.isBefore(MIN_AGGREGATION_DATE)) {
            startDateToUse = MIN_AGGREGATION_DATE;
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

    AggregationStrategy determineApplicationStrategyForConfig(final List<AggregationAnalysisReportForConfig> reports, final TurnoverReportingConfig turnoverReportingConfig){

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
        // WRONG Reasoning: there can be a previous occupancy on the same lease and two parallel after that one is closed
//        if (previous ==null || reportForPrevious ==null){
//            // no previous lease or no prev occs on prev lease
//            return AggregationStrategy.SIMPLE;
//        }

        if (reportForConfig.getParallelConfigs().isEmpty()){

            // no multiple par configs on this lease

            if (previous ==null || reportForPrevious ==null){
                            // no previous lease or no prev occs on prev lease
                            return AggregationStrategy.SIMPLE;
            }
            if (reportForPrevious.getParallelConfigs().isEmpty()){
                // no par configs (occs) on prev lease
                return AggregationStrategy.SIMPLE;
            } else {
                // par configs (occs) on prev lease
                return AggregationStrategy.PREVIOUS_MANY_OCCS_TO_ONE;
            }

        } else {
            if (previous ==null || reportForPrevious ==null){
                // check if there is a  previous config (occ) on the same lease for both this and the parallel one
                if (!reportForConfig.getPreviousOnOtherUnit().isEmpty()){
                    //TODO: refine this
                    // Check if at least 1 occ ends before at least two start
                    return AggregationStrategy.ONE_OCC_TO_MANY_SAME_LEASE;
                } else {
                    return AggregationStrategy.SIMPLE;
                }
            }
            // multiple par occs on this lease
            if (reportForPrevious.getParallelConfigs().isEmpty()){
                // no par occs on prev lease
                return AggregationStrategy.PREVIOUS_ONE_OCC_TO_MANY;
            } else {
                // par occs on prev lease
                return AggregationStrategy.PREVIOUS_MANY_OCCS_TO_MANY;
            }

        }

    }

    void maintainTurnoverAggregationsForConfig(final AggregationAnalysisReportForConfig report){

        try {
            final TurnoverReportingConfig config = report.getTurnoverReportingConfig();
            final List<TurnoverAggregation> aggregations = turnoverAggregationRepository
                    .findByTurnoverReportingConfig(config);
            final List<LocalDate> currentAggDates = aggregations.stream().map(a -> a.getDate())
                    .collect(Collectors.toList());

            // create if needed
            report.getAggregationDates().stream().forEach(d -> {
                if (!currentAggDates.contains(d)) {
                    turnoverAggregationRepository.findOrCreate(config, d, config.getCurrency());
                }
            });
            // remove if needed
            currentAggDates.stream().forEach(d -> {
                if (!report.getAggregationDates().contains(d)) {
                    turnoverAggregationRepository.findUnique(config, d).remove();
                }
            });
        } catch (Exception e){
            LOG.warn(String.format("Problem with aggregation for lease %s and occ %s",
                    report.getTurnoverReportingConfig().getOccupancy().getLease().getReference(),
                    report.getTurnoverReportingConfig().getOccupancy()));
            LOG.warn(e.getMessage());
        }

    }


    Boolean isComparableToDate(final LocalDate aggregationDate, final Integer numberOfTurnoversThisYear, final Integer numberOfTurnoversPreviousYear, final Boolean nonComparableThisYear, final Boolean nonComparablePreviousYear ){
         if (numberOfTurnoversThisYear==null || numberOfTurnoversPreviousYear == null) return false;
         return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= getMinNumberOfTurnoversToDate(aggregationDate) && numberOfTurnoversPreviousYear >= getMinNumberOfTurnoversToDate(aggregationDate);
    }

    Boolean containsNonComparableTurnover(final List<Turnover> turnoverList){
        if (turnoverList.isEmpty()) return null;
        return turnoverList.stream().anyMatch(t->t.isNonComparable());
    }

    Boolean isComparableForPeriod(final AggregationPeriod period, final Integer numberOfTurnoversThisYear, final Integer numberOfTurnoversPreviousYear, final Boolean nonComparableThisYear, final Boolean nonComparablePreviousYear){
        if (numberOfTurnoversThisYear==null || numberOfTurnoversPreviousYear==null) return false;
        return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= period.getMinNumberOfTurnovers() && numberOfTurnoversPreviousYear >=period.getMinNumberOfTurnovers();
    }

    private BigDecimal aggAmount(final BigDecimal curAmount, final BigDecimal amountToAdd) {
        if (amountToAdd==null) return curAmount;
        if (curAmount==null)   return amountToAdd;
        return curAmount.add(amountToAdd);
    }

    private BigInteger aggCount(final BigInteger curCount, final BigInteger countToAdd) {
        if (countToAdd==null) return curCount;
        if (curCount==null)   return countToAdd;
        return curCount.add(countToAdd);
    }

    int getMinNumberOfTurnoversToDate(final LocalDate aggregationDate){
        return aggregationDate.getMonthOfYear();
    }

    private void resetTurnoverAggregateToDate(final TurnoverAggregateToDate agg) {
        agg.setGrossAmount(null);
        agg.setNetAmount(null);
        agg.setTurnoverCount(null);
        agg.setNonComparableThisYear(null);
        agg.setGrossAmountPreviousYear(null);
        agg.setNetAmountPreviousYear(null);
        agg.setTurnoverCountPreviousYear(null);
        agg.setNonComparablePreviousYear(null);
        agg.setComparable(false);
    }

    private void resetTurnoverAggregateForPeriod(final TurnoverAggregateForPeriod agg){
        agg.setGrossAmount(null);
        agg.setNetAmount(null);
        agg.setTurnoverCount(null);
        agg.setNonComparableThisYear(null);
        agg.setGrossAmountPreviousYear(null);
        agg.setNetAmountPreviousYear(null);
        agg.setTurnoverCountPreviousYear(null);
        agg.setNonComparablePreviousYear(null);
        agg.setComparable(false);
    }

    private void resetPurchaseCountAggregateForPeriod(final PurchaseCountAggregateForPeriod agg){
        agg.setCount(null);
        agg.setCountPreviousYear(null);
        agg.setComparable(false);
    }

    public List<TurnoverReportingConfig> choicesForChildConfig(final TurnoverReportingConfig config) {
        Lease lease = config.getOccupancy().getLease();
        List<TurnoverReportingConfig> result = new ArrayList<>();
        if (lease.getPrevious()!=null) {
            lease = (Lease) lease.getPrevious();
            Lists.newArrayList(lease.getOccupancies()).stream()
                    .forEach(o->{
                        result.addAll(turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(o, Type.PRELIMINARY, Frequency.MONTHLY));
                    });
        }
        return result;
    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject ClockService clockService;
}
