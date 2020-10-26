package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keytable.KeyTable;

@Mixin
public class Budget_downloadCalculationsForUnit {

    private final Budget budget;

    public Budget_downloadCalculationsForUnit(Budget budget) {
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob $$(final Unit unit, final BudgetCalculationType budgetCalculationType, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {
        final List<InMemBudgetCalculation> calculations= budgetCalculationService.calculateInMemForUnit(budget, budgetCalculationType, unit, calculationStartDate, calculationEndDate);
        List<CalculationVMForUnit> vmList = new ArrayList<>();
        for (InMemBudgetCalculation calculation : calculations){
            StringBuffer buffer = new StringBuffer();
            buffer.append(calculation.getIncomingCharge().getReference());
            buffer.append("-");
            if (budgetCalculationType==BudgetCalculationType.BUDGETED && calculation.getPartitionItem().getFixedBudgetedAmount()!=null){
                buffer.append("fixed amount-");
                buffer.append(calculation.getPartitionItem().getFixedBudgetedAmount());
            } else {
                if (budgetCalculationType == BudgetCalculationType.AUDITED
                        && calculation.getPartitionItem().getFixedAuditedAmount() != null) {
                    buffer.append("fixed amount-");
                    buffer.append(calculation.getPartitionItem().getFixedAuditedAmount());
                } else {
                    buffer.append(calculation.getPartitionItem().getPercentage().setScale(2, RoundingMode.HALF_UP));
                    buffer.append("%");
                }
            }
            String incomingChargeReferenceAndPartitioning = buffer.toString();

            final BigDecimal budgetItemAmount = budgetCalculationType==BudgetCalculationType.BUDGETED ? calculation.getPartitionItem().getBudgetItem().getBudgetedValue() : calculation.getPartitionItem().getBudgetItem().getAuditedValue();

            StringBuffer buffer1 = new StringBuffer();
            buffer1.append(calculation.getTableItem().getPartitioningTable().getName());
            if (calculation.getTableItem().getClass().isAssignableFrom(KeyItem.class)){
                KeyItem item = (KeyItem) calculation.getTableItem();
                KeyTable keyTable = (KeyTable) item.getPartitioningTable();
                switch (keyTable.getFoundationValueType()){
                case AREA:
                    buffer1.append("-m2-");
                    buffer1.append(item.getSourceValue().setScale(2, RoundingMode.HALF_UP));
                    break;
                case COUNT:
                case MANUAL:
                        buffer1.append("-src val-");
                        buffer1.append(item.getSourceValue().setScale(2, RoundingMode.HALF_UP));
                }
            }
            final String tableNameAndSourceValue =
                    buffer1.toString();
            vmList.add(
                    new CalculationVMForUnit(
                            incomingChargeReferenceAndPartitioning,
                            budgetItemAmount,
                            calculation.getPartitionItem().getBudgetItem().getCalculationDescription(),
                            tableNameAndSourceValue,
                            calculation.getValue()
                    )
            );
        }
        StringBuffer fileNameBuffer = new StringBuffer();
        fileNameBuffer.append(unit.getReference());
        fileNameBuffer.append("-");
        fileNameBuffer.append(calculationStartDate.toString("ddMM"));
        fileNameBuffer.append("-");
        fileNameBuffer.append(calculationEndDate.toString("ddMMyyyy"));
        fileNameBuffer.append("-");
        fileNameBuffer.append(budgetCalculationType.name());
        fileNameBuffer.append(LocalDateTime.now().toString("yyyyMMddhhmm"));
        fileNameBuffer.append(".xlsx");
        WorksheetSpec spec = new WorksheetSpec(CalculationVMForUnit.class, "summaryPerUnit");
        WorksheetContent worksheetContent = new WorksheetContent(vmList.stream().sorted(Comparator.comparing(CalculationVMForUnit::getBudgetItemChargeReferenceAndPartitioning)).collect(
                Collectors.toList()), spec);
        return excelService.toExcelPivot(worksheetContent, fileNameBuffer.toString());
    }

    public List<Unit> choices0$$(){
        return unitRepository.findByProperty(budget.getProperty()).stream()
                .filter(u->u.getEndDate()==null || !u.getEndDate().isBefore(budget.getStartDate()))
                .filter(u->u.getStartDate()==null || !u.getStartDate().isAfter(budget.getEndDate()))
                .sorted(Comparator.comparing(Unit::getReference))
                .collect(Collectors.toList());
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

    @Inject BudgetCalculationService budgetCalculationService;

    @Inject ExcelService excelService;

    @Inject UnitRepository unitRepository;

}
