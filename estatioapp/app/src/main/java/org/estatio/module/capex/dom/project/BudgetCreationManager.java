package org.estatio.module.capex.dom.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.dom.project.BudgetCreationManager"
)
public class BudgetCreationManager {

    public BudgetCreationManager(){}

    public BudgetCreationManager(final Project project, final int version){
        this.project = project;
        this.version = version;
    }

    public String title(){
        return TitleBuilder.start().withName("Budget creation for ").withParent(getProject()).withName("v.").withName(getVersion()).toString();
    }

    @Getter @Setter
    private Project project;

    @Getter @Setter
    private int version;

    public List<BudgetLineViewmodel> getBudgetLines(){
        ProjectBudget budget = projectBudgetRepository.findOrCreate(getProject(), getVersion());
        List<BudgetLineViewmodel> result = new ArrayList<>();
        Lists.newArrayList(budget.getItems()).forEach(bi->{
            result.add(new BudgetLineViewmodel(bi));
        });
        return result.stream().sorted(Comparator.comparing(BudgetLineViewmodel::getChargeReference)).collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download(final String filename){
        WorksheetSpec budgetLineSpec = new WorksheetSpec(BudgetLineViewmodel.class, "budgetLines");
        WorksheetContent budgetLineContent = new WorksheetContent(getBudgetLines(), budgetLineSpec);
        return excelService.toExcel(Arrays.asList(budgetLineContent), filename);
    }

    public String default0Download(){
        return "Budget " + getProject().getReference() + " v" + getVersion() + " " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public BudgetCreationManager upload(final Blob spreadSheet){
        excelService.fromExcel(spreadSheet, BudgetLineViewmodel.class, "budgetLines", Mode.RELAXED).forEach(imp->imp.importData(getProject()));
        return new BudgetCreationManager(getProject(), getVersion());
    }

    @Inject
    ExcelService excelService;

    @Inject
    ProjectBudgetRepository projectBudgetRepository;

    @Inject
    ProjectBudgetItemRepository projectBudgetItemRepository;

    @Inject
    ClockService clockService;
}
