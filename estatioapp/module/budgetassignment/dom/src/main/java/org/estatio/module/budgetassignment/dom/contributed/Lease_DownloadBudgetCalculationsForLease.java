package org.estatio.module.budgetassignment.dom.contributed;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.budgetassignment.dom.BudgetAssignmentService;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.module.budgetassignment.dom.contributed.results.DetailedCalculationResultViewmodel;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budget.BudgetRepository;
import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.lease.dom.Lease;

/**
 * This cannot be inlined because Lease doesn't know about BudgetCalculationRunRepository.
 */
@Mixin
public class Lease_DownloadBudgetCalculationsForLease {

    private final Lease lease;
    public Lease_DownloadBudgetCalculationsForLease(Lease lease){
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    @MemberOrder(name="budgetCalculationRuns", sequence = "1")
    public Blob downloadBudgetCalculationsForLease(Budget budget, BudgetCalculationType type) {
        final String fileName =  lease.getReference() + " - budget details" + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(DetailedCalculationResultViewmodel.class, "values for lease");
        WorksheetContent worksheetContent = new WorksheetContent(budgetAssignmentService.getDetailedCalculationResults(lease, budget, type), spec);
        return excelService.toExcelPivot(worksheetContent, fileName);
    }

    public List<Budget> choices0DownloadBudgetCalculationsForLease(final Budget budget){
        return budgetRepository.findByProperty(lease.getProperty());
    }

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private ExcelService excelService;

}
