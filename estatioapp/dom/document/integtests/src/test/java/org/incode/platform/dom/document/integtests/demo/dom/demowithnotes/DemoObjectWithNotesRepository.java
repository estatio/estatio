package org.incode.platform.dom.document.integtests.demo.dom.demowithnotes;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DemoObjectWithNotes.class
)
public class DemoObjectWithNotesRepository {

    public List<DemoObjectWithNotes> listAll() {
        return repositoryService.allInstances(DemoObjectWithNotes.class);
    }

    public List<DemoObjectWithNotes> findByName(final String name) {
        return repositoryService.allMatches(
                new QueryDefault<DemoObjectWithNotes>(
                        DemoObjectWithNotes.class,
                        "findByName",
                        "name", name));
    }

    public DemoObjectWithNotes create(final String name) {
        final DemoObjectWithNotes object = new DemoObjectWithNotes(name, null);
        serviceRegistry.injectServicesInto(object);
        repositoryService.persist(object);
        return object;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;
}
