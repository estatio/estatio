package org.estatio.capex.dom.task;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.role.PartyRole;
import org.estatio.dom.party.role.PartyRoleType;

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
                        "findIncomplete"));
    }

    /**
     * Incomplete and assigned explicitly to me
     */
    @Programmatic
    public List<Task> findIncompleteForMeOnly() {
        final Person meAsPerson = personRepository.me();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }
        return findIncompleteByPersonAssignedTo(meAsPerson);
    }

    /**
     * Incomplete, assigned explicitly to me, AND ALSO any tasks not assigned to anyone but for which
     * I have the (party) roles to perform them (so should be part of "my tasks")
     */
    @Programmatic
    public List<Task> findIncompleteForMe() {
        final Person meAsPerson = personRepository.me();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }

        final List<Task> tasks = findIncompleteForMeOnly();
        final List<Task> myRolesTasksUnassigned = findIncompleteForMyRolesAndUnassigned();
        tasks.addAll(myRolesTasksUnassigned);

        return tasks;
    }

    /**
     * Those tasks which are assigned to no-one, but for which I have the (party) roles to perform.
     */
    @Programmatic
    public List<Task> findIncompleteForMyRolesAndUnassigned() {
        final Person meAsPerson = personRepository.me();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }
        final List<PartyRoleType> myRoleTypes = partyRoleTypesFor(meAsPerson);

        final List<Task> tasksForNoone = findIncompleteByPersonAssignedTo(null);
        return tasksForNoone.stream()
                .filter(task -> myRoleTypes.contains(task.getAssignedTo()))
                .collect(Collectors.toList());
    }

    /**
     * Those tasks that ARE assigned explicitly, but to someone else, for which I have the (party)
     * roles to perform (ie "my colleague's tasks")
     */
    @Programmatic
    public List<Task> findIncompleteForMyRoles() {
        final Person meAsPerson = personRepository.me();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }
        final List<PartyRoleType> myRoleTypes = partyRoleTypesFor(meAsPerson);

        final List<Task> tasksForOthersOrNoone = findIncompleteForOthers();
        return tasksForOthersOrNoone.stream()
                                    .filter(task -> myRoleTypes.contains(task.getAssignedTo()))
                                    .collect(Collectors.toList());
    }

    private List<PartyRoleType> partyRoleTypesFor(final Person person) {
        return Lists.newArrayList(person.getRoles()).stream()
                .map(PartyRole::getRoleType)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Task> findIncompleteForOthers() {
        final Person meAsPerson = personRepository.me();
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByNotPersonAssignedTo",
                        "personAssignedTo", meAsPerson));
    }

    @Programmatic
    public List<Task> findIncompleteByPersonAssignedTo(final Person personAssignedTo) {
        return queryResultsCache.execute(
                () -> doFindIncompleteByPersonAssignedTo(personAssignedTo),
                getClass(),
                "findIncompleteByPersonAssignedTo", personAssignedTo);
    }

    private List<Task> doFindIncompleteByPersonAssignedTo(final Person personAssignedTo) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByPersonAssignedTo",
                        "personAssignedTo", personAssignedTo));
    }

    @Programmatic
    public List<Task> findIncompleteForAndCreatedOnBefore(final Person personAssignedTo, final LocalDateTime createdOn) {
        return queryResultsCache.execute(
                () -> doFindIncompleteForAndCreatedOnBefore(personAssignedTo, createdOn),
                getClass(),
                "findIncompleteForAndCreatedOnBefore", personAssignedTo, createdOn);
    }

    private List<Task> doFindIncompleteForAndCreatedOnBefore(
            final Person personAssignedTo,
            final LocalDateTime createdOn) {
        final List<Task> tasks = repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByPersonAssignedToAndCreatedOnBefore",
                        "personAssignedTo", personAssignedTo,
                        "createdOn", createdOn));
        tasks.sort(Ordering.natural().nullsLast().reverse().onResultOf(Task::getCreatedOn));
        return tasks;
    }

    @Programmatic
    public List<Task> findIncompleteForAndCreatedOnAfter(final Person personAssignedTo, final LocalDateTime createdOn) {
        return queryResultsCache.execute(
                () -> doFindIncompleteForAndCreatedOnAfter(personAssignedTo, createdOn),
                getClass(),
                "findIncompleteForAndCreatedOnAfter", personAssignedTo, createdOn);
    }

    private List<Task> doFindIncompleteForAndCreatedOnAfter(
            final Person personAssignedTo,
            final LocalDateTime createdOn) {
        final List<Task> tasks = repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByPersonAssignedToAndCreatedOnAfter",
                        "personAssignedTo", personAssignedTo,
                        "createdOn", createdOn));
        tasks.sort(Ordering.natural().nullsLast().onResultOf(Task::getCreatedOn));
        return tasks;
    }

    @Programmatic
    public Task previousTaskBefore(final Task previousTask) {

        final Person previousAssignedTo = previousTask.getPersonAssignedTo();
        final LocalDateTime previousCreatedOn = previousTask.getCreatedOn();
        final List<Task> tasksIncompleteForAndCreatedOnBefore = findIncompleteForAndCreatedOnBefore(
                previousAssignedTo, previousCreatedOn);
        final Optional<Task> firstTaskIfAny =
                tasksIncompleteForAndCreatedOnBefore
                        .stream()
                        .findFirst();

        return firstTaskIfAny.orElse(previousTask);
    }

    @Programmatic
    public Task nextTaskAfter(final Task previousTask) {

        final Person previousAssignedTo = previousTask.getPersonAssignedTo();
        final LocalDateTime previousCreatedOn = previousTask.getCreatedOn();
        final Optional<Task> firstTaskIfAny =
                findIncompleteForAndCreatedOnAfter(previousAssignedTo, previousCreatedOn)
                        .stream()
                        .findFirst();

        return firstTaskIfAny.orElse(previousTask);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    PersonRepository personRepository;

    @Inject
    QueryResultsCache queryResultsCache;

}
