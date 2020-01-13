package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.agreement.dom.Agreement;
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

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TurnoverAggregationService {

    public static Logger LOG = LoggerFactory.getLogger(TurnoverAggregationService.class);

    // TODO: candidate for configuration property?
    public static LocalDate MIN_AGGREGATION_DATE = new LocalDate(2010, 1,1);

    public void maintainTurnoverAggregationsForLease(final Lease lease, final Type type, final Frequency frequency){

        final List<AggregationReportForConfig> aggregationReports = createAggregationReports(lease, type, frequency);

        aggregationReports.stream().forEach(r->{
            // determine and set strategy
            final TurnoverReportingConfig config = r.getTurnoverReportingConfig();
            config.setAggregationStrategy(determineApplicationStrategyForConfig(aggregationReports, config));

            // create / delete aggregations
            maintainTurnoverAggregationsForConfig(r);
        });

    }

    public AggregationStrategy determineApplicationStrategyForConfig(final List<AggregationReportForConfig> reports, final TurnoverReportingConfig turnoverReportingConfig){

        final AggregationReportForConfig reportForConfig = reports.stream()
                .filter(r -> r.getTurnoverReportingConfig().equals(turnoverReportingConfig)).findFirst().orElse(null);

        if (reportForConfig==null){
            // should not happen
            return null;
        }

        final Lease previous = (Lease) reportForConfig.getTurnoverReportingConfig().getOccupancy().getLease().getPrevious();
        final AggregationReportForConfig reportForPrevious = reports.stream()
                .filter(r -> r.getTurnoverReportingConfig().getOccupancy().getLease().equals(previous))
                .findFirst().orElse(null);
        if (previous ==null || reportForPrevious ==null){
            // no previous lease or no prev occs on prev lease
            return AggregationStrategy.SIMPLE;
        }

        if (reportForConfig.getParallelOccupancies().isEmpty()){
            // no multiple par occs on this lease

            if (reportForPrevious.getParallelOccupancies().isEmpty()){
                // no par occs on prev lease
                return AggregationStrategy.SIMPLE;
            } else {
                // par occs on prev lease
                return AggregationStrategy.PREVIOUS_MANY_OCCS_TO_ONE;
            }

        } else {
            // multiple par occs on this lease
            if (reportForPrevious.getParallelOccupancies().isEmpty()){
                // no par occs on prev lease
                return AggregationStrategy.PREVIOUS_ONE_OCC_TO_MANY;
            } else {
                // par occs on prev lease
                return AggregationStrategy.PREVIOUS_MANY_OCCS_TO_MANY;
            }

        }

    }

    public void maintainTurnoverAggregationsForConfig(final AggregationReportForConfig report){

        final TurnoverReportingConfig config = report.getTurnoverReportingConfig();
        final List<TurnoverAggregation> aggregations = turnoverAggregationRepository
                .findByTurnoverReportingConfig(config);
        final List<LocalDate> currentAggDates = aggregations.stream().map(a -> a.getDate()).collect(Collectors.toList());

        // create if needed
        report.getAggregationDates().stream().forEach(d->{
            if (!currentAggDates.contains(d)) {
                turnoverAggregationRepository.findOrCreate(config, d, config.getCurrency());
            }
        });
        // remove if needed
        currentAggDates.stream().forEach(d->{
            if (!report.getAggregationDates().contains(d)){
                turnoverAggregationRepository.findUnique(config, d).remove();
            }
        });

    }

    /**
     * Looks 24 M into the future (with respect to calculation date)
     *
     * @param lease
     * @param type
     * @param frequency
     * @param calculationDate
     */
    public void aggregateTurnoversForLease(final Lease lease, final Type type, final Frequency frequency, final LocalDate calculationDate){

        LocalDate minTurnoverDate = calculationDate==null ? MIN_AGGREGATION_DATE.minusMonths(24) : calculationDate.minusMonths(24);
        List<Turnover> turnoversToAggregate = new ArrayList<>();
        List<TurnoverAggregation> aggregationsToCalculate = new ArrayList<>();
        for (TurnoverAggregation a : aggregationsToCalculate){
            a.calculate(turnoversToAggregate);
        }
    }

    /**
     * This method returns a collection of reports on occupancy level intended for aggregation maintenance
     * From the occupancy point of view it tries to find the toplevel parent lease and 'walk the graph' over all previous leases
     *
     * @param lease
     * @param frequency
     * @return
     */
    public List<AggregationReportForConfig> createAggregationReports(final Lease lease, final Type type, final Frequency frequency){
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

        List<AggregationReportForConfig> result = new ArrayList<>();

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

    List<AggregationReportForConfig> reportsForOccupancyTypeAndFrequency(final Lease l, final Type type, final Frequency frequency, final boolean isToplevelLease) {
        List<AggregationReportForConfig> result = new ArrayList<>();
        final List<List<TurnoverReportingConfig>> collect = Lists.newArrayList(l.getOccupancies()).stream()
                .map(o -> turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(o, type, frequency))
                .collect(Collectors.toList());
        for (List<TurnoverReportingConfig> list : collect) {

            if (!list.isEmpty()) {

                final TurnoverReportingConfig config = list.get(0);
                AggregationReportForConfig report = new AggregationReportForConfig(config);

                // find parallel occs
                final Occupancy occupancy = config.getOccupancy();
                if (l.hasOverlappingOccupancies()) {
                    for (Occupancy oc : l.getOccupancies()) {

                        if ((!oc.equals(occupancy)) && oc.getEffectiveInterval().overlaps(
                                occupancy.getEffectiveInterval())) {
                            final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository
                                    .findByOccupancyAndTypeAndFrequency(oc, type, frequency);
                            if (!configs.isEmpty()) {
                                report.getParallelOccupancies().add(configs.get(0));
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
                    final Optional<Occupancy> prev = Lists.newArrayList(l.getOccupancies()).stream()
                            .filter(occ -> !occ.equals(occupancy))
                            .filter(occ -> occ.getUnit().equals(occupancy.getUnit()))
                            .filter(occ -> occ.getEndDate() != null)
                            .filter(occ -> occ.getEndDate().isBefore(occupancy.getStartDate())).findFirst();
                    if (prev.isPresent()) {
                        final List<TurnoverReportingConfig> configs2 = turnoverReportingConfigRepository
                                .findByOccupancyAndTypeAndFrequency(prev.get(), type, frequency);
                        if (!configs2.isEmpty()) report.setPreviousOnSameUnit(configs2.get(0));
                    }
                }
                // find next
                if (occupancy.getEndDate() != null) {
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
                boolean isToplevelOccupancy = isToplevelLease && report.getNextOnSameUnit() == null;
                report.setToplevel(isToplevelOccupancy);
                report.getAggregationDates().addAll(
                        aggregationDatesForTurnoverReportingConfig(config, isToplevelOccupancy));

                result.add(report);

            }
        }

        return result;
    }

    public void calculateForConfig(final TurnoverReportingConfig turnoverReportingConfig, final LocalDate calculationDate) {

    }

    /**
     * this method returns the dates that aggregations should be made which can be used in aggregation maintenance process
     *
     * @param config
     * @param toplevel
     * @return
     */
    List<LocalDate> aggregationDatesForTurnoverReportingConfig(final TurnoverReportingConfig config, final boolean toplevel){

        LocalDate startDate = config.getOccupancy().getEffectiveInterval().startDate().withDayOfMonth(1);
        if (startDate.isBefore(MIN_AGGREGATION_DATE)) startDate = MIN_AGGREGATION_DATE;

        LocalDate effectiveEndDateOcc = config.getOccupancy().getEffectiveEndDate();
        if (effectiveEndDateOcc==null) effectiveEndDateOcc = clockService.now();
        LocalDate endDate = toplevel ? effectiveEndDateOcc.withDayOfMonth(1).plusMonths(24) : effectiveEndDateOcc.withDayOfMonth(1);

        List<LocalDate> result = new ArrayList<>();

        while (!startDate.isAfter(endDate)){
            result.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return result;
    }

    /**
     * Aggregates turnovers on an config
     *
     * Note that: since the aggregation also takes other occupancies on the lease and previous/next leases into account, this method also affect the aggregation of other occupancies
     *
     * @param config
     * @param type
     * @param frequency
     * @param calculationDate Date in time from where the algorithm looks back (and forward) 24 months.
     */
    public void aggregateTurnoversForConfig(final TurnoverReportingConfig config, final Type type, final Frequency frequency, final LocalDate calculationDate){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No aggregate-turnovers-for-config implementation for type %s found.",
                    type));
            return;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No aggregate-turnovers-for-config for frequency %s found.",
                    frequency));
            return;
        }

        if (config==null) return;

        LocalDate effectiveAggregationDate = calculationDate==null ? MIN_AGGREGATION_DATE : calculationDate; //TODO: this is meant to aggregate everything ... (Check)

        final List<Turnover> turnoverValueObjects = turnoversToAggregateForOccupancySortedAsc(config.getOccupancy(), Type.PRELIMINARY,
                Frequency.MONTHLY, effectiveAggregationDate);

        // for efficiency reasons - currently we never look back more than 24 months
        final  List<Turnover> turnoverValueObjectsFiltered = turnoverValueObjects.stream().filter(t->!t.getDate().isBefore(effectiveAggregationDate.minusMonths(24))).collect(
                Collectors.toList());

        // guard and efficiency
        if (turnoverValueObjectsFiltered.isEmpty()) return;

        Currency currency = config.getCurrency();

        final List<TurnoverAggregation> aggregationsToCalculate = findOrCreateAggregationsForMonthly(config, turnoverValueObjectsFiltered, calculationDate, currency, type, frequency);

        aggregationsToCalculate.forEach(a->a.calculate(turnoverValueObjectsFiltered));

    }

    /**
     *
     * @param occupancy
     * @param type
     * @param frequency
     * @param aggregationDate
     * @return
     */
    List<Turnover> turnoversToAggregateForOccupancySortedAsc(final Occupancy occupancy, final Type type, final Frequency frequency, final LocalDate aggregationDate){
        final Lease lease = occupancy.getLease();
        final Unit unit = occupancy.getUnit();
        final List<Lease> leasesToExamine = leasesToExamine(lease); // looks to the past only
        final List<Occupancy> occupanciesToExamine = occupanciesToExamine(unit, leasesToExamine);
        List<Turnover> turnovers = new ArrayList<>();
        occupanciesToExamine.forEach(o->{
            turnovers.addAll(
                    turnoverRepository.findApprovedByOccupancyAndTypeAndFrequency(
                            o,
                            type,
                            frequency)
            );
        });
        final List<Turnover> turnoversSorted = turnovers.stream()
                .filter(t->!t.getDate().isBefore(aggregationDate.minusMonths(24))) // efficiency reasons
                .sorted(Comparator.comparing(t -> t.getDate()))
                .collect(Collectors.toList());

        return turnoversSorted.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    /**
     * Currently looks to the past only TODO: may also look at parents ...?
     * @param lease
     * @return
     */
    List<Lease> leasesToExamine(final Lease lease) {
        List<Lease> leases = new ArrayList<>();
        Agreement previous = lease.getPrevious();
        leases.add(lease);
        while (previous!=null) {
            leases.add((Lease) previous);
            previous = previous.getPrevious();
        }
        return leases;
    }

    /**
     *
     * @param unit
     * @param leasesToExamine
     * @return
     */
    List<Occupancy> occupanciesToExamine(final Unit unit, final List<Lease> leasesToExamine) {
        List<Occupancy> occupanciesToExamine = new ArrayList<>();
        leasesToExamine.forEach(l->{
            final List<Occupancy> occupanciesOfSameUnit = Lists.newArrayList(l.getOccupancies()).stream()
                    .filter(o -> o.getUnit() == unit)
                    .collect(Collectors.toList());
            final List<Occupancy> occupanciesOfDifferentUnit = Lists.newArrayList(l.getOccupancies()).stream()
                    .filter(o -> o.getUnit() != unit)
                    .collect(Collectors.toList());
            if (noOverlapOccupanciesOnLease(l)) {
                occupanciesToExamine.addAll(l.getOccupancies());
            } else {
                if (occupanciesOfSameUnit.size()>=1){
                    occupanciesToExamine.addAll(occupanciesOfSameUnit);
                }
                if (occupanciesOfSameUnit.isEmpty() && occupanciesOfDifferentUnit.size() == 1) {
                    occupanciesToExamine.addAll(occupanciesOfDifferentUnit);
                }
                // TODO: hopefully not too many cases ...
                if (occupanciesOfSameUnit.isEmpty() && occupanciesOfDifferentUnit.size() > 1) {
                    LOG.warn(String.format(
                            "No occupancy found for lease %s with unit %s and multiple occupancies found for other unit - HOW TO HANDLE?",
                            l.getReference(), unit.getReference()));
                }
            }
        });
        return occupanciesToExamine;
    }

    boolean noOverlapOccupanciesOnLease(final Lease l){
        return !l.hasOverlappingOccupancies();
    }

    boolean containsNonComparableTurnover(final List<Turnover> turnoverList){
        return turnoverList.stream().anyMatch(t->t.isNonComparable());
    }

    boolean isComparable(final AggregationPeriod period, final int numberOfTurnoversThisYear, final int numberOfTurnoversPreviousYear, final boolean nonComparableThisYear, final boolean nonComparablePreviousYear){
        return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= period.getMinNumberOfTurnovers() && numberOfTurnoversPreviousYear >=period.getMinNumberOfTurnovers();
    }

    boolean isComparableToDate(final LocalDate aggregationDate, final int numberOfTurnoversThisYear, final int numberOfTurnoversPreviousYear, final boolean nonComparableThisYear, final boolean nonComparablePreviousYear ){
        return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= getMinNumberOfTurnoversToDate(aggregationDate) && numberOfTurnoversPreviousYear >= getMinNumberOfTurnoversToDate(aggregationDate);
    }

    int getMinNumberOfTurnoversToDate(final LocalDate aggregationDate){
        return aggregationDate.getMonthOfYear();
    }

    List<LocalDate> aggregationDatesForTurnoverReportingConfig(final TurnoverReportingConfig config, final LocalDate aggregationDate){
        if (config.getFrequency()!=Frequency.MONTHLY) return Collections.emptyList();

        final LocalDate startDate = config.getEffectiveStartDate().withDayOfMonth(1); // withDayOfMonth(1) should be redundant here
        final LocalDate endDate = determineEndDate(config, aggregationDate);
        if (endDate.isBefore(startDate)) return Collections.emptyList();

        List<LocalDate> result = new ArrayList<>();
        LocalDate d = startDate;
        while (!d.isAfter(endDate)){
            result.add(d);
            d = d.plusMonths(1);
        }
        return result.stream().sorted().collect(Collectors.toList());
    }

    LocalDate determineEndDate(final TurnoverReportingConfig config, final LocalDate aggregationDate) {

        final LocalDate endDate = config.getEndDate() != null ?
                config.getEndDate().withDayOfMonth(1) :
                clockService.now().withDayOfMonth(1);
        return isConfigForLastExpiredLease(config, aggregationDate) ? endDate.plusMonths(24) : endDate;
    }

    boolean isConfigForLastExpiredLease(final TurnoverReportingConfig config, final LocalDate aggregationDate){
        Lease lease = config.getOccupancy().getLease();
        if (lease.getEffectiveInterval().contains(aggregationDate.plusDays(1))) return false;
        if (lease.getNext()!=null) return false;
        return true;
    }

    List<TurnoverAggregation> findOrCreateAggregationsForMonthly(final TurnoverReportingConfig config, final List<Turnover> turnoverValueObjects, final LocalDate aggregationDate, final Currency currency, final Type type, final Frequency frequency){

        LocalDate startDate = determineStartDate(aggregationDate, turnoverValueObjects.get(0).getDate());
        LocalDate endDate = determineEndDate(config, aggregationDate);
        List<TurnoverAggregation> result = new ArrayList<>();

        LocalDate date = startDate.withDayOfMonth(1); // extra safeguard; should not be needed for Monthly turnovers
        while (!date.isAfter(endDate)){
            result.add(turnoverAggregationRepository.findOrCreate(config, date, currency));
            date = date.plusMonths(1);
        }
        return result;
    }

    LocalDate determineStartDate(final LocalDate aggregationDate, final LocalDate minTurnoverDate) {
        if (minTurnoverDate==null) return MIN_AGGREGATION_DATE; // should not happen
        if (aggregationDate==null) return MIN_AGGREGATION_DATE.minusMonths(24);
        final LocalDate dateMin24M = aggregationDate.minusMonths(24);
        return dateMin24M.isAfter(minTurnoverDate) ? dateMin24M : minTurnoverDate;
    }

    LocalDate determineEndDate(final Occupancy occupancy, final LocalDate aggregationDate) {
        return isOccupancyForLastExpiredLease(occupancy, aggregationDate) ? occupancy.getEffectiveEndDate().withDayOfMonth(1).plusMonths(24) : aggregationDate.plusMonths(24);
    }

    boolean isOccupancyForLastExpiredLease(final Occupancy occupancy, final LocalDate aggregationDate){
        Lease lease = occupancy.getLease();
        if (lease.getEffectiveInterval().contains(aggregationDate.plusDays(1))) return false;
        if (lease.getNext()!=null) return false;
        return true;
    }

    public void calculateTurnoverAggregation(final TurnoverAggregation aggregation, final List<Turnover> turnovers) {

        calculateAggregationForOther(aggregation, turnovers);

        aggregation.getAggregate1Month().calculate(aggregation, turnovers);
        aggregation.getAggregate2Month().calculate(aggregation, turnovers);
        aggregation.getAggregate3Month().calculate(aggregation, turnovers);
        aggregation.getAggregate6Month().calculate(aggregation, turnovers);
        aggregation.getAggregate9Month().calculate(aggregation, turnovers);
        aggregation.getAggregate12Month().calculate(aggregation, turnovers);

        aggregation.getAggregateToDate().calculate(aggregation, turnovers);

        aggregation.getPurchaseCountAggregate1Month().calculate(aggregation, turnovers);
        aggregation.getPurchaseCountAggregate3Month().calculate(aggregation, turnovers);
        aggregation.getPurchaseCountAggregate6Month().calculate(aggregation, turnovers);
        aggregation.getPurchaseCountAggregate12Month().calculate(aggregation, turnovers);

    }

    void calculateAggregationForOther(final TurnoverAggregation aggregation, final List<Turnover> turnovers){

        // reset
        aggregation.setGrossAmount1MCY_1(BigDecimal.ZERO);
        aggregation.setNetAmount1MCY_1(BigDecimal.ZERO);
        aggregation.setGrossAmount1MCY_2(BigDecimal.ZERO);
        aggregation.setNetAmount1MCY_2(BigDecimal.ZERO);
        aggregation.setComments12MCY(null);
        aggregation.setComments12MPY(null);

        LocalDate aggDate = aggregation.getDate();
        Turnover to = turnovers.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(1))).findFirst().orElse(null);
        if (to!=null){
            aggregation.setGrossAmount1MCY_1(to.getGrossAmount());
            aggregation.setNetAmount1MCY_1(to.getNetAmount());
        }
        to = turnovers.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(2))).findFirst().orElse(null);
        if (to!=null ) {
            aggregation.setGrossAmount1MCY_2(to.getGrossAmount());
            aggregation.setNetAmount1MCY_2(to.getNetAmount());
        }

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
        aggregation.setComments12MCY(ccy);
        aggregation.setComments12MPY(cpy);
    }

    public void calculateTurnoverAggregateForPeriod(
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

        // TODO: implement

//        final Optional<Turnover> aggCY = valuesCurrentYear.stream()
//                .reduce(Turnover::addIgnoringDate);
//
//        final Optional<Turnover> aggPY = valuesPreviousYear.stream()
//                .reduce(Turnover::addIgnoringDate);
//
//        if (aggCY.isPresent()) {
//            turnoverAggregateForPeriod.setGrossAmount(aggCY.get().getGrossAmount());
//            turnoverAggregateForPeriod.setNetAmount(aggCY.get().getNetAmount());
//            turnoverAggregateForPeriod.setTurnoverCount(aggCY.get().getTurnoverCount());
//            turnoverAggregateForPeriod.setNonComparableThisYear(aggCY.get().isNonComparable());
//        }
//        if (aggPY.isPresent()) {
//            turnoverAggregateForPeriod.setGrossAmountPreviousYear(aggPY.get().getGrossAmount());
//            turnoverAggregateForPeriod.setNetAmountPreviousYear(aggPY.get().getNetAmount());
//            turnoverAggregateForPeriod.setTurnoverCountPreviousYear(aggPY.get().getTurnoverCount());
//            turnoverAggregateForPeriod.setNonComparablePreviousYear(aggPY.get().isNonComparable());
//        }
//
//        final boolean comparable = isComparable(
//                turnoverAggregateForPeriod.getAggregationPeriod(),
//                turnoverAggregateForPeriod.getTurnoverCount(),
//                turnoverAggregateForPeriod.getTurnoverCountPreviousYear(),
//                turnoverAggregateForPeriod.isNonComparableThisYear(),
//                turnoverAggregateForPeriod.isNonComparablePreviousYear()
//                );
//        turnoverAggregateForPeriod.setComparable(comparable);

    }

    private List<Turnover> getTurnoversForInterval(
            final List<Turnover> turnovers,
            final LocalDateInterval intervalCY) {
        return turnovers.stream()
                .filter(t -> intervalCY.contains(t.getDate()))
                .collect(Collectors.toList());
    }

    public void calculatePurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<Turnover> turnoverValueObjects) {
        final LocalDate periodStartDate = purchaseCountAggregateForPeriod.getAggregationPeriod()
                .periodStartDateFor(aggregationDate);
        final LocalDate periodEndDate = aggregationDate;
        if (periodStartDate.isAfter(periodEndDate)) return;

        final LocalDateInterval intervalCY = LocalDateInterval.including(periodStartDate, periodEndDate);
        final List<Turnover> valuesCurrentYear = getTurnoversForInterval(turnoverValueObjects,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(periodStartDate.minusYears(1), periodEndDate.minusYears(1));
        final List<Turnover> valuesPreviousYear = getTurnoversForInterval(turnoverValueObjects,
                intervalPY);

        if (valuesCurrentYear.isEmpty() && valuesPreviousYear.isEmpty()) return;

        resetPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod);

        // TODO: implement

//        final Optional<TurnoverValueObject> aggCY = valuesCurrentYear.stream()
//                .reduce(TurnoverValueObject::addIgnoringDate);
//
//        final Optional<TurnoverValueObject> aggPY = valuesPreviousYear.stream()
//                .reduce(TurnoverValueObject::addIgnoringDate);
//
//        if (aggCY.isPresent()) {
//            purchaseCountAggregateForPeriod.setCount(aggCY.get().getPurchaseCount());
//        }
//        if (aggPY.isPresent()) {
//            purchaseCountAggregateForPeriod.setCountPreviousYear(aggPY.get().getPurchaseCount());
//        }
//
//        final boolean comparable = isComparable(
//                purchaseCountAggregateForPeriod.getAggregationPeriod(),
//                aggCY.isPresent() ? aggCY.get().getTurnoverCount() : 0,
//                aggPY.isPresent() ? aggPY.get().getTurnoverCount() : 0,
//                aggCY.isPresent() ? aggCY.get().isNonComparable() : false,
//                aggPY.isPresent() ? aggPY.get().isNonComparable() : false);
//        purchaseCountAggregateForPeriod.setComparable(comparable);

    }

    public void calculateTurnoverAggregateToDate(
            final TurnoverAggregateToDate turnoverAggregateToDate,
            final LocalDate aggregationDate,
            final List<Turnover> turnoverValueObjects){

        final LocalDate startOfTheYear = new LocalDate(aggregationDate.getYear(), 1, 1);
        final LocalDateInterval intervalCY = LocalDateInterval.including(startOfTheYear, aggregationDate);
        final List<Turnover> valuesCurrentYear = getTurnoversForInterval(turnoverValueObjects,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(startOfTheYear.minusYears(1), aggregationDate.minusYears(1));
        final List<Turnover> valuesPreviousYear = getTurnoversForInterval(turnoverValueObjects,
                intervalPY);

        if (valuesCurrentYear.isEmpty() && valuesPreviousYear.isEmpty()) return;

        resetTurnoverAggregateToDate(turnoverAggregateToDate);

        //TODO: implement

//        final Optional<TurnoverValueObject> aggCY = valuesCurrentYear.stream()
//                .reduce(TurnoverValueObject::addIgnoringDate);
//
//        final Optional<TurnoverValueObject> aggPY = valuesPreviousYear.stream()
//                .reduce(TurnoverValueObject::addIgnoringDate);
//
//        if (aggCY.isPresent()) {
//            turnoverAggregateToDate.setGrossAmount(aggCY.get().getGrossAmount());
//            turnoverAggregateToDate.setNetAmount(aggCY.get().getNetAmount());
//            turnoverAggregateToDate.setTurnoverCount(aggCY.get().getTurnoverCount());
//            turnoverAggregateToDate.setNonComparableThisYear(aggCY.get().isNonComparable());
//        }
//        if (aggPY.isPresent()) {
//            turnoverAggregateToDate.setGrossAmountPreviousYear(aggPY.get().getGrossAmount());
//            turnoverAggregateToDate.setNetAmountPreviousYear(aggPY.get().getNetAmount());
//            turnoverAggregateToDate.setTurnoverCountPreviousYear(aggPY.get().getTurnoverCount());
//            turnoverAggregateToDate.setNonComparablePreviousYear(aggPY.get().isNonComparable());
//        }
//
//        final boolean comparable = isComparableToDate(
//                aggregationDate,
//                turnoverAggregateToDate.getTurnoverCount(),
//                turnoverAggregateToDate.getTurnoverCountPreviousYear(),
//                turnoverAggregateToDate.isNonComparableThisYear(),
//                turnoverAggregateToDate.isNonComparablePreviousYear()
//        );
//        turnoverAggregateToDate.setComparable(comparable);

    }

    private void resetTurnoverAggregateToDate(final TurnoverAggregateToDate agg) {
        agg.setGrossAmount(BigDecimal.ZERO);
        agg.setNetAmount(BigDecimal.ZERO);
        agg.setTurnoverCount(0);
        agg.setNonComparableThisYear(false);
        agg.setGrossAmountPreviousYear(BigDecimal.ZERO);
        agg.setNetAmountPreviousYear(BigDecimal.ZERO);
        agg.setTurnoverCountPreviousYear(0);
        agg.setNonComparablePreviousYear(false);
        agg.setComparable(false);
    }

    private void resetTurnoverAggregateForPeriod(final TurnoverAggregateForPeriod agg){
        agg.setGrossAmount(BigDecimal.ZERO);
        agg.setNetAmount(BigDecimal.ZERO);
        agg.setTurnoverCount(0);
        agg.setNonComparableThisYear(false);
        agg.setGrossAmountPreviousYear(BigDecimal.ZERO);
        agg.setNetAmountPreviousYear(BigDecimal.ZERO);
        agg.setTurnoverCountPreviousYear(0);
        agg.setNonComparablePreviousYear(false);
        agg.setComparable(false);
    }

    private void resetPurchaseCountAggregateForPeriod(final PurchaseCountAggregateForPeriod agg){
        agg.setCount(BigInteger.ZERO);
        agg.setCountPreviousYear(BigInteger.ZERO);
        agg.setComparable(false);
    }


    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject ClockService clockService;
}
