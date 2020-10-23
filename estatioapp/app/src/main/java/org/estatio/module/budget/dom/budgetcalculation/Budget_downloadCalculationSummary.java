package org.estatio.module.budget.dom.budgetcalculation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

@Mixin
public class Budget_downloadCalculationSummary {

    private final Budget budget;

    public Budget_downloadCalculationSummary(Budget budget) {
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob $$(final String filename, final BudgetCalculationType budgetCalculationType, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {
        final List<InMemBudgetCalculation> calculations = budgetCalculationService
                .calculateInMem(budget, budgetCalculationType, calculationStartDate, calculationEndDate);
        List<CalculationVM> vmList = new ArrayList<>();
        for (InMemBudgetCalculation calculation : calculations){
            final Occupancy occupancyIfAny = occupancyRepository
                    .occupanciesByUnitAndInterval(calculation.getUnit(), calculation.getBudget().getInterval()).stream()
                    .findFirst().orElse(null);
            String leaseReference = occupancyIfAny!=null ? occupancyIfAny.getLease().getReference() : null;
            vmList.add(
                    new CalculationVM(
                        calculation.getUnit().getReference(),
                        leaseReference,
                        calculation.getTableItem().getPartitioningTable().getName(),
                        calculation.getValue())
            );
        }
        final String fileNameToUse = withExtension(filename, ".xlsx");
        WorksheetSpec spec = new WorksheetSpec(CalculationVM.class, "summaryPerUnit");
        WorksheetContent worksheetContent = new WorksheetContent(vmList.stream().sorted(Comparator.comparing(CalculationVM::getUnitReference)).collect(
                Collectors.toList()), spec);
        return excelService.toExcelPivot(worksheetContent, fileNameToUse);
    }

    public BudgetCalculationType default1$$(){
        return BudgetCalculationType.BUDGETED;
    }

    public LocalDate default2$$(){
        return budget.getStartDate();
    }

    public LocalDate default3$$(){
        return budget.getEndDate();
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }

    @Inject ExcelService excelService;

    @Inject OccupancyRepository occupancyRepository;

    @Inject BudgetCalculationService budgetCalculationService;

}
