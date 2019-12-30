package org.estatio.module.turnoveraggregate.dom;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = TurnoverAggregateToDate.class
)
public class TurnoverAggregateToDateRepository {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public java.util.List<TurnoverAggregateToDate> listAll() {
        return repositoryService.allInstances(TurnoverAggregateToDate.class);
    }

    public TurnoverAggregateToDate findUnique(
            final TurnoverAggregation aggregation
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TurnoverAggregateToDate.class,
                        "findUnique",
                        "aggregation", aggregation));
    }


    public TurnoverAggregateToDate create(final TurnoverAggregation aggregation) {
        final TurnoverAggregateToDate aggregate = new TurnoverAggregateToDate();
        aggregate.setAggregation(aggregation);
        serviceRegistry.injectServicesInto(aggregate);
        repositoryService.persistAndFlush(aggregate);
        return aggregate;
    }

    public TurnoverAggregateToDate findOrCreate(
            final TurnoverAggregation aggregation
    ) {
        TurnoverAggregateToDate aggregate = findUnique(aggregation);
        if (aggregate == null) {
            aggregate = create(aggregation);
        }
        return aggregate;
    }

    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry;
}
