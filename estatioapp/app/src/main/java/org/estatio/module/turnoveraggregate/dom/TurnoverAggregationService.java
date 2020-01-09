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
import org.joda.time.Period;
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

    public static LocalDate MIN_AGGREGATION_DATE = new LocalDate(2010, 1,1);

    public TurnoverAggregateForPeriod aggregateForPeriod(final TurnoverAggregateForPeriod turnoverAggregateForPeriod, final Occupancy occupancy, final LocalDate aggregationDate, final Type type, final Frequency frequency){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No aggregate-for-period implementation for type %s found.",
                    type));
            return turnoverAggregateForPeriod;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No aggregate-for-period implementation for frequency %s found.",
                    frequency));
            return turnoverAggregateForPeriod;
        }

        final List<Turnover> turnoversToAggregate = turnoversToAggregateForPeriod(occupancy, aggregationDate, turnoverAggregateForPeriod.getAggregationPeriod(), type, frequency, false);
        final BigDecimal totalGross = turnoversToAggregate.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNet = turnoversToAggregate.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final List<Turnover> turnoversToAggregatePrevYear = turnoversToAggregateForPeriod(occupancy, aggregationDate, turnoverAggregateForPeriod.getAggregationPeriod(), type, frequency, true);
        final BigDecimal totalGrossPY = turnoversToAggregatePrevYear.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNetPY = turnoversToAggregatePrevYear.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final boolean nonComparableThisYear = containsNonComparableTurnover(turnoversToAggregate);
        final boolean nonComparablePreviousYear = containsNonComparableTurnover(turnoversToAggregatePrevYear);
        final int numberOfTurnoversThisYear = turnoversToAggregate.size();
        final int numberOfTurnoversPreviousYear = turnoversToAggregatePrevYear.size();

        turnoverAggregateForPeriod.setGrossAmount(totalGross);
        turnoverAggregateForPeriod.setNetAmount(totalNet);
        turnoverAggregateForPeriod.setTurnoverCount(numberOfTurnoversThisYear);
        turnoverAggregateForPeriod.setGrossAmountPreviousYear(totalGrossPY);
        turnoverAggregateForPeriod.setNetAmountPreviousYear(totalNetPY);
        turnoverAggregateForPeriod.setTurnoverCountPreviousYear(numberOfTurnoversPreviousYear);
        turnoverAggregateForPeriod.setNonComparableThisYear(nonComparableThisYear);
        turnoverAggregateForPeriod.setNonComparablePreviousYear(nonComparablePreviousYear);
        turnoverAggregateForPeriod.setComparable(isComparable(turnoverAggregateForPeriod.getAggregationPeriod(), numberOfTurnoversThisYear, numberOfTurnoversPreviousYear, nonComparableThisYear, nonComparablePreviousYear));

        return turnoverAggregateForPeriod;
    }

    public TurnoverAggregateToDate aggregateToDate(final TurnoverAggregateToDate turnoverAggregateToDate, final Occupancy occupancy, final LocalDate aggregationDate, final Type type, final Frequency frequency){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No aggregate-to-aggregateToDate implementation for type %s found.",
                    type));
            return turnoverAggregateToDate;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No aggregate-to-aggregateToDate implementation for frequency %s found.",
                    frequency));
            return turnoverAggregateToDate;
        }
        final LocalDate startOfTheYear = new LocalDate(aggregationDate.getYear(), 1, 1);

        final List<Turnover> turnoversToAggregate = turnoversToAggregate(occupancy, aggregationDate,
                startOfTheYear, aggregationDate, type, frequency);
        final BigDecimal totalGross = turnoversToAggregate.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNet = turnoversToAggregate.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final List<Turnover> turnoversToAggregatePrevYear = turnoversToAggregate(occupancy, aggregationDate,
                startOfTheYear.minusYears(1), aggregationDate.minusYears(1), type, frequency);
        final BigDecimal totalGrossPY = turnoversToAggregatePrevYear.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNetPY = turnoversToAggregatePrevYear.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final boolean nonComparableThisYear = containsNonComparableTurnover(turnoversToAggregate);
        final boolean nonComparablePreviousYear = containsNonComparableTurnover(turnoversToAggregatePrevYear);
        final int numberOfTurnoversThisYear = turnoversToAggregate.size();
        final int numberOfTurnoversPreviousYear = turnoversToAggregatePrevYear.size();

        turnoverAggregateToDate.setGrossAmount(totalGross);
        turnoverAggregateToDate.setNetAmount(totalNet);
        turnoverAggregateToDate.setTurnoverCount(numberOfTurnoversThisYear);
        turnoverAggregateToDate.setGrossAmountPreviousYear(totalGrossPY);
        turnoverAggregateToDate.setNetAmountPreviousYear(totalNetPY);
        turnoverAggregateToDate.setTurnoverCountPreviousYear(numberOfTurnoversPreviousYear);
        turnoverAggregateToDate.setNonComparableThisYear(nonComparableThisYear);
        turnoverAggregateToDate.setNonComparablePreviousYear(nonComparablePreviousYear);
        turnoverAggregateToDate.setComparable(isComparableToDate(aggregationDate, numberOfTurnoversThisYear, numberOfTurnoversPreviousYear, nonComparableThisYear, nonComparablePreviousYear));

        return turnoverAggregateToDate;
    }

    public PurchaseCountAggregateForPeriod aggregateForPurchaseCount(final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod, final Occupancy occupancy, final LocalDate aggregationDate, final Type type, final Frequency frequency){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No purchase-count-aggregate-for-period implementation for type %s found.",
                    type));
            return purchaseCountAggregateForPeriod;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No purchase-count-aggregate-for-period implementation for frequency %s found.",
                    frequency));
            return purchaseCountAggregateForPeriod;
        }

        final List<Turnover> turnoversToAggregate = turnoversToAggregateForPeriod(occupancy, aggregationDate, purchaseCountAggregateForPeriod.getAggregationPeriod(), type, frequency, false);
        final BigInteger count = turnoversToAggregate.stream()
                .filter(t->t.getPurchaseCount()!=null)
                .map(t -> t.getPurchaseCount())
                .reduce(BigInteger.ZERO, BigInteger::add);

        final List<Turnover> turnoversToAggregatePrevYear = turnoversToAggregateForPeriod(occupancy, aggregationDate, purchaseCountAggregateForPeriod.getAggregationPeriod(), type, frequency, true);
        final BigInteger countPY = turnoversToAggregatePrevYear.stream()
                .filter(t->t.getPurchaseCount()!=null)
                .map(t -> t.getPurchaseCount())
                .reduce(BigInteger.ZERO, BigInteger::add);
        final boolean nonComparableThisYear = containsNonComparableTurnover(turnoversToAggregate);
        final boolean nonComparablePreviousYear = containsNonComparableTurnover(turnoversToAggregatePrevYear);
        final int numberOfTurnoversThisYear = turnoversToAggregate.size();
        final int numberOfTurnoversPreviousYear = turnoversToAggregatePrevYear.size();

        purchaseCountAggregateForPeriod.setCount(count);
        purchaseCountAggregateForPeriod.setCountPreviousYear(countPY);
        purchaseCountAggregateForPeriod.setComparable(isComparable(purchaseCountAggregateForPeriod.getAggregationPeriod(), numberOfTurnoversThisYear, numberOfTurnoversPreviousYear, nonComparableThisYear, nonComparablePreviousYear));

        return purchaseCountAggregateForPeriod;
    }

    public TurnoverAggregation aggregateOtherAggregationProperties(final TurnoverAggregation turnoverAggregation) {
        if (turnoverAggregation.getType() != Type.PRELIMINARY ) {
            LOG.warn(String.format("No aggregate-other-aggregation-properties implementation for type %s found.",
                    turnoverAggregation.getType()));
            return turnoverAggregation;
        }
        if (turnoverAggregation.getFrequency() != Frequency.MONTHLY) {
            LOG.warn(String.format("No aggregate-other-aggregation-properties for frequency %s found.",
                    turnoverAggregation.getFrequency()));
            return turnoverAggregation;
        }
        final List<Turnover> turnoversToAggregateForMinusMonth1 = turnoversToAggregate(turnoverAggregation.getOccupancy(), turnoverAggregation.getDate(),
                turnoverAggregation.getDate().minusMonths(1), turnoverAggregation.getDate().minusMonths(1), turnoverAggregation.getType(), turnoverAggregation.getFrequency());
        final BigDecimal totalGrossMinM1 = turnoversToAggregateForMinusMonth1.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNetMinM1 = turnoversToAggregateForMinusMonth1.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final List<Turnover> turnoversToAggregateForMinusMonth2 = turnoversToAggregate(turnoverAggregation.getOccupancy(), turnoverAggregation.getDate(),
                turnoverAggregation.getDate().minusMonths(2), turnoverAggregation.getDate().minusMonths(2), turnoverAggregation.getType(), turnoverAggregation.getFrequency());
        final BigDecimal totalGrossMinM2 = turnoversToAggregateForMinusMonth2.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNetMinM2 = turnoversToAggregateForMinusMonth2.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final List<Turnover> turnoversToAggregateForComments = turnoversToAggregateForPeriod(turnoverAggregation.getOccupancy(), turnoverAggregation.getDate(), AggregationPeriod.P_12M, turnoverAggregation.getType(), turnoverAggregation.getFrequency(), false);
        final String comments12M = turnoversToAggregateForComments.stream()
                .filter(t->t.getComments()!=null && t.getComments()!="")
                .sorted(Comparator.reverseOrder())
                .map(t->t.getComments())
                .reduce("", String::concat);
        final List<Turnover> turnoversToAggregateForCommentsPY = turnoversToAggregateForPeriod(turnoverAggregation.getOccupancy(), turnoverAggregation.getDate(), AggregationPeriod.P_12M, turnoverAggregation.getType(), turnoverAggregation.getFrequency(), true);
        final String comments12MPY = turnoversToAggregateForCommentsPY.stream()
                .filter(t->t.getComments()!=null && t.getComments()!="")
                .map(t->t.getComments())
                .sorted(Comparator.reverseOrder())
                .reduce("", String::concat);;

        turnoverAggregation.setGrossAmount1MCY_1(totalGrossMinM1);
        turnoverAggregation.setNetAmount1MCY_1(totalNetMinM1);
        turnoverAggregation.setGrossAmount1MCY_2(totalGrossMinM2);
        turnoverAggregation.setNetAmount1MCY_2(totalNetMinM2);
        turnoverAggregation.setComments12MCY(comments12M);
        turnoverAggregation.setComments12MPY(comments12MPY);

        return turnoverAggregation;
    }

    List<Turnover> turnoversToAggregateForPeriod(final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear){
        final LocalDate periodEndDate = prevYear ? date.minusYears(1) : date;
        final LocalDate periodStartDate = aggregationPeriod.periodStartDateFor(periodEndDate);
        return turnoversToAggregate(occupancy, date, periodStartDate, periodEndDate, type, frequency);
    }

    List<Turnover> turnoversToAggregate(final Occupancy occupancy, final LocalDate date, final LocalDate periodStartDate, final LocalDate periodEndDate, final Type type, final Frequency frequency){
        final Lease lease = occupancy.getLease();
        final Unit unit = occupancy.getUnit();
        final List<Lease> leasesToExamine = leasesToExamine(lease);
        final List<Occupancy> occupanciesToExamine = occupanciesToExamine(unit, leasesToExamine);

        List<Turnover> result = new ArrayList<>();
        occupanciesToExamine.forEach(o->{
            result.addAll(
                    turnoverRepository.findApprovedByOccupancyAndTypeAndFrequencyAndPeriod(
                            o,
                            type,
                            frequency,
                            periodStartDate,
                            periodEndDate)
            );
        });
        return result;
    }



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

    public void aggregateForConfig(final TurnoverReportingConfig config) {
        // TODO: bring actual aggregation to background service?
        List<LocalDate> aggregationDates = aggregationDatesForTurnoverReportingConfig(config, clockService.now());
        aggregationDates.forEach(ad->{
            final TurnoverAggregation aggregation = turnoverAggregationRepository.findOrCreate(
                    config.getOccupancy(),
                    ad,
                    config.getType(),
                    config.getFrequency(),
                    config.getCurrency());
            aggregation.aggregate();
        });
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

    public List<TurnoverValueObject> turnoversToAggregateForOccupancySortedAsc(final Occupancy occupancy, final Type type, final Frequency frequency, final LocalDate aggregationDate){
        final Lease lease = occupancy.getLease();
        final Unit unit = occupancy.getUnit();
        final List<Lease> leasesToExamine = leasesToExamine(lease);
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

        List<TurnoverValueObject> result = new ArrayList<>();

        TurnoverValueObject previous = null;
        for (Turnover t : turnoversSorted){
            TurnoverValueObject tov = new TurnoverValueObject(t);
            if (previous!=null && previous.getDate().equals(tov.getDate())){
                previous.add(tov);
            } else {
                result.add(tov);
            }
            previous = tov;
        }

        return result.stream().sorted().collect(Collectors.toList());
    }

    public void aggregateTurnoversForLease(final Lease lease, final Type type, final Frequency frequency, final LocalDate aggregationDate){
        for (Occupancy o : lease.getOccupancies()){
            aggregateTurnoversForOccupancy(o, type, frequency, aggregationDate);
        }
    }

    public void aggregateTurnoversForOccupancy(final Occupancy occupancy, final Type type, final Frequency frequency, final LocalDate aggregationDate){
        if (type != Type.PRELIMINARY ) {
            LOG.warn(String.format("No aggregate-turnovers-for-occupancy implementation for type %s found.",
                    type));
            return;
        }
        if (frequency != Frequency.MONTHLY) {
            LOG.warn(String.format("No aggregate-turnovers-for-occupancy for frequency %s found.",
                    frequency));
            return;
        }

        final TurnoverReportingConfig config = turnoverReportingConfigRepository
                .findUnique(occupancy, type);
        // for efficiency reasons
        if (config == null) return;

        LocalDate effectiveAggregationDate = aggregationDate==null ? MIN_AGGREGATION_DATE : aggregationDate;

        final List<TurnoverValueObject> turnoverValueObjects = turnoversToAggregateForOccupancySortedAsc(occupancy, Type.PRELIMINARY,
                Frequency.MONTHLY, effectiveAggregationDate);

        // for efficiency reasons - currently we never look back more than 24 months
        final  List<TurnoverValueObject> turnoverValueObjectsFiltered = turnoverValueObjects.stream().filter(t->!t.getDate().isBefore(effectiveAggregationDate.minusMonths(24))).collect(
                    Collectors.toList());

        // guard and efficiency
        if (turnoverValueObjectsFiltered.isEmpty()) return;

        Currency currency = config.getCurrency();

        final List<TurnoverAggregation> aggregationsToCalculate = findOrCreateAggregationsForMonthly(occupancy, turnoverValueObjectsFiltered, aggregationDate, currency, type, frequency);

        aggregationsToCalculate.forEach(a->a.calculate(turnoverValueObjectsFiltered));

    }

    List<TurnoverAggregation> findOrCreateAggregationsForMonthly(final Occupancy occupancy, final List<TurnoverValueObject> turnoverValueObjects, final LocalDate aggregationDate, final Currency currency, final Type type, final Frequency frequency){

        LocalDate startDate = determineStartDate(aggregationDate, turnoverValueObjects.get(0).getDate());
        LocalDate endDate = determineEndDate(occupancy, aggregationDate);
        List<TurnoverAggregation> result = new ArrayList<>();

        LocalDate date = startDate.withDayOfMonth(1); // extra safeguard; should not be needed for Monthly turnovers
        while (!date.isAfter(endDate)){
            result.add(turnoverAggregationRepository.findOrCreate(occupancy, date, type, frequency, currency));
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

    public void calculateTurnoverAggregation(final TurnoverAggregation aggregation, final List<TurnoverValueObject> turnoverValueObjects) {

        calculateAggregationForOther(aggregation, turnoverValueObjects);

        aggregation.getAggregate1Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getAggregate2Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getAggregate3Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getAggregate6Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getAggregate9Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getAggregate12Month().calculate(aggregation, turnoverValueObjects);

        aggregation.getAggregateToDate().calculate(aggregation, turnoverValueObjects);

        aggregation.getPurchaseCountAggregate1Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getPurchaseCountAggregate3Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getPurchaseCountAggregate6Month().calculate(aggregation, turnoverValueObjects);
        aggregation.getPurchaseCountAggregate12Month().calculate(aggregation, turnoverValueObjects);

    }

    void calculateAggregationForOther(final TurnoverAggregation aggregation, final List<TurnoverValueObject> turnoverValueObjects){

        // reset
        aggregation.setGrossAmount1MCY_1(BigDecimal.ZERO);
        aggregation.setNetAmount1MCY_1(BigDecimal.ZERO);
        aggregation.setGrossAmount1MCY_2(BigDecimal.ZERO);
        aggregation.setNetAmount1MCY_2(BigDecimal.ZERO);
        aggregation.setComments12MCY(null);
        aggregation.setComments12MPY(null);

        LocalDate aggDate = aggregation.getDate();
        TurnoverValueObject tvo = turnoverValueObjects.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(1))).findFirst().orElse(null);
        if (tvo!=null){
            aggregation.setGrossAmount1MCY_1(tvo.getGrossAmount());
            aggregation.setNetAmount1MCY_1(tvo.getNetAmount());
        }
        tvo = turnoverValueObjects.stream()
                .filter(t -> t.getDate().equals(aggDate.minusMonths(2))).findFirst().orElse(null);
        if (tvo!=null ) {
            aggregation.setGrossAmount1MCY_2(tvo.getGrossAmount());
            aggregation.setNetAmount1MCY_2(tvo.getNetAmount());
        }

        LocalDateInterval I12MCY = LocalDateInterval.including(aggDate.minusMonths(11), aggDate);
        LocalDateInterval I12MPY = LocalDateInterval.including(aggDate.minusYears(1).minusMonths(11), aggDate.minusYears(1));
        String ccy = null;
        String cpy = null;
        for (TurnoverValueObject to : turnoverValueObjects){
            if (to.getComments()!=null && I12MCY.contains(to.getDate())){
                if (ccy==null ){
                    ccy = to.getComments();
                } else {
                    ccy = ccy.concat(" | ").concat(to.getComments());
                }
            }
            if (to.getComments()!=null && I12MPY.contains(to.getDate())){
                if (cpy==null ){
                    cpy = to.getComments();
                } else {
                    cpy = cpy.concat(" | ").concat(to.getComments());
                }
            }
        }
        aggregation.setComments12MCY(ccy);
        aggregation.setComments12MPY(cpy);
    }

    public void calculateTurnoverAggregateForPeriod(
            final TurnoverAggregateForPeriod turnoverAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<TurnoverValueObject> turnoverValueObjects) {
        final LocalDate periodStartDate = turnoverAggregateForPeriod.getAggregationPeriod()
                .periodStartDateFor(aggregationDate);
        final LocalDate periodEndDate = aggregationDate;
        if (periodStartDate.isAfter(periodEndDate)) return;

        final LocalDateInterval intervalCY = LocalDateInterval.including(periodStartDate, periodEndDate);
        final List<TurnoverValueObject> valuesCurrentYear = getTurnoverValueObjectsForInterval(turnoverValueObjects,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(periodStartDate.minusYears(1), periodEndDate.minusYears(1));
        final List<TurnoverValueObject> valuesPreviousYear = getTurnoverValueObjectsForInterval(turnoverValueObjects,
                intervalPY);

        if (valuesCurrentYear.isEmpty() && valuesPreviousYear.isEmpty()) return;

        resetTurnoverAggregateForPeriod(turnoverAggregateForPeriod);

        final Optional<TurnoverValueObject> aggCY = valuesCurrentYear.stream()
                .reduce(TurnoverValueObject::addIgnoringDate);

        final Optional<TurnoverValueObject> aggPY = valuesPreviousYear.stream()
                .reduce(TurnoverValueObject::addIgnoringDate);

        if (aggCY.isPresent()) {
            turnoverAggregateForPeriod.setGrossAmount(aggCY.get().getGrossAmount());
            turnoverAggregateForPeriod.setNetAmount(aggCY.get().getNetAmount());
            turnoverAggregateForPeriod.setTurnoverCount(aggCY.get().getTurnoverCount());
            turnoverAggregateForPeriod.setNonComparableThisYear(aggCY.get().isNonComparable());
        }
        if (aggPY.isPresent()) {
            turnoverAggregateForPeriod.setGrossAmountPreviousYear(aggPY.get().getGrossAmount());
            turnoverAggregateForPeriod.setNetAmountPreviousYear(aggPY.get().getNetAmount());
            turnoverAggregateForPeriod.setTurnoverCountPreviousYear(aggPY.get().getTurnoverCount());
            turnoverAggregateForPeriod.setNonComparablePreviousYear(aggPY.get().isNonComparable());
        }

        final boolean comparable = isComparable(
                turnoverAggregateForPeriod.getAggregationPeriod(),
                turnoverAggregateForPeriod.getTurnoverCount(),
                turnoverAggregateForPeriod.getTurnoverCountPreviousYear(),
                turnoverAggregateForPeriod.isNonComparableThisYear(),
                turnoverAggregateForPeriod.isNonComparablePreviousYear()
                );
        turnoverAggregateForPeriod.setComparable(comparable);

    }

    private List<TurnoverValueObject> getTurnoverValueObjectsForInterval(
            final List<TurnoverValueObject> turnoverValueObjects,
            final LocalDateInterval intervalCY) {
        return turnoverValueObjects.stream()
                .map(t -> new TurnoverValueObject(
                        t)) // NOTE: we make copies in order to preserve the values in turnoverValueObjects List
                .filter(t -> intervalCY.contains(t.getDate()))
                .collect(Collectors.toList());
    }

    public void calculatePurchaseCountAggregateForPeriod(
            final PurchaseCountAggregateForPeriod purchaseCountAggregateForPeriod,
            final LocalDate aggregationDate,
            final List<TurnoverValueObject> turnoverValueObjects) {
        final LocalDate periodStartDate = purchaseCountAggregateForPeriod.getAggregationPeriod()
                .periodStartDateFor(aggregationDate);
        final LocalDate periodEndDate = aggregationDate;
        if (periodStartDate.isAfter(periodEndDate)) return;

        final LocalDateInterval intervalCY = LocalDateInterval.including(periodStartDate, periodEndDate);
        final List<TurnoverValueObject> valuesCurrentYear = getTurnoverValueObjectsForInterval(turnoverValueObjects,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(periodStartDate.minusYears(1), periodEndDate.minusYears(1));
        final List<TurnoverValueObject> valuesPreviousYear = getTurnoverValueObjectsForInterval(turnoverValueObjects,
                intervalPY);

        if (valuesCurrentYear.isEmpty() && valuesPreviousYear.isEmpty()) return;

        resetPurchaseCountAggregateForPeriod(purchaseCountAggregateForPeriod);

        final Optional<TurnoverValueObject> aggCY = valuesCurrentYear.stream()
                .reduce(TurnoverValueObject::addIgnoringDate);

        final Optional<TurnoverValueObject> aggPY = valuesPreviousYear.stream()
                .reduce(TurnoverValueObject::addIgnoringDate);

        if (aggCY.isPresent()) {
            purchaseCountAggregateForPeriod.setCount(aggCY.get().getPurchaseCount());
        }
        if (aggPY.isPresent()) {
            purchaseCountAggregateForPeriod.setCountPreviousYear(aggPY.get().getPurchaseCount());
        }

        final boolean comparable = isComparable(
                purchaseCountAggregateForPeriod.getAggregationPeriod(),
                aggCY.isPresent() ? aggCY.get().getTurnoverCount() : 0,
                aggPY.isPresent() ? aggPY.get().getTurnoverCount() : 0,
                aggCY.isPresent() ? aggCY.get().isNonComparable() : false,
                aggPY.isPresent() ? aggPY.get().isNonComparable() : false);
        purchaseCountAggregateForPeriod.setComparable(comparable);

    }

    public void calculateTurnoverAggregateToDate(
            final TurnoverAggregateToDate turnoverAggregateToDate,
            final LocalDate aggregationDate,
            final List<TurnoverValueObject> turnoverValueObjects){

        final LocalDate startOfTheYear = new LocalDate(aggregationDate.getYear(), 1, 1);
        final LocalDateInterval intervalCY = LocalDateInterval.including(startOfTheYear, aggregationDate);
        final List<TurnoverValueObject> valuesCurrentYear = getTurnoverValueObjectsForInterval(turnoverValueObjects,
                intervalCY);

        final LocalDateInterval intervalPY = LocalDateInterval.including(startOfTheYear.minusYears(1), aggregationDate.minusYears(1));
        final List<TurnoverValueObject> valuesPreviousYear = getTurnoverValueObjectsForInterval(turnoverValueObjects,
                intervalPY);

        if (valuesCurrentYear.isEmpty() && valuesPreviousYear.isEmpty()) return;

        resetTurnoverAggregateToDate(turnoverAggregateToDate);

        final Optional<TurnoverValueObject> aggCY = valuesCurrentYear.stream()
                .reduce(TurnoverValueObject::addIgnoringDate);

        final Optional<TurnoverValueObject> aggPY = valuesPreviousYear.stream()
                .reduce(TurnoverValueObject::addIgnoringDate);

        if (aggCY.isPresent()) {
            turnoverAggregateToDate.setGrossAmount(aggCY.get().getGrossAmount());
            turnoverAggregateToDate.setNetAmount(aggCY.get().getNetAmount());
            turnoverAggregateToDate.setTurnoverCount(aggCY.get().getTurnoverCount());
            turnoverAggregateToDate.setNonComparableThisYear(aggCY.get().isNonComparable());
        }
        if (aggPY.isPresent()) {
            turnoverAggregateToDate.setGrossAmountPreviousYear(aggPY.get().getGrossAmount());
            turnoverAggregateToDate.setNetAmountPreviousYear(aggPY.get().getNetAmount());
            turnoverAggregateToDate.setTurnoverCountPreviousYear(aggPY.get().getTurnoverCount());
            turnoverAggregateToDate.setNonComparablePreviousYear(aggPY.get().isNonComparable());
        }

        final boolean comparable = isComparableToDate(
                aggregationDate,
                turnoverAggregateToDate.getTurnoverCount(),
                turnoverAggregateToDate.getTurnoverCountPreviousYear(),
                turnoverAggregateToDate.isNonComparableThisYear(),
                turnoverAggregateToDate.isNonComparablePreviousYear()
        );
        turnoverAggregateToDate.setComparable(comparable);

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
