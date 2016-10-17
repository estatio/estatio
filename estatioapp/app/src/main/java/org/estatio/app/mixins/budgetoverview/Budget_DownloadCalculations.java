package org.estatio.app.mixins.budgetoverview;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.dom.budgetassignment.BudgetAssignmentService;
import org.estatio.dom.budgetassignment.viewmodels.BudgetAssignmentResult;
import org.estatio.dom.budgeting.budget.Budget;

@Mixin
public class Budget_DownloadCalculations {

    private final Budget budget;
    public Budget_DownloadCalculations(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob downloadCalculations() {
        final String fileName =  budget.title() + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(BudgetAssignmentResult.class, "calculations");
        WorksheetContent worksheetContent = new WorksheetContent(budgetAssignmentResults(), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    private List<BudgetAssignmentResult> budgetAssignmentResults(){
        return budgetAssignmentService.getAssignmentResults(budget);
    }

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private ExcelService excelService;

}
