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

    public java.util.List<TurnoverAggregateToDate> listAll() {
        return repositoryService.allInstances(TurnoverAggregateToDate.class);
    }

    public TurnoverAggregateToDate create() {
        final TurnoverAggregateToDate aggregate = new TurnoverAggregateToDate();
        serviceRegistry.injectServicesInto(aggregate);
        repositoryService.persistAndFlush(aggregate);
        return aggregate;
    }

    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry;
}
