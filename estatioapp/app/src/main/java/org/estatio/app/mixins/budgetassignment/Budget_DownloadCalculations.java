package org.estatio.app.mixins.budgetassignment;

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
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgetassignment.viewmodels.CalculationResultViewModel;
import org.estatio.budget.dom.budget.Budget;

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
        WorksheetSpec spec = new WorksheetSpec(CalculationResultViewModel.class, "values");
        WorksheetContent worksheetContent = new WorksheetContent(budgetAssignmentService.getCalculationResults(budget), spec);
        return excelService.toExcelPivot(worksheetContent, fileName);
    }

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private ExcelService excelService;

}
