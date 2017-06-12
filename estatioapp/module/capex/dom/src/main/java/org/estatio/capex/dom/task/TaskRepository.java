package org.estatio.capex.dom.task;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeRepository;

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
    public List<Task> findTasksIncomplete() {
        // REVIEW: this is rather naive query, but will do for prototyping at least
        final List<Task> tasks = Lists.newArrayList();

        for (PartyRoleType partyRoleType : partyRoleTypeRepository.listAll()) {
            appendTasksIncompleteFor(partyRoleType, tasks);
        }

        Collections.sort(tasks, Ordering.natural().nullsFirst().onResultOf(Task::getCreatedOn));
        return tasks;
    }

    @Programmatic
    public List<Task> findTasksIncompleteFor(final IPartyRoleType iPartyRoleType) {
        final PartyRoleType partyRoleType = iPartyRoleType.findUsing(partyRoleTypeRepository);
        return findTasksIncompleteFor(partyRoleType);
    }

    @Programmatic
    public List<Task> findTasksIncompleteFor(final PartyRoleType partyRoleType) {
        List<Task> tasks = Lists.newArrayList();
        appendTasksIncompleteFor(partyRoleType, tasks);
        Collections.sort(tasks, Ordering.natural().nullsFirst().onResultOf(Task::getCreatedOn));
        return tasks;
    }

    @Programmatic
    private List<Task> appendTasksIncompleteFor(final PartyRoleType partyRoleType, List<Task> results) {
        final List<Task> tasksForRole = findByAssignedToIncomplete(partyRoleType);
        results.addAll(tasksForRole);
        return results;
    }

    @Programmatic
    public List<Task> findTasksIncompleteCreatedOnAfter(final LocalDateTime localDateTime) {
        // REVIEW: this is rather naive, but will do for prototyping at least
        final List<Task> results = findTasksIncomplete();
        results.removeIf(task -> task.getCreatedOn().isBefore(localDateTime));
        return results;
    }

    @Programmatic
    public Task nextTaskAfter(final Task task) {
        final List<Task> tasks = findTasksIncompleteCreatedOnAfter(task.getCreatedOn());
        return tasks.size() > 0 ? tasks.get(0) : null;
    }



    @Programmatic
    public List<Task> findByAssignedToIncomplete(final PartyRoleType assignedTo) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findByAssignedToIncomplete",
                        "assignedTo", assignedTo));
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;


}
