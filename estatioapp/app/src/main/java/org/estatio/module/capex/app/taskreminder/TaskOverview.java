package org.estatio.module.capex.app.taskreminder;

import java.util.List;
import java.util.function.Predicate;
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

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.module.base.dom.VisibilityEvaluator;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
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

    @Property
    public long getTasksOverdue() {
        return getListOfTasksOverdue().size();
    }

    @Property
    public long getTasksNotYetOverdue() {
        return getListOfTasksNotYetOverdue().size();
    }

    @Collection
    @CollectionLayout(named = "Tasks Not Yet Overdue")
    public List<Task> getListOfTasksNotYetOverdue() {
        return streamIncompleteTasksVisibleToMeAssignedTo(person)
                .filter(task -> {
                    StateTransition stateTransition = stateTransitionService.findFor(task);
                    return stateTransition != null && stateTransition.getDomainObject() != null && !(stateTransition.getDomainObject() instanceof Order);
                })
                .filter(lessThanFiveDaysOld())
                .collect(Collectors.toList());
    }

    @Collection
    @CollectionLayout(named = "Tasks Overdue")
    public List<Task> getListOfTasksOverdue() {
        return streamIncompleteTasksVisibleToMeAssignedTo(person)
                .filter(task -> {
                    StateTransition stateTransition = stateTransitionService.findFor(task);
                    return stateTransition != null && stateTransition.getDomainObject() != null && !(stateTransition.getDomainObject() instanceof Order);
                })
                .filter(moreThanFiveDaysOld())
                .collect(Collectors.toList());
    }

    private Predicate<Task> lessThanFiveDaysOld() {
        return moreThanFiveDaysOld().negate();
    }

    private Predicate<Task> moreThanFiveDaysOld() {
        return t -> t.getCreatedOn().plusDays(5).isBefore(clockService.nowAsLocalDateTime());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public TaskOverview sendReminder() {
        taskReminderService.sendReminder(person, getListOfTasksOverdue());
        return this;
    }

    public String disableSendReminder() {
        return taskReminderService.disableSendReminder(person, getListOfTasksOverdue());
    }

    private Stream<Task> streamIncompleteTasksVisibleToMeAssignedTo(final Person person) {
        final List<Task> tasks = taskRepository.findIncompleteByPersonAssignedTo(person);
        return tasks.stream()
                .filter(visibilityEvaluator::visibleToMe); // since just counting
    }

    @Override
    @Programmatic
    public String getAtPath() {
        return person.getAtPath();
    }

    @Inject
    ClockService clockService;

    @Inject
    TaskReminderService taskReminderService;

    @Inject
    TaskRepository taskRepository;

    @Inject
    VisibilityEvaluator visibilityEvaluator;

    @Inject StateTransitionService stateTransitionService;

}

