package org.estatio.capex.dom.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

/**
 * TODO: inline this mixin.
 */
@Mixin(method = "act")
public class Task_assignTo {

    protected final Task task;

    public Task_assignTo(final Task task) {
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Task act(@Nullable final Person person) {
        task.setPersonAssignedTo(person);
        return task;
    }

    public List<Person> choices0Act() {
        final List<Person> personsInEstatio = personRepository.findWithUsername();
        return personsInEstatio.stream()
                .filter(person -> person.hasPartyRoleType(task.getAssignedTo()))
                .collect(Collectors.toList());
    }

    public Person default0Act() {
        return task.getPersonAssignedTo();
    }

    public String disableAct() {
        return task.isCompleted() ? "Task has already been completed" : null;
    }

    @Inject
    PersonRepository personRepository;


}
