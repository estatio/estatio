package org.incode.platform.dom.alias.integtests.demo.dom.demo;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DemoObject.class
)
public class DemoObjectRepository {

    public List<DemoObject> listAll() {
        return repositoryService.allInstances(DemoObject.class);
    }

    public List<DemoObject> findByName(final String name) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        DemoObject.class,
                        "findByName",
                        "name", name));
    }

    public DemoObject create(final String name) {
        final DemoObject object = new DemoObject(name);
        serviceRegistry.injectServicesInto(object);
        repositoryService.persist(object);
        return object;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;
}
