package org.estatio.module.capex.imports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.imports.ProjectImportManager"
)
public class ProjectImportManager {

    public ProjectImportManager() {
    }

    public ProjectImportManager(final Country country, final Project project) {
        this.country = country;
        this.project = project;
    }

    public String title(){
        return "Project Import Manager";
    }

    @Getter @Setter
    public Country country;

    @Getter @Setter
    public Project project;

    public List<ProjectImport> getProjectLines(){
        List<ProjectImport> result = new ArrayList<>();
        List<Project> projects = selectProjects();
        projects.forEach(p->{
            if (p.getItems().isEmpty()){
                result.add(whenHavingNoProjectItems(p));
            } else {
                result.addAll(whenHavingProjectItems(p));
            }
        });
        return result;
    }

    private List<ProjectImport> whenHavingProjectItems(final Project p) {
        List<ProjectImport> result = new ArrayList<>();
        Lists.newArrayList(p.getItems()).forEach(pi->
        {
            ProjectImport imp = new ProjectImport();
            imp.setProjectReference(p.getReference());
            imp.setProjectName(p.getName());
            imp.setStartDate(p.getStartDate());
            imp.setEndDate(p.getEndDate());
            imp.setAtPath(p.getAtPath());
            imp.setParentReference(p.getParent()!=null ? p.getParent().getReference() : null);
            imp.setItemWorkTypeReference(pi.getCharge()!=null ? pi.getCharge().getReference() : null);
            imp.setItemDescription(pi.getDescription());
            imp.setItemStartDate(pi.getStartDate());
            imp.setItemEndDate(pi.getEndDate());
            imp.setItemPropertyReference(pi.getProperty()!=null ? pi.getProperty().getReference() : null);
            imp.setItemTaxReference(pi.getTax()!=null ? pi.getTax().getReference() : null);
            result.add(imp);
        });
        return result;
    }

    private ProjectImport whenHavingNoProjectItems(final Project p) {
        ProjectImport imp = new ProjectImport();
        imp.setProjectReference(p.getReference());
        imp.setProjectName(p.getName());
        imp.setStartDate(p.getStartDate());
        imp.setEndDate(p.getEndDate());
        imp.setAtPath(p.getAtPath());
        imp.setParentReference(p.getParent()!=null ? p.getParent().getReference() : null);
        return imp;
    }

    List<Project> selectProjects() {
        if (this.project!=null) return Arrays.asList(project);
        if (this.country!=null) return projectRepository.findUsingAtPath(deriveAtPathFromCountry());
        return Arrays.asList();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download(final String filename){
        WorksheetSpec projectLineSpec = new WorksheetSpec(ProjectImport.class, "projects");
        WorksheetContent projectLineContent = new WorksheetContent(getProjectLines(), projectLineSpec);
        return excelService.toExcel(Arrays.asList(projectLineContent), filename);
    }

    public String default0Download(){
        return "Projects " + getCountry().getReference() + " " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public ProjectImportManager upload(final Blob spreadSheet){
        excelService.fromExcel(spreadSheet, ProjectImport.class, "projects", Mode.RELAXED).forEach(imp->imp.importData(null));
        return new ProjectImportManager(getCountry(), getProject());
    }

    private String deriveAtPathFromCountry(){
        return "/" + country.getReference();
    }

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

}
