package org.estatio.capex.dom.task;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.roles.EstatioRole;

/***
 * There is no "create" method here because tasks are only ever created in the context of state transitions.
 */
@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Task.class
)
public class TaskRepository {

    @Programmatic
    public java.util.List<Task> listAll() {
        return repositoryService.allInstances(Task.class);
    }

    @Programmatic
    public List<Task> findMyTasksIncomplete() {
        // REVIEW: this is rather naive query, but will do for prototyping at least
        // REVIEW: should also figure out sorting, eg by role/date asc or maybe by date/role
        List<Task> results = Lists.newArrayList();
        final EstatioRole[] estatioRoles = EstatioRole.values();
        for (EstatioRole estatioRole : estatioRoles) {
            final List<Task> tasksForRole = findByAssignedToIncomplete(estatioRole);
            results.addAll(tasksForRole);
        }
        Collections.sort(results, Ordering.natural().nullsFirst().onResultOf(Task::getCreatedOn));
        return results;
    }



    @Programmatic
    public List<Task> findByAssignedToIncomplete(final EstatioRole assignedTo) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findByAssignedToIncomplete",
                        "assignedTo", assignedTo));
    }

    @Inject
    RepositoryService repositoryService;

}
