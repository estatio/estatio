package org.estatio.capex.dom.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

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

    @Inject
    TaskRepository taskRepository;
}
