package org.estatio.capex.dom.task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRole;
import org.estatio.dom.party.role.PartyRoleRepository;
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

        final List<Task> tasks = Lists.newArrayList();

        for (PartyRoleType partyRoleType : partyRoleTypeRepository.listAll()) {
            appendTasksIncompleteFor(partyRoleType, tasks);
        }

        sort(tasks);
        return tasks;
    }

    @Programmatic
    public List<Task> findTasksIncompleteForMe() {

        // get all tasks.
        final List<Task> tasks = findTasksIncomplete();

        final ApplicationUser me = meService.me();
        final Person meAsPerson = personRepository.findByUsername(me.getUsername());

        List<Task> myTasks =
                tasks.stream()
                        .filter(x -> x.getPersonAssignedTo() == meAsPerson)
                        .collect(Collectors.toList());
        sort(myTasks);

        return myTasks;
    }

    @Programmatic
    public List<Task> findTasksIncompleteForOthers() {

        final List<Task> tasks = findTasksIncomplete();

        List<Task> myTasks = findTasksIncompleteForMe();
        tasks.removeAll(myTasks);

        sort(tasks);
        return tasks;
    }

    /**
     * TODO: this requires EST-1331 to be implemented in order to correctly work (since relies on PartyRole to be correctly populated).
     *
     * @return null if unable to determine current user as a Person
     */
    @Programmatic
    public List<Task> findTasksIncompleteForMeThenMyRoles() {

        final List<Task> result = Lists.newArrayList();

        // get all tasks.
        final List<Task> tasks = findTasksIncomplete();


        // first, pull out my tasks
        final ApplicationUser me = meService.me();
        final Person meAsPerson = personRepository.findByUsername(me.getUsername());

        List<Task> selectedTasks =
                tasks.stream()
                        .filter(x -> x.getPersonAssignedTo() == meAsPerson)
                        .collect(Collectors.toList());
        sortAndMoveSelectedTasksToResult(tasks, selectedTasks, result);

        // then, any tasks assigned to no-one, in one of my roles
        // TODO: this requires EST-1331 to be implemented.
        List<PartyRole> myPartyRoles =  partyRoleRepository.findByParty(meAsPerson);

        for (PartyRole partyRole : myPartyRoles) {
            selectedTasks =
                    tasks.stream()
                            .filter(x -> x.getAssignedTo() == partyRole.getRoleType() && x.getPersonAssignedTo() == null)
                            .collect(Collectors.toList());
            sortAndMoveSelectedTasksToResult(tasks, selectedTasks, result);
        }

        // then, any tasks assigned to someone else, in one of my roles
        for (PartyRole partyRole : myPartyRoles) {
            selectedTasks =
                    tasks.stream()
                            .filter(x -> x.getAssignedTo() == partyRole.getRoleType() && x.getPersonAssignedTo() != null)
                            .collect(Collectors.toList());
            sortAndMoveSelectedTasksToResult(tasks, selectedTasks, result);
        }

        // finally, any remaining tasks
        selectedTasks = Lists.newArrayList(tasks);
        sortAndMoveSelectedTasksToResult(tasks, selectedTasks, result);

        return result;
    }

    private void sortAndMoveSelectedTasksToResult(
            final List<Task> tasks,
            final List<Task> selectedTasks, final List<Task> result) {
        tasks.removeAll(selectedTasks);
        sort(selectedTasks);
        result.addAll(selectedTasks);
    }

    private void sort(final List<Task> tasks) {
        Collections.sort(tasks, Ordering.natural().nullsFirst().onResultOf(Task::getCreatedOn));
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
        sort(tasks);
        return tasks;
    }

    @Programmatic
    private List<Task> appendTasksIncompleteFor(final PartyRoleType partyRoleType, List<Task> results) {
        final List<Task> tasksForRole = findByAssignedToIncomplete(partyRoleType);
        results.addAll(tasksForRole);
        return results;
    }

    @Programmatic
    public List<Task> findTasksIncompleteForMeCreatedOnAfter(final LocalDateTime localDateTime) {
        // REVIEW: this is rather naive, but will do for prototyping at least
        final List<Task> results = findTasksIncompleteForMe();
        results.removeIf(task -> task.getCreatedOn().isBefore(localDateTime));
        return results;
    }

    @Programmatic
    public Task nextTaskForMeAfter(final Task task) {
        final List<Task> tasks = findTasksIncompleteForMeCreatedOnAfter(task.getCreatedOn());
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

    @Inject
    PartyRoleRepository partyRoleRepository;

    @Inject
    MeService meService;

    @Inject
    PersonRepository personRepository;

}
