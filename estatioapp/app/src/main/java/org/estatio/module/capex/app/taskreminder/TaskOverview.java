package org.estatio.module.capex.app.taskreminder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.party.dom.Person;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.app.taskreminder.TaskOverview"
)
@NoArgsConstructor
public class TaskOverview implements HasAtPath {

    public TaskOverview(
            final Person person) {
        this.person = person;
    }

    public String title() {
        return "Task Overview";
    }

    @Getter @Setter
    private Person person;

    @Inject
    ApplicationTenancyEvaluator evaluator;

    @Inject
    MeService meService;

    @Property
    public long getTasksOverdue() {
        final Stream<Task> incompleteTasks = streamIncompleteTasksVisibleToMeAssignedTo(person);
        return incompleteTasks
                .map(Task::getCreatedOn)
                .filter(ld -> ld.plusDays(5).isBefore(clockService.nowAsLocalDateTime()))
                .count();
    }

    @Property
    public long getTasksNotYetOverdue() {
        final Stream<Task> incompleteTasks = streamIncompleteTasksVisibleToMeAssignedTo(person);
        return incompleteTasks.count() - getTasksOverdue();
    }

    private Stream<Task> streamIncompleteTasksVisibleToMeAssignedTo(final Person person) {
        final List<Task> tasks = taskRepository.findIncompleteByPersonAssignedTo(person);
        return tasks.stream()
                .filter(this::visibleToMe); // since just counting
    }

    /**
     * To filter out any tasks that this user doesn't have access to.
     *
     * We wouldn't need to do this if just rendering in a table, but it is necessary when
     * just counting the tasks.
     */
    private boolean visibleToMe(final Task task) {
        final ApplicationUser meAsApplicationUser = meService.me();
        return evaluator.hides(task, meAsApplicationUser) == null;
    }

    @Collection
    @CollectionLayout(named = "Tasks Not Yet Overdue")
    public List<Task> getListOfTasksNotYetOverdue() {
        return taskRepository.findIncompleteByPersonAssignedTo(person).stream()
                .filter(t -> t.getCreatedOn().plusDays(5).isAfter(clockService.nowAsLocalDateTime()))
                .collect(Collectors.toList());
    }

    @Collection
    @CollectionLayout(named = "Tasks Overdue")
    public List<Task> getListOfTasksOverdue() {
        return taskRepository.findIncompleteByPersonAssignedTo(person).stream()
                .filter(t -> t.getCreatedOn().plusDays(5).isBefore(clockService.nowAsLocalDateTime()))
                .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public TaskOverview sendReminder() {
        taskReminderService.sendReminder(person, getListOfTasksOverdue());
        return this;
    }

    public String disableSendReminder() {
        return taskReminderService.disableSendReminder(person, getListOfTasksOverdue());
    }

    @Override
    @Programmatic
    public String getAtPath() {
        return person.getAtPath();
    }

    @Inject
    private ClockService clockService;

    @Inject
    private TaskReminderService taskReminderService;

    @Inject
    private TaskRepository taskRepository;
}

