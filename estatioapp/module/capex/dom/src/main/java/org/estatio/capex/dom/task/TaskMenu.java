package org.estatio.capex.dom.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "task.TaskMenu"
    )
@DomainServiceLayout(
        named = "Tasks & Docs",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "75.2"
)
public class TaskMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<Task> allTasks(){
        return taskRepository.listAll();
    }

    @MemberOrder(sequence = "1")
    public List<Task> myTasks(){
        return taskRepository.findIncompleteForMe();
    }


    @MemberOrder(sequence = "2")
    public List<Task> findTasksForPerson(final Person person){
        return taskRepository.findIncompleteByPersonAssignedTo(person);
    }

    public List<Person> choices0FindTasksForPerson(){
        return personRepository.findWithUsername();
    }


    @MemberOrder(sequence = "3")
    public List<Task> findTasksForRole(final PartyRoleType partyRoleType){
        return taskRepository.findIncompleteByRole(partyRoleType);
    }
    public List<PartyRoleType> choices0FindTasksForRole(){
        return partyRoleTypeRepository.listAll();
    }


    @MemberOrder(sequence = "4")
    public List<Task> findTasksUnassignedToPerson(){
        return taskRepository.findIncompleteByUnassigned();
    }


    @Inject
    TaskRepository taskRepository;

    @Inject
    PersonRepository personRepository;

    @Inject PartyRoleTypeRepository partyRoleTypeRepository;

}
