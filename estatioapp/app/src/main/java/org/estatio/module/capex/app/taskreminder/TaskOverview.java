package org.estatio.module.capex.app.taskreminder;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

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
public class TaskOverview {

    public TaskOverview(
            final Person person) {
        this.person = person;
    }

    public String title() {
        return "Task Overview";
    }

    @Getter @Setter
    private Person person;

    @Property
    public long getTasksOverdue() {
        return taskRepository.findIncompleteByPersonAssignedTo(person).stream()
                .map(Task::getCreatedOn)
                .filter(ld -> ld.plusDays(5).isBefore(clockService.nowAsLocalDateTime()))
                .count();
    }

    @Property
    public long getTasksNotYetOverdue() {
        return taskRepository.findIncompleteByPersonAssignedTo(person).size() - getTasksOverdue();
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

    @Inject
    private ClockService clockService;

    @Inject
    private TaskReminderService taskReminderService;

    @Inject
    private TaskRepository taskRepository;

}

