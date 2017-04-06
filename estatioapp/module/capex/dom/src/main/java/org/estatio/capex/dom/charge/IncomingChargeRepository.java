package org.estatio.capex.dom.charge;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingCharge.class
)
public class IncomingChargeRepository {

    @Programmatic
    public java.util.List<IncomingCharge> listAll() {
        return repositoryService.allInstances(IncomingCharge.class);
    }

    @Programmatic
    public IncomingCharge findByName(
            final String name
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        IncomingCharge.class,
                        "findByName",
                        "name", name));
    }

    @Programmatic
    public List<IncomingCharge> findByNameContains(
            final String name
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingCharge.class,
                        "findByNameContains",
                        "name", name));
    }

    @Programmatic
    public IncomingCharge create(final String name, final IncomingCharge parent, final String atPath) {
        final IncomingCharge incomingCharge = new IncomingCharge(name, parent, atPath);
        serviceRegistry2.injectServicesInto(incomingCharge);
        repositoryService.persist(incomingCharge);
        return incomingCharge;
    }

    @Programmatic
    public IncomingCharge findOrCreate(final String name, final IncomingCharge parent, final String atPath) {
        IncomingCharge incomingCharge = findByName(name);
        if (incomingCharge == null) {
            incomingCharge = create(name, parent, atPath);
        }
        return incomingCharge;
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
