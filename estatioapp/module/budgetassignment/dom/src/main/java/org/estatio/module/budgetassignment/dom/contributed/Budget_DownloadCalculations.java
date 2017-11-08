package org.estatio.module.budgetassignment.dom.contributed;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.budgetassignment.dom.BudgetAssignmentService;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.module.budgetassignment.dom.contributed.results.CalculationResultViewModel;
import org.estatio.module.budgeting.dom.budget.Budget;

/**
 * This cannot be inlined (needs to be a mixin) because Budget doesn't know about CalculationResultViewModel
 */
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
