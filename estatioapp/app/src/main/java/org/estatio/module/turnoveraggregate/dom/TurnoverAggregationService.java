package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TurnoverAggregationService {

    public static Logger LOG = LoggerFactory.getLogger(TurnoverAggregationService.class);

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
            if (occupanciesOfSameUnit.size()>=1){
                occupanciesToExamine.addAll(occupanciesOfSameUnit);
            }
            if (occupanciesOfSameUnit.isEmpty() && occupanciesOfDifferentUnit.size()==1){
                occupanciesToExamine.addAll(occupanciesOfDifferentUnit);
            }
            if (occupanciesOfSameUnit.isEmpty() && occupanciesOfDifferentUnit.size()>1){
                LOG.warn(String.format("No occupancy found for lease %s with unit %s and multiple occupancies found for other unit - HOW TO HANDLE?", l.getReference(), unit.getReference()));
            }
        });
        return occupanciesToExamine;
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
        List<LocalDate> aggregationDates = aggregationDatesForTurnoverReportingConfig(config);
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

    List<LocalDate> aggregationDatesForTurnoverReportingConfig(final TurnoverReportingConfig config){
        if (config.getFrequency()!=Frequency.MONTHLY) return Collections.emptyList();

        final LocalDate startDate = config.getEffectiveStartDate().withDayOfMonth(1); // withDayOfMonth(1) should be redundant here
        final LocalDate endDate = config.getEndDate()!=null ? config.getEndDate().withDayOfMonth(1).plusMonths(24) : clockService.now().withDayOfMonth(1).plusMonths(24);
        if (endDate.isBefore(startDate)) return Collections.emptyList();

        List<LocalDate> result = new ArrayList<>();
        LocalDate d = startDate;
        while (!d.isAfter(endDate)){
            result.add(d);
            d = d.plusMonths(1);
        }
        return result.stream().sorted().collect(Collectors.toList());
    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject ClockService clockService;
}
