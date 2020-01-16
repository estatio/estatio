package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.client.util.Lists;
import com.google.inject.internal.cglib.core.$ReflectUtils;

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
     * Then (re-)calculates the aggregations with respect to a turnover date. This comprises every aggregation with a date in the period
     * from turnover date to turnover-date-plus-23-months.
     *
     *
     * @param lease
     * @param type
     * @param frequency
     * @param turnoverDate
     * @param maintainOnly
     */
    public void aggregateTurnoversForLease(
            final Lease lease,
            final Type type,
            final Frequency frequency,
            final LocalDate turnoverDate,
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

        final List<AggregationAnalysisReportForConfig> analysisReports = analyze(lease, type,
                frequency);

        analysisReports.stream().forEach(r -> {
            // determine and set strategy
            final TurnoverReportingConfig config = r.getTurnoverReportingConfig();
            config.setAggregationStrategy(determineApplicationStrategyForConfig(analysisReports, config));

            // create / delete aggregations
            maintainTurnoverAggregationsForConfig(r);
        });

        if (maintainOnly)
            return;

        LocalDate minTurnoverDate = turnoverDate.withDayOfMonth(1).isBefore(MIN_AGGREGATION_DATE.minusMonths(23)) ?
                MIN_AGGREGATION_DATE.minusMonths(23) :
                turnoverDate.withDayOfMonth(1);
        LocalDateInterval calculationPeriodForAggregations = LocalDateInterval
                .excluding(minTurnoverDate, minTurnoverDate.plusMonths(24));

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
                    .filter(t -> calculationPeriodForAggregations.contains(t.getDate()))
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
            turnoverAggregateForPeriod.setTurnoverCount(turnoverAggregateForPeriod.getTurnoverCount() != null ? turnoverAggregateForPeriod.getTurnoverCount()+1 : 1);
        });
        turnoverAggregateForPeriod.setNonComparableThisYear(containsNonComparableTurnover(toCY));

        toPY.forEach(t->{
            turnoverAggregateForPeriod.setGrossAmountPreviousYear(aggAmount(turnoverAggregateForPeriod.getGrossAmountPreviousYear(), t.getGrossAmount()));
            turnoverAggregateForPeriod.setNetAmountPreviousYear(aggAmount(turnoverAggregateForPeriod.getNetAmountPreviousYear(), t.getNetAmount()));
            turnoverAggregateForPeriod.setTurnoverCountPreviousYear(turnoverAggregateForPeriod.getTurnoverCountPreviousYear() !=null ? turnoverAggregateForPeriod.getTurnoverCountPreviousYear()+1 : 1);
        });
        turnoverAggregateForPeriod.setNonComparablePreviousYear(containsNonComparableTurnover(toPY));

        turnoverAggregateForPeriod.setComparable(isComparableForPeriod(
                turnoverAggregateForPeriod.getAggregationPeriod(),
                turnoverAggregateForPeriod.getTurnoverCount()!=null ? turnoverAggregateForPeriod.getTurnoverCount() : null,
                turnoverAggregateForPeriod.getTurnoverCountPreviousYear() != null ? turnoverAggregateForPeriod.getTurnoverCountPreviousYear() : null,
                turnoverAggregateForPeriod.isNonComparableThisYear(),
                turnoverAggregateForPeriod.isNonComparablePreviousYear()
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
            turnoverAggregateToDate.setTurnoverCount(turnoverAggregateToDate.getTurnoverCount()!= null ? turnoverAggregateToDate.getTurnoverCount()+1 : 1);
        });
        turnoverAggregateToDate.setNonComparableThisYear(containsNonComparableTurnover(toCY));

        toPY.forEach(t->{
            turnoverAggregateToDate.setGrossAmountPreviousYear(aggAmount(turnoverAggregateToDate.getGrossAmountPreviousYear(), t.getGrossAmount()));
            turnoverAggregateToDate.setNetAmountPreviousYear(aggAmount(turnoverAggregateToDate.getNetAmountPreviousYear(), t.getNetAmount()));
            turnoverAggregateToDate.setTurnoverCountPreviousYear(turnoverAggregateToDate.getTurnoverCountPreviousYear()!=null ? turnoverAggregateToDate.getTurnoverCountPreviousYear()+1 : 1);
        });
        turnoverAggregateToDate.setNonComparablePreviousYear(containsNonComparableTurnover(toPY));

        turnoverAggregateToDate.setComparable(isComparableToDate(
                aggregationDate,
                turnoverAggregateToDate.getTurnoverCount()!=null ? turnoverAggregateToDate.getTurnoverCount() : null,
                turnoverAggregateToDate.getTurnoverCountPreviousYear()!=null ? turnoverAggregateToDate.getTurnoverCountPreviousYear() : null,
                turnoverAggregateToDate.isNonComparableThisYear(),
                turnoverAggregateToDate.isNonComparablePreviousYear()
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

        toCY.forEach(t->{
            purchaseCountAggregateForPeriod.setCount(aggCount(purchaseCountAggregateForPeriod.getCount(), t.getPurchaseCount()));
        });
        toPY.forEach(t->{
            purchaseCountAggregateForPeriod.setCountPreviousYear(aggCount(purchaseCountAggregateForPeriod.getCountPreviousYear(), t.getPurchaseCount()));
        });

        if (toCY.size()==toPY.size()){
            purchaseCountAggregateForPeriod.setComparable(true);
        }
    }

    boolean containsNonComparableTurnover(final List<Turnover> turnoverList){
        return turnoverList.stream().anyMatch(t->t.isNonComparable());
    }

    boolean isComparableForPeriod(final AggregationPeriod period, final Integer numberOfTurnoversThisYear, final Integer numberOfTurnoversPreviousYear, final boolean nonComparableThisYear, final boolean nonComparablePreviousYear){
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
                boolean isToplevelOccupancy = isToplevelLease && report.getNextOnSameUnit() == null && report.getNextOnOtherUnit().isEmpty();
                report.setToplevel(isToplevelOccupancy);
                report.getAggregationDates().addAll(
                        aggregationDatesForTurnoverReportingConfig(config, isToplevelOccupancy));

                result.add(report);

            }
        }

        return result;
    }

    List<LocalDate> aggregationDatesForTurnoverReportingConfig(final TurnoverReportingConfig config, final boolean toplevel){

        LocalDate startDate;
        LocalDate startDateOrNull = null;
        try {
            startDateOrNull = config.getOccupancy().getEffectiveInterval().startDate();
        } catch (Exception e){
            LOG.warn(String.format("Problem with config %s", config.toString()));
            LOG.warn(e.getMessage());
        }
        if (startDateOrNull==null) {
            startDate = MIN_AGGREGATION_DATE;
        } else {
            startDate = startDateOrNull.withDayOfMonth(1);
        }
        if (startDate.isBefore(MIN_AGGREGATION_DATE)) startDate = MIN_AGGREGATION_DATE;

        LocalDate effectiveEndDateOcc = config.getOccupancy().getEffectiveEndDate();
        if (effectiveEndDateOcc==null) effectiveEndDateOcc = clockService.now();
        LocalDate endDate = toplevel ? effectiveEndDateOcc.withDayOfMonth(1).plusMonths(23) : effectiveEndDateOcc.withDayOfMonth(1).minusMonths(1);

        List<LocalDate> result = new ArrayList<>();

        while (!startDate.isAfter(endDate)){
            result.add(startDate);
            startDate = startDate.plusMonths(1);
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


    boolean isComparableToDate(final LocalDate aggregationDate, final Integer numberOfTurnoversThisYear, final Integer numberOfTurnoversPreviousYear, final boolean nonComparableThisYear, final boolean nonComparablePreviousYear ){
         if (numberOfTurnoversThisYear==null || numberOfTurnoversPreviousYear == null) return false;
         return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= getMinNumberOfTurnoversToDate(aggregationDate) && numberOfTurnoversPreviousYear >= getMinNumberOfTurnoversToDate(aggregationDate);
    }

    int getMinNumberOfTurnoversToDate(final LocalDate aggregationDate){
        return aggregationDate.getMonthOfYear();
    }

    private void resetTurnoverAggregateToDate(final TurnoverAggregateToDate agg) {
        agg.setGrossAmount(null);
        agg.setNetAmount(null);
        agg.setTurnoverCount(null);
        agg.setNonComparableThisYear(false);
        agg.setGrossAmountPreviousYear(null);
        agg.setNetAmountPreviousYear(null);
        agg.setTurnoverCountPreviousYear(null);
        agg.setNonComparablePreviousYear(false);
        agg.setComparable(false);
    }

    private void resetTurnoverAggregateForPeriod(final TurnoverAggregateForPeriod agg){
        agg.setGrossAmount(null);
        agg.setNetAmount(null);
        agg.setTurnoverCount(null);
        agg.setNonComparableThisYear(false);
        agg.setGrossAmountPreviousYear(null);
        agg.setNetAmountPreviousYear(null);
        agg.setTurnoverCountPreviousYear(null);
        agg.setNonComparablePreviousYear(false);
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
