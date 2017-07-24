package org.estatio.capex.dom.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "task.TaskMenu"
    )
@DomainServiceLayout(named = "Tasks")
public class TaskMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<Task> allTasks(){
        return taskRepository.listAll();
    }

    public List<Task> myTasks(){
        return taskRepository.findIncompleteForMe();
    }

    public List<Task> findTasksFor(final Person person){
        return taskRepository.findIncompleteByPersonAssignedTo(person);
    }

    public List<Person> choices0FindTasksFor(){
        return personRepository.findWithUsername();
    }

    @Inject
    TaskRepository taskRepository;

    @Inject
    PersonRepository personRepository;
}
