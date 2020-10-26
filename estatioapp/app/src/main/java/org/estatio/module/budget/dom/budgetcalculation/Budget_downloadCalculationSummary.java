package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
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
    public Blob $$(final BudgetCalculationType budgetCalculationType, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {
        final List<InMemBudgetCalculation> calculations = budgetCalculationService
                .calculateInMem(budget, budgetCalculationType, calculationStartDate, calculationEndDate);
        final List<PartitioningTable> distinctTables = calculations.stream().map(c -> c.getTableItem().getPartitioningTable())
                .distinct().collect(Collectors.toList());
        final List<Unit> distinctUnits = calculations.stream().map(c -> c.getUnit()).distinct().collect(Collectors.toList());
        List<CalculationVM> vmList = new ArrayList<>();
        for (PartitioningTable table : distinctTables){
            for (Unit unit : distinctUnits){
                final List<InMemBudgetCalculation> calcsForTableAndUnit = calculations.stream()
                        .filter(c -> c.getTableItem().getPartitioningTable() == table && c.getUnit() == unit)
                        .collect(Collectors.toList());
                if (!calcsForTableAndUnit.isEmpty() ) {
                    final Occupancy occupancyIfAny = occupancyRepository
                            .occupanciesByUnitAndInterval(unit,
                                    budget.getInterval()).stream()
                            .findFirst().orElse(null);
                    final String leaseReference = occupancyIfAny != null ? occupancyIfAny.getLease().getReference() : null;

                    final BigDecimal sumValueForUnitAndTable = calcsForTableAndUnit.stream().map(c -> c.getValue())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    vmList.add(new CalculationVM(
                            unit.getReference(),
                            leaseReference,
                            table.getName(),
                            sumValueForUnitAndTable
                    ));
                }
            }
        }
        StringBuffer fileNameBuffer = new StringBuffer();
        fileNameBuffer.append(budget.getProperty().getReference());
        fileNameBuffer.append("-");
        fileNameBuffer.append(calculationStartDate.toString("ddMM"));
        fileNameBuffer.append("-");
        fileNameBuffer.append(calculationEndDate.toString("ddMMyyyy"));
        fileNameBuffer.append("-");
        fileNameBuffer.append(budgetCalculationType.name());
        fileNameBuffer.append(LocalDateTime.now().toString("yyyyMMddhhmm"));
        fileNameBuffer.append(".xlsx");
        WorksheetSpec spec = new WorksheetSpec(CalculationVM.class, "summaryPerUnit");
        WorksheetContent worksheetContent = new WorksheetContent(vmList.stream().sorted(Comparator.comparing(CalculationVM::getUnitReference)).collect(
                Collectors.toList()), spec);
        return excelService.toExcelPivot(worksheetContent, fileNameBuffer.toString());
    }

    public BudgetCalculationType default0$$(){
        return BudgetCalculationType.BUDGETED;
    }

    public LocalDate default1$$(){
        return budget.getStartDate();
    }

    public LocalDate default2$$(){
        return budget.getEndDate();
    }

    @Inject ExcelService excelService;

    @Inject OccupancyRepository occupancyRepository;

    @Inject BudgetCalculationService budgetCalculationService;

}
