package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Task.class
)
public class TaskRepository {

    @Programmatic
    public java.util.List<Task> listAll() {
        return container.allInstances(Task.class);
    }

    @Inject
    RepositoryService container;
}
