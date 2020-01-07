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
        repositoryFor = TurnoverAggregateForPeriod.class
)
public class TurnoverAggregateForPeriodRepository {

    public java.util.List<TurnoverAggregateForPeriod> listAll() {
        return repositoryService.allInstances(TurnoverAggregateForPeriod.class);
    }

    public TurnoverAggregateForPeriod create(final AggregationPeriod period) {
        final TurnoverAggregateForPeriod aggregate = new TurnoverAggregateForPeriod();
        aggregate.setAggregationPeriod(period);
        serviceRegistry.injectServicesInto(aggregate);
        repositoryService.persistAndFlush(aggregate);
        return aggregate;
    }

    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry;
}
