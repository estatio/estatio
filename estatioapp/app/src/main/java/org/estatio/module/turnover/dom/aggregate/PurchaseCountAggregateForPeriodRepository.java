package org.estatio.module.turnover.dom.aggregate;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = PurchaseCountAggregateForPeriod.class
)
public class PurchaseCountAggregateForPeriodRepository {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public java.util.List<PurchaseCountAggregateForPeriod> listAll() {
        return repositoryService.allInstances(PurchaseCountAggregateForPeriod.class);
    }

    public PurchaseCountAggregateForPeriod findUnique(
            final TurnoverAggregation aggregation,
            final AggregationPeriod period
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        PurchaseCountAggregateForPeriod.class,
                        "findUnique",
                        "aggregation", aggregation,
                        "period", period));
    }


    public PurchaseCountAggregateForPeriod create(final TurnoverAggregation aggregation, final AggregationPeriod period) {
        final PurchaseCountAggregateForPeriod aggregate = new PurchaseCountAggregateForPeriod();
        aggregate.setAggregation(aggregation);
        aggregate.setAggregationPeriod(period);
        serviceRegistry.injectServicesInto(aggregate);
        repositoryService.persistAndFlush(aggregate);
        return aggregate;
    }

    public PurchaseCountAggregateForPeriod findOrCreate(
            final TurnoverAggregation aggregation, final AggregationPeriod period
    ) {
        PurchaseCountAggregateForPeriod aggregate = findUnique(aggregation, period);
        if (aggregate == null) {
            aggregate = create(aggregation, period);
        }
        return aggregate;
    }

    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry;
}
