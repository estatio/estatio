package org.estatio.capex.dom.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;

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
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findByIncomplete"));
    }

    @Programmatic
    public List<Task> findTasksIncompleteForMe() {
        final Person meAsPerson = personRepository.me();
        return findTasksIncompleteFor(meAsPerson);
    }

    @Programmatic
    public List<Task> findTasksIncompleteFor(final Person person) {
        // get all tasks.
        final List<Task> tasks = findTasksIncomplete();

        List<Task> myTasks =
                tasks.stream()
                        .filter(x -> x.getPersonAssignedTo() == person)
                        .collect(Collectors.toList());

        return myTasks;
    }

    @Programmatic
    public List<Task> findTasksIncompleteForOthers() {

        final List<Task> tasks = findTasksIncomplete();

        List<Task> myTasks = findTasksIncompleteForMe();
        tasks.removeAll(myTasks);

        return tasks;
    }

    @Programmatic
    public Task nextTaskAfter(final Task previousTask) {

        final Person previousTaskAssignedTo = previousTask.getPersonAssignedTo();
        final List<Task> results = findTasksIncompleteFor(previousTaskAssignedTo);

        results.removeIf(task -> task.getCreatedOn().isBefore(previousTask.getCreatedOn()));
        final List<Task> tasks = results;

        return tasks.size() > 0 ? tasks.get(0) : null;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    PersonRepository personRepository;

}
