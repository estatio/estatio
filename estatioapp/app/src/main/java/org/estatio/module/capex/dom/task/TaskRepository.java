package org.estatio.module.capex.dom.task;

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

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;

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
    private List<Task> findIncompleteForMeOnly() {
        final Person meAsPerson = meAsPerson();
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
        final Person meAsPerson = meAsPerson();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }

        final List<Task> tasks = findIncompleteForMeOnly();
        final List<Task> myRolesTasksUnassigned = findIncompleteForMyRolesAndUnassigned();
        tasks.addAll(myRolesTasksUnassigned);

        return tasks;
    }

    /**
     * Incomplete, assigned explicitly to me, AND ALSO any tasks not assigned to anyone but for which
     * I have the (party) roles to perform them (so should be part of "my tasks") after {@param createdOn}
     * @param createdOn
     * @return
     */
    @Programmatic
    public List<Task> findIncompleteForMeAndCreatedOnAfter(final LocalDateTime createdOn){
        final Person meAsPerson = meAsPerson();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }

        final List<Task> tasks = findIncompleteForAndCreatedOnAfter(meAsPerson, createdOn);
        final List<Task> myRolesTasksUnassigned = findIncompleteForMyRolesAndUnassignedAndCreatedOnAfter(createdOn);
        tasks.addAll(myRolesTasksUnassigned);
        tasks.sort(Ordering.natural().nullsLast().onResultOf(Task::getCreatedOn));
        return tasks;
    }

    /**
     * Incomplete, assigned explicitly to me, AND ALSO any tasks not assigned to anyone but for which
     * I have the (party) roles to perform them (so should be part of "my tasks") before {@param createdOn}
     * @param createdOn
     * @return
     */
    @Programmatic
    public List<Task> findIncompleteForMeAndCreatedOnBefore(final LocalDateTime createdOn){
        final Person meAsPerson = meAsPerson();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }

        final List<Task> tasks = findIncompleteForAndCreatedOnBefore(meAsPerson, createdOn);
        final List<Task> myRolesTasksUnassigned = findIncompleteForMyRolesAndUnassignedAndCreatedOnBefore(createdOn);
        tasks.addAll(myRolesTasksUnassigned);
        tasks.sort(Ordering.natural().nullsLast().reverse().onResultOf(Task::getCreatedOn));
        return tasks;
    }

    /**
     * Those tasks which are assigned to no-one, but for which I have the (party) roles to perform.
     */
    @Programmatic
    public List<Task> findIncompleteForMyRolesAndUnassigned() {
        final Person meAsPerson = meAsPerson();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }
        final List<PartyRoleType> myRoleTypes = partyRoleTypesFor(meAsPerson);

        return findIncompleteByUnassignedForRoles(myRoleTypes);
    }

    /**
     * Those tasks which are assigned to no-one, but for which I have the (party) roles to perform after {@param createdOn}
     * @param createdOn
     * @return
     */
    @Programmatic
    public List<Task> findIncompleteForMyRolesAndUnassignedAndCreatedOnAfter(final LocalDateTime createdOn) {
        final Person meAsPerson = meAsPerson();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }
        final List<PartyRoleType> myRoleTypes = partyRoleTypesFor(meAsPerson);

        return findIncompleteByUnassignedForRolesAndCreatedOnAfter(myRoleTypes, createdOn);
    }

    /**
     * Those tasks which are assigned to no-one, but for which I have the (party) roles to perform before {@param createdOn}
     * @param createdOn
     * @return
     */
    @Programmatic
    public List<Task> findIncompleteForMyRolesAndUnassignedAndCreatedOnBefore(final LocalDateTime createdOn) {
        final Person meAsPerson = meAsPerson();
        if(meAsPerson == null) {
            return Lists.newArrayList();
        }
        final List<PartyRoleType> myRoleTypes = partyRoleTypesFor(meAsPerson);

        return findIncompleteByUnassignedForRolesAndCreatedOnBefore(myRoleTypes, createdOn);
    }


    @Programmatic
    public List<Task> findIncompleteByRole(final PartyRoleType roleType) {
        return queryResultsCache.execute(() -> doFindIncompleteByRole(roleType),
                getClass(),
                "findIncompleteByRole",
                roleType);
    }

    private List<Task> doFindIncompleteByRole(final PartyRoleType roleType) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByRole",
                        "roleType", roleType));
    }


    @Programmatic
    public List<Task> findIncompleteByUnassignedForRolesAndCreatedOnAfter(final List<PartyRoleType> roleTypes, final LocalDateTime createdOn) {
        return queryResultsCache.execute(() -> doFindIncompleteByUnassignedForRolesAndCreatedOnAfter(roleTypes, createdOn),
                getClass(),
                "findIncompleteByUnassignedForRolesAndCreatedOnAfter",
                roleTypes, createdOn);
    }

    private List<Task> doFindIncompleteByUnassignedForRolesAndCreatedOnAfter(final List<PartyRoleType> roleTypes, final LocalDateTime createdOn) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByUnassignedForRolesAndCreatedOnAfter",
                        "roleTypes", roleTypes,
                        "createdOn", createdOn));
    }

    @Programmatic
    public List<Task> findIncompleteByUnassignedForRolesAndCreatedOnBefore(final List<PartyRoleType> roleTypes, final LocalDateTime createdOn) {
        return queryResultsCache.execute(() -> doFindIncompleteByUnassignedForRolesAndCreatedOnBefore(roleTypes, createdOn),
                getClass(),
                "findIncompleteByUnassignedForRolesAndCreatedOnBefore",
                roleTypes, createdOn);
    }

    private List<Task> doFindIncompleteByUnassignedForRolesAndCreatedOnBefore(final List<PartyRoleType> roleTypes, final LocalDateTime createdOn) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByUnassignedForRolesAndCreatedOnBefore",
                        "roleTypes", roleTypes,
                        "createdOn", createdOn));
    }

    @Programmatic
    public List<Task> findIncompleteByUnassignedForRoles(final List<PartyRoleType> roleTypes) {
        return queryResultsCache.execute(() -> doFindIncompleteByUnassignedForRoles(roleTypes),
                getClass(),
                "findIncompleteByUnassignedForRoles",
                roleTypes);
    }

    private List<Task> doFindIncompleteByUnassignedForRoles(final List<PartyRoleType> roleTypes) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByUnassignedForRoles",
                        "roleTypes", roleTypes));
    }

    @Programmatic
    public List<Task> findIncompleteByUnassigned() {
        return queryResultsCache.execute(() -> doFindIncompleteByUnassigned(),
                getClass(),
                "findIncompleteByUnassigned");
    }

    private List<Task> doFindIncompleteByUnassigned() {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Task.class,
                        "findIncompleteByUnassigned"));
    }


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

    private List<Task> findIncompleteForAndCreatedOnBefore(
            final Person personAssignedTo,
            final LocalDateTime createdOn) {
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

    private List<Task> findIncompleteForAndCreatedOnAfter(
            final Person personAssignedTo,
            final LocalDateTime createdOn) {
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

        final LocalDateTime previousCreatedOn = previousTask.getCreatedOn();
        final Optional<Task> firstTaskIfAny =
                findIncompleteForMeAndCreatedOnBefore(previousCreatedOn)
                        .stream()
                        .findFirst();

        return firstTaskIfAny.orElse(previousTask);
    }

    @Programmatic
    public Task nextTaskAfter(final Task previousTask) {

        final LocalDateTime previousCreatedOn = previousTask.getCreatedOn();
        final Optional<Task> firstTaskIfAny =
                findIncompleteForMeAndCreatedOnAfter(previousCreatedOn)
                        .stream()
                        .findFirst();

        return firstTaskIfAny.orElse(previousTask);
    }

    private Person meAsPerson() {
        return queryResultsCache.execute(this::doMeAsPerson, getClass(), "meAsPerson");
    }

    private Person doMeAsPerson() {
        return personRepository.me();
    }

    private List<PartyRoleType> partyRoleTypesFor(final Person person) {
        return queryResultsCache.execute(() -> doPartyRoleTypesFor(person), getClass(), "partyRoleTypesFor", person);
    }

    private List<PartyRoleType> doPartyRoleTypesFor(final Person person) {
        if(person == null) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(person.getRoles()).stream()
                .map(PartyRole::getRoleType)
                .collect(Collectors.toList());
    }


    @Inject
    RepositoryService repositoryService;

    @Inject
    PersonRepository personRepository;

    @Inject
    QueryResultsCache queryResultsCache;

}
