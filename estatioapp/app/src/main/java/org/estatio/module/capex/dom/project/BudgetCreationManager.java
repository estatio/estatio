package org.estatio.module.capex.dom.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.capex.imports.ProjectImport;
import org.estatio.module.capex.imports.ProjectImportManager;
import org.estatio.module.capex.imports.ProjectItemTermImport;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.dom.project.BudgetCreationManager"
)
public class BudgetCreationManager {

    public BudgetCreationManager(){}

    public BudgetCreationManager(final Project project){
        this.project = project;
    }

    @Getter @Setter
    private Project project;

    public List<BudgetLine> getBudgetLines(){

        // find or create budget and budget items
        final ProjectBudget budget = projectBudgetRepository.findOrCreate(getProject(), 1);
        Lists.newArrayList(project.getItems()).forEach(i->{
            budget.findOrCreateBudgetItem(i);
        });
        List<BudgetLine> result = new ArrayList<>();
        Lists.newArrayList(budget.getItems()).forEach(bi->{
            result.add(new BudgetLine(bi));
        });
        return result;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download(final String filename){
        WorksheetSpec budgetLineSpec = new WorksheetSpec(BudgetLine.class, "budgetLines");
        WorksheetContent budgetLineContent = new WorksheetContent(getBudgetLines(), budgetLineSpec);
        return excelService.toExcel(Arrays.asList(budgetLineContent), filename);
    }

    public String default0Download(){
        return "Budget " + getProject().getReference() + " " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public BudgetCreationManager upload(final Blob spreadSheet){
        excelService.fromExcel(spreadSheet, BudgetLine.class, "budgetLines", Mode.RELAXED).forEach(imp->imp.importData(getProject()));
        return new BudgetCreationManager(getProject());
    }

    @Inject
    ExcelService excelService;

    @Inject
    ProjectBudgetRepository projectBudgetRepository;

    @Inject
    ClockService clockService;
}
