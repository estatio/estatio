package org.estatio.module.capex.contributions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.imports.ProjectImportManager;

@Mixin
public class Project_importExport {

    private final Project project;

    public Project_importExport(Project project) {
        this.project = project;
    }

    @Action()
    public ProjectImportManager $$() {
        return new ProjectImportManager(null, project);
    }

}
