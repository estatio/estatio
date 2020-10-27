package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationService {

    public void calculate(final Budget budget, final BudgetCalculationType type, final LocalDate calculationStartDate, final LocalDate calculationEndDate, final boolean persistCalculations) {
        // TODO: is this wise? Or would it be better to remove only when persist calculations is checked?
        // TODO: should this be refined to calculation period?
        removeNewCalculationsOfType(budget, type);
        final List<InMemBudgetCalculation> inMemCalcs = calculateInMem(budget, type, calculationStartDate,
                calculationEndDate);
        if (persistCalculations){
            inMemCalcs.forEach(c->{
                budgetCalculationRepository.findOrCreateBudgetCalculation(c);
            });
        }
    }

    public void removeNewCalculationsOfType(final Budget budget, final BudgetCalculationType type) {
        budgetCalculationRepository.findByBudgetAndTypeAndStatus(budget, type, Status.NEW).forEach(
                c->budgetCalculationRepository.delete(c)
        );
    }

    public List<InMemBudgetCalculation> calculateInMem(final Budget budget, final BudgetCalculationType type, final LocalDate calculationStartDate, final LocalDate calculationEndDate){
        List<InMemBudgetCalculation> calculations = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {
            calculations.addAll(calculateInMem(budgetItem, type, calculationStartDate, calculationEndDate));
        }
        return calculations;
    }

    public List<InMemBudgetCalculation> calculateInMemForUnit(final Budget budget, final BudgetCalculationType type, final Unit unit, final LocalDate calculationStartDate, final LocalDate calculationEndDate){
        List<InMemBudgetCalculation> calculations = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {
            calculations.addAll(calculateInMemForUnit(budgetItem, type, unit, calculationStartDate, calculationEndDate));
        }
        return calculations;
    }

    private List<InMemBudgetCalculation> calculateInMem(final BudgetItem budgetItem, final BudgetCalculationType type, final LocalDate calculationStartDate, final LocalDate calculationEndDate){
        List<InMemBudgetCalculation> result = new ArrayList<>();
        for (PartitionItem partitionItem : budgetItem.getPartitionItemsForType(type)) {
            result.addAll(calculateInMem(partitionItem, type, calculationStartDate, calculationEndDate));
        }
        return result;
    }

    private List<InMemBudgetCalculation> calculateInMemForUnit(final BudgetItem budgetItem, final BudgetCalculationType type, final Unit unit, final LocalDate calculationStartDate, final LocalDate calculationEndDate){
        List<InMemBudgetCalculation> result = new ArrayList<>();
        for (PartitionItem partitionItem : budgetItem.getPartitionItemsForType(type)) {
            result.addAll(calculateInMemForUnit(partitionItem, type, unit, calculationStartDate, calculationEndDate));
        }
        return result;
    }

    private List<InMemBudgetCalculation> calculateInMem(final PartitionItem partitionItem, final BudgetCalculationType type, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {

        List<InMemBudgetCalculation> results = new ArrayList<>();

        switch (type) {
        case BUDGETED:
            results.addAll(
                    calculateInMemForTotalAndType(partitionItem, partitionItem.getBudgetedValue(), BudgetCalculationType.BUDGETED, calculationStartDate, calculationEndDate));
            break;

        // We do not bother here about the audited value that will be derived from incoming invoices for occupancies not covering the budget period;
        // the calculation results for those will not be assigned to lease terms by BudgetsAssignmentService#assignNonAssignedCalculationResultsToLeases
        case AUDITED:
            if (partitionItem.getBudgetItem().getAuditedValue() != null) {
                results.addAll(
                        calculateInMemForTotalAndType(partitionItem, partitionItem.getAuditedValue(), BudgetCalculationType.AUDITED, calculationStartDate, calculationEndDate));
            }
            break;
        }

        return results;
    }

    public List<InMemBudgetCalculation> calculateInMemForUnitPartitionItemAndAuditedPartitionItemValue(final PartitionItem partitionItem, final BigDecimal auditedPartitionItemValue, final Unit unit, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {

        List<InMemBudgetCalculation> results = new ArrayList<>();

        if (auditedPartitionItemValue != null) {
            results.addAll(
                    calculateInMemForUnitTotalAndType(partitionItem, auditedPartitionItemValue, BudgetCalculationType.AUDITED, unit, calculationStartDate, calculationEndDate));
        }

        return results;
    }

    private List<InMemBudgetCalculation> calculateInMemForUnit(final PartitionItem partitionItem, final BudgetCalculationType type, final Unit unit, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {

        List<InMemBudgetCalculation> results = new ArrayList<>();

        switch (type) {
        case BUDGETED:
            results.addAll(
                    calculateInMemForUnitTotalAndType(partitionItem, partitionItem.getBudgetedValue(), BudgetCalculationType.BUDGETED, unit, calculationStartDate, calculationEndDate));
            break;

        // We do not bother here about the audited value that will be derived from incoming invoices for occupancies not covering the budget period;
        // the calculation results for those will not be assigned to lease terms by BudgetsAssignmentService#assignNonAssignedCalculationResultsToLeases
        case AUDITED:
            if (partitionItem.getBudgetItem().getAuditedValue() != null) {
                results.addAll(
                        calculateInMemForUnitTotalAndType(partitionItem, partitionItem.getAuditedValue(), BudgetCalculationType.AUDITED, unit, calculationStartDate, calculationEndDate));
            }
            break;
        }

        return results;
    }

    private List<InMemBudgetCalculation> calculateInMemForTotalAndType(final PartitionItem partitionItem, final BigDecimal partitionItemValue, final BudgetCalculationType calculationType, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {
        List<InMemBudgetCalculation> results = new ArrayList<>();
        final PartitioningTable partitioningTable = partitionItem.getPartitioningTable();
        results.addAll(partitioningTable.calculateInMemFor(partitionItem, partitionItemValue, calculationType, calculationStartDate, calculationEndDate));
        return results;
    }

    private List<InMemBudgetCalculation> calculateInMemForUnitTotalAndType(final PartitionItem partitionItem, final BigDecimal partitionItemValue, final BudgetCalculationType calculationType, final Unit unit, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {
        List<InMemBudgetCalculation> results = new ArrayList<>();
        final PartitioningTable partitioningTable = partitionItem.getPartitioningTable();
        results.addAll(partitioningTable.calculateInMemForUnit(partitionItem, partitionItemValue, calculationType, unit, calculationStartDate, calculationEndDate));
        return results;
    }

    public static CalculationVMForUnit inMemCalculationToVMForUnit(final InMemBudgetCalculation calculation){
        final BigDecimal budgetItemAmount = calculation.getCalculationType()==BudgetCalculationType.BUDGETED ? calculation.getPartitionItem().getBudgetItem().getBudgetedValue() : calculation.getPartitionItem().getBudgetItem().getAuditedValue();
        return new CalculationVMForUnit(
                incomingChargeReferenceAndPartitioning(calculation),
                budgetItemAmount,
                calculation.getPartitionItem().getBudgetItem().getCalculationDescription(),
                tableNameAndSourceValue(calculation),
                calculation.getValue()
        );
    }

    public static String incomingChargeReferenceAndPartitioning(final InMemBudgetCalculation calculation){
        StringBuffer buffer = new StringBuffer();
        buffer.append(calculation.getIncomingCharge().getReference());
        buffer.append("-");
        if (calculation.getCalculationType()==BudgetCalculationType.BUDGETED && calculation.getPartitionItem().getFixedBudgetedAmount()!=null){
            buffer.append("fixed amount-");
            buffer.append(calculation.getPartitionItem().getFixedBudgetedAmount());
        } else {
            if (calculation.getCalculationType() == BudgetCalculationType.AUDITED
                    && calculation.getPartitionItem().getFixedAuditedAmount() != null) {
                buffer.append("fixed amount-");
                buffer.append(calculation.getPartitionItem().getFixedAuditedAmount());
            } else {
                buffer.append(calculation.getPartitionItem().getPercentage().setScale(2, RoundingMode.HALF_UP));
                buffer.append("%");
            }
        }
        return buffer.toString();
    }

    public static String tableNameAndSourceValue(final InMemBudgetCalculation calculation){
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
        return buffer1.toString();
    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;


}
