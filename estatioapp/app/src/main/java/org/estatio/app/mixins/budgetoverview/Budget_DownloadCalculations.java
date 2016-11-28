package org.estatio.app.mixins.budgetoverview;

import java.math.BigDecimal;
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

import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgetassignment.viewmodels.BudgetCalculationResultViewModel2;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.lease.Occupancy;

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
        WorksheetSpec spec = new WorksheetSpec(BudgetCalculationResultViewModel2.class, "values");
        WorksheetContent worksheetContent = new WorksheetContent(budgetAssignmentResults(), spec);
        return excelService.toExcelPivot(worksheetContent, fileName);
    }

    private List<BudgetCalculationResultViewModel2> budgetAssignmentResults(){
        List<BudgetCalculationResultViewModel2> results = new ArrayList<>();
        for (BudgetCalculationRun run : budgetCalculationRunRepository.findByBudgetAndType(budget, BudgetCalculationType.BUDGETED)){
            for (BudgetCalculationResult result : run.getBudgetCalculationResults()){
                BudgetCalculationResultViewModel2 vm = new BudgetCalculationResultViewModel2(
                        run.getLease(),
                        result.getInvoiceCharge(),
                        run.getType()==BudgetCalculationType.BUDGETED ? result.getValue().add(result.getShortfall()) : BigDecimal.ZERO,
                        run.getType()==BudgetCalculationType.BUDGETED ? result.getValue() :BigDecimal.ZERO ,
                        run.getType()==BudgetCalculationType.BUDGETED ? result.getShortfall() : BigDecimal.ZERO,
                        run.getType()==BudgetCalculationType.ACTUAL ? result.getValue().add(result.getShortfall()) : BigDecimal.ZERO,
                        run.getType()==BudgetCalculationType.ACTUAL ? result.getValue() :BigDecimal.ZERO ,
                        run.getType()==BudgetCalculationType.ACTUAL ? result.getShortfall(): BigDecimal.ZERO
                        );
                String unitString = run.getLease().getOccupancies().first().getUnit().getReference();
                if (run.getLease().getOccupancies().size()>1) {
                    boolean skip = true;
                    for (Occupancy occupancy : run.getLease().getOccupancies()){
                        if (skip){
                            skip = false;
                        } else {
                            unitString = unitString.concat(" | ").concat(occupancy.getUnit().getReference());
                        }
                    }
                }
                vm.setUnit(unitString);
                results.add(vm);
            }
        }
        return results;
    }

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    private ExcelService excelService;

}
