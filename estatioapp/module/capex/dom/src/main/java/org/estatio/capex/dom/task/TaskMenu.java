package org.estatio.capex.dom.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.role.PartyRoleType;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "task.TaskMenu"
    )
@DomainServiceLayout(named = "Tasks")
public class TaskMenu {

    public List<Task> allTasks(){
        return taskRepository.listAll();
    }

    public List<Task> findTasksAssignedTo(PartyRoleType partyRoleType){
        return taskRepository.findByAssignedToIncomplete(partyRoleType);
    }

    @Inject
    TaskRepository taskRepository;
}
