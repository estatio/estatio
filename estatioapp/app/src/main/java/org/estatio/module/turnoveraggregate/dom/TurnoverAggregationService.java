package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.client.util.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.Lease;
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

    public TurnoverAggregateForPeriod aggregateForPeriod(final TurnoverAggregateForPeriod turnoverAggregateForPeriod, final Occupancy occupancy, final LocalDate date, final Type type, final Frequency frequency){
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

        final List<Turnover> turnoversToAggregate = turnoversToAggregate(occupancy, date, turnoverAggregateForPeriod.getAggregationPeriod(), type, frequency, false);
        final BigDecimal totalGross = turnoversToAggregate.stream()
                .filter(t->t.getGrossAmount()!=null)
                .map(t -> t.getGrossAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal totalNet = turnoversToAggregate.stream()
                .filter(t->t.getNetAmount()!=null)
                .map(t -> t.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final List<Turnover> turnoversToAggregatePrevYear = turnoversToAggregate(occupancy, date, turnoverAggregateForPeriod.getAggregationPeriod(), type, frequency, true);
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

    List<Turnover> turnoversToAggregate(final Occupancy occupancy, final LocalDate date, final AggregationPeriod aggregationPeriod, final Type type, final Frequency frequency, boolean prevYear){
        final Lease lease = occupancy.getLease();
        final Unit unit = occupancy.getUnit();
        final LocalDate periodEndDate = prevYear ? date.minusYears(1) : date;
        final LocalDate periodStartDate = aggregationPeriod.periodStartDateFor(periodEndDate);

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

    @Inject TurnoverRepository turnoverRepository;

}
