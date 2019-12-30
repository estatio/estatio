package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TurnoverAggregationService {

    public static Logger LOG = LoggerFactory.getLogger(TurnoverAggregationService.class);

    public TurnoverAggregateForPeriod aggregateForPeriod(final TurnoverAggregateForPeriod turnoverAggregateForPeriod){
        final Type type = turnoverAggregateForPeriod.getAggregation().getType();
        final Frequency frequency = turnoverAggregateForPeriod.getAggregation().getFrequency();
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

        final List<Turnover> turnoversToAggregate = turnoversToAggregate(turnoverAggregateForPeriod, false);
        final BigDecimal totalGross = turnoversToAggregate.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNet = turnoversToAggregate.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final List<Turnover> turnoversToAggregatePrevYear = turnoversToAggregate(turnoverAggregateForPeriod, true);
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

    // TODO: handle previous occupancies / leases
    List<Turnover> turnoversToAggregate(final TurnoverAggregateForPeriod turnoverAggregateForPeriod, boolean prevYear){
        final Occupancy occupancy = turnoverAggregateForPeriod.getAggregation().getOccupancy();
        final Type type = turnoverAggregateForPeriod.getAggregation().getType();
        final Frequency frequency = turnoverAggregateForPeriod.getAggregation().getFrequency();

        final LocalDate periodEndDate = prevYear ? turnoverAggregateForPeriod.getAggregation().getDate().minusYears(1) : turnoverAggregateForPeriod.getAggregation().getDate();
        final LocalDate periodStartDate = turnoverAggregateForPeriod.getAggregationPeriod().periodStartDateFor(periodEndDate);
        return turnoverRepository.findApprovedByOccupancyAndTypeAndFrequencyAndPeriod(
                occupancy,
                type,
                frequency,
                periodStartDate,
                periodEndDate);
    }

    boolean containsNonComparableTurnover(final List<Turnover> turnoverList){
        return turnoverList.stream().anyMatch(t->t.isNonComparable());
    }

    boolean isComparable(final AggregationPeriod period, final int numberOfTurnoversThisYear, final int numberOfTurnoversPreviousYear, final boolean nonComparableThisYear, final boolean nonComparablePreviousYear){
        return !nonComparableThisYear && !nonComparablePreviousYear && numberOfTurnoversThisYear >= period.getMinNumberOfTurnovers() && numberOfTurnoversPreviousYear >=period.getMinNumberOfTurnovers();
    }

    @Inject TurnoverRepository turnoverRepository;

}
