package org.estatio.capex.dom.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Person;

@Mixin(method = "coll")
public class Person_currentTasks {

    protected final Person person;

    public Person_currentTasks(final Person person) {
        this.person = person;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Task> coll() {
        return taskRepository.findIncompleteByPersonAssignedTo(person);
    }


    @Inject
    TaskRepository taskRepository;


}
