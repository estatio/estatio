package org.estatio.module.capex.app.project;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.project.Project;

@Mixin
public class Project_moveOrderItems {

    private final Project project;

    public Project_moveOrderItems(Project project) {
        this.project = project;
    }

    @Action()
    public ProjectOrderItemTransferManager $$(final Project target) {
        return new ProjectOrderItemTransferManager(target, project);
    }

    public List<Project> choices0$$(){
        return project.getParent().getChildren().stream().filter(x->!x.equals(project)).collect(Collectors.toList());
    }

    public String validate$$(final Project target){
        return target.equals(project) ? "Moves to same project" : null;
    }

    public String disable$$(){
        if (project.isParentProject()) return "This is a parent project";
        if (project.getParent()==null) return "This project has no parent; moving allowed only to projects with the same parent";
        if (project.getParent().getChildren().size()==1) return "No other child projects found on parent; moving allowed only to projects with the same parent";
        return null;
    }

}
