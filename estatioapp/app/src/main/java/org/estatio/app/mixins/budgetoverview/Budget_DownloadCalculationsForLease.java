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
import org.estatio.dom.budgetassignment.viewmodels.DetailedBudgetCalculationResultViewmodel;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

@Mixin
public class Budget_DownloadCalculationsForLease {

    private final Budget budget;
    public Budget_DownloadCalculationsForLease(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob downloadCalculationsForLease(Lease lease) {
        final String fileName =  lease.getReference() + " - budget details" + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(DetailedBudgetCalculationResultViewmodel.class, "values for lease");
        WorksheetContent worksheetContent = new WorksheetContent(budgetAssignmentResultsForLease(lease), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public List<Lease> choices0DownloadCalculationsForLease(final Lease lease){
        return leaseRepository.findLeasesByProperty(budget.getProperty());
    }

    private List<DetailedBudgetCalculationResultViewmodel> budgetAssignmentResultsForLease(final Lease lease){
        return budgetAssignmentService.getDetailedBudgetAssignmentResults(budget, lease);
    }


    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private ExcelService excelService;

}
