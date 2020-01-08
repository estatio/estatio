package org.estatio.module.turnoveraggregate.dom;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Type;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = TurnoverAggregation.class)
public class TurnoverAggregationRepository {

    public java.util.List<TurnoverAggregation> listAll() {
        return repositoryService.allInstances(TurnoverAggregation.class);
    }

    public TurnoverAggregation findUnique(
            final Occupancy occupancy,
            final LocalDate date,
            final Type type,
            final Frequency frequency
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TurnoverAggregation.class,
                        "findUnique",
                        "occupancy", occupancy,
                        "date", date,
                        "type", type,
                        "frequency", frequency));
    }

    public TurnoverAggregation findOrCreate(
            final Occupancy occupancy,
            final LocalDate date,
            final Type type,
            final Frequency frequency,
            final Currency currency
    ) {
        TurnoverAggregation turnoverAggregation = findUnique(occupancy, date, type, frequency);
        if (turnoverAggregation == null) {
            turnoverAggregation = create(occupancy, date, type, frequency, currency);
        }
        return turnoverAggregation;
    }

    public List<TurnoverAggregation> findByOccupancyAndTypeAndFrequencyOnOrBeforeDate(final Occupancy occupancy, final Type type, final Frequency frequency, final LocalDate date) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TurnoverAggregation.class,
                        "findByOccupancyAndTypeAndFrequencyOnOrBeforeDate",
                        "occupancy", occupancy,
                        "type", type,
                        "frequency", frequency,
                        "date", date
                ));
    }

    public TurnoverAggregation create(final Occupancy occupancy, final LocalDate date, final Type type, final Frequency frequency, final
    Currency currency) {
        final TurnoverAggregation turnoverAggregation = new TurnoverAggregation(occupancy, date, type, frequency, currency);
        initialize(turnoverAggregation);
        serviceRegistry.injectServicesInto(turnoverAggregation);
        repositoryService.persistAndFlush(turnoverAggregation);
        return turnoverAggregation;
    }

    private void initialize(final TurnoverAggregation turnoverAggregation){
        if (turnoverAggregation.getAggregate1Month()==null) turnoverAggregation.setAggregate1Month(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_1M));
        if (turnoverAggregation.getAggregate2Month()==null) turnoverAggregation.setAggregate2Month(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_2M));
        if (turnoverAggregation.getAggregate3Month()==null) turnoverAggregation.setAggregate3Month(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_3M));
        if (turnoverAggregation.getAggregate6Month()==null) turnoverAggregation.setAggregate6Month(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_6M));
        if (turnoverAggregation.getAggregate9Month()==null) turnoverAggregation.setAggregate9Month(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_9M));
        if (turnoverAggregation.getAggregate12Month()==null) turnoverAggregation.setAggregate12Month(turnoverAggregateForPeriodRepository.create(AggregationPeriod.P_12M));
        if (turnoverAggregation.getAggregateToDate()==null) turnoverAggregation.setAggregateToDate(turnoverAggregateToDateRepository.create());
        if (turnoverAggregation.getPurchaseCountAggregate1Month()==null) turnoverAggregation.setPurchaseCountAggregate1Month(purchaseCountAggregateForPeriodRepository.create(AggregationPeriod.P_1M));
        if (turnoverAggregation.getPurchaseCountAggregate3Month()==null) turnoverAggregation.setPurchaseCountAggregate3Month(purchaseCountAggregateForPeriodRepository.create(AggregationPeriod.P_3M));
        if (turnoverAggregation.getPurchaseCountAggregate6Month()==null) turnoverAggregation.setPurchaseCountAggregate6Month(purchaseCountAggregateForPeriodRepository.create(AggregationPeriod.P_6M));
        if (turnoverAggregation.getPurchaseCountAggregate12Month()==null) turnoverAggregation.setPurchaseCountAggregate12Month(purchaseCountAggregateForPeriodRepository.create(AggregationPeriod.P_12M));
    }


    @Inject RepositoryService repositoryService;

    @Inject TurnoverAggregateForPeriodRepository turnoverAggregateForPeriodRepository;

    @Inject TurnoverAggregateToDateRepository turnoverAggregateToDateRepository;

    @Inject PurchaseCountAggregateForPeriodRepository purchaseCountAggregateForPeriodRepository;

    @Inject ServiceRegistry2 serviceRegistry;
}
