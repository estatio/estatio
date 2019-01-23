package org.estatio.module.budgetassignment.contributions;

import java.util.ArrayList;
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

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.dom.service.CalculationResultsForLeaseViewmodel;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

/**
 * This cannot be inlined because Lease doesn't know about BudgetCalculationRunRepository.
 */
@Mixin
public class Lease_DownloadBudgetCalculations {

    private final Lease lease;
    public Lease_DownloadBudgetCalculations(Lease lease){
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob downloadBudgetCalculations(Budget budget, BudgetCalculationType type) {
        final String fileName =  lease.getReference() + " - budget details" + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(CalculationResultsForLeaseViewmodel.class, "values for lease");

        List<CalculationResultsForLeaseViewmodel> vmList = new ArrayList<>();
        for (LeaseItem item : lease.findItemsOfType(LeaseItemType.SERVICE_CHARGE)){
            for (LeaseTerm term : item.getTerms()){
                List<BudgetCalculationResult> calcResults = budgetCalculationResultRepository.findByLeaseTermAndBudgetAndType((LeaseTermForServiceCharge) term, budget, type);
                for (BudgetCalculationResult calcResult : calcResults){
                    for (BudgetCalculation calc : calcResult.getBudgetCalculations()){
                        vmList.add(new CalculationResultsForLeaseViewmodel(
                                calc.getUnit(),
                                incomingChargeDescription(calc),
                                calc.getValue(),
                                calc.getPartitionItem().getBudgetedValue(),
                                calc.getInvoiceCharge()
                        ));
                    }
                }
            }
        }


        WorksheetContent worksheetContent = new WorksheetContent(vmList, spec);
        return excelService.toExcelPivot(worksheetContent, fileName);
    }

    public List<Budget> choices0DownloadBudgetCalculations(final Budget budget){
        return budgetRepository.findByProperty(lease.getProperty());
    }

    private String incomingChargeDescription(final BudgetCalculation calculation){
        StringBuilder builder = new StringBuilder();
        builder.append(calculation.getIncomingCharge().getName());
        final String calculationDescription = calculation.getPartitionItem().getBudgetItem().getCalculationDescription();
        if (calculationDescription !=null){
            builder.append(" ");
            builder.append(calculationDescription);
        }
        return builder.toString();
    }

    @Inject
    private BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private ExcelService excelService;

}
