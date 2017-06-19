package org.estatio.capex.dom.documents.categorisation.tasks;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;

@Mixin(method = "act")
public class Task_assignTo {

    protected final Task task;

    public Task_assignTo(final Task task) {
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Task act(final Person person) {
        task.setPersonAssignedTo(person);
        return task;
    }

    public List<Person> choices0Act() {
        return personRepository.findWithUsername();
    }

    public Person default0Act() {
        return task.getPersonAssignedTo();
    }

    @Inject
    PersonRepository personRepository;


}
