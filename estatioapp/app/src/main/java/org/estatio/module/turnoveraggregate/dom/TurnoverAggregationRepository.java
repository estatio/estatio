package org.estatio.module.turnoveraggregate.dom;

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

    public TurnoverAggregation create(final Occupancy occupancy, final LocalDate date, final Type type, final Frequency frequency, final
    Currency currency) {
        final TurnoverAggregation turnoverAggregation = new TurnoverAggregation(occupancy, date, type, frequency, currency);
        serviceRegistry.injectServicesInto(turnoverAggregation);
        repositoryService.persistAndFlush(turnoverAggregation);
        return turnoverAggregation;
    }


    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry;

}
