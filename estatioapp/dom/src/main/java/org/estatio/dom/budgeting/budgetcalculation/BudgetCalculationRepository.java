package org.estatio.dom.budgeting.budgetcalculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.charge.Charge;

@DomainService(repositoryFor = BudgetCalculation.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationRepository extends UdoDomainRepositoryAndFactory<BudgetCalculation> {

    public BudgetCalculationRepository() {
        super(BudgetCalculationRepository.class, BudgetCalculation.class);
    }

    public BudgetCalculation updateOrCreateTemporaryBudgetCalculation(
            final BudgetItemAllocation budgetItemAllocation,
            final KeyItem keyItem,
            final BigDecimal value,
            final BigDecimal sourceValue,
            final BudgetCalculationType calculationType){

        BudgetCalculation existingCalculation = findUnique(budgetItemAllocation, keyItem, BudgetCalculationStatus.TEMPORARY, calculationType);

        if (existingCalculation != null) {
            existingCalculation.setValue(value);
            existingCalculation.setSourceValue(sourceValue);
            return existingCalculation;
        }

        return createBudgetCalculation(budgetItemAllocation, keyItem, value, sourceValue, calculationType, BudgetCalculationStatus.TEMPORARY);
    }

    public BudgetCalculation createBudgetCalculation(
            final BudgetItemAllocation budgetItemAllocation,
            final KeyItem keyItem,
            final BigDecimal value,
            final BigDecimal sourceValue,
            final BudgetCalculationType calculationType,
            final BudgetCalculationStatus status){

        BudgetCalculation budgetCalculation = newTransientInstance(BudgetCalculation.class);
        budgetCalculation.setBudgetItemAllocation(budgetItemAllocation);
        budgetCalculation.setKeyItem(keyItem);
        budgetCalculation.setValue(value);
        budgetCalculation.setSourceValue(sourceValue);
        budgetCalculation.setCalculationType(calculationType);
        budgetCalculation.setStatus(status);

        persist(budgetCalculation);

        return budgetCalculation;
    }

    public BudgetCalculation updateOrCreateAssignedFromTemporary(final BudgetCalculation calculation){
        BudgetCalculation existingAssignedCalculation = findUnique(calculation.getBudgetItemAllocation(), calculation.getKeyItem(), BudgetCalculationStatus.ASSIGNED, calculation.getCalculationType());
        if (existingAssignedCalculation == null) {
            return createBudgetCalculation(calculation.getBudgetItemAllocation(), calculation.getKeyItem(), calculation.getValue(), calculation.getSourceValue(), calculation.getCalculationType(), BudgetCalculationStatus.ASSIGNED);
        } else {
            existingAssignedCalculation.setValue(calculation.getValue());
            existingAssignedCalculation.setSourceValue(calculation.getSourceValue());
            return existingAssignedCalculation;
        }
    }

    public BudgetCalculation findUnique(
            final BudgetItemAllocation budgetItemAllocation,
            final KeyItem keyItem,
            final BudgetCalculationStatus calculationStatus,
            final BudgetCalculationType calculationType
            ){
        return uniqueMatch(
                "findUnique",
                "budgetItemAllocation", budgetItemAllocation,
                "keyItem", keyItem,
                "status", calculationStatus,
                "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByBudgetItemAllocationAndCalculationType(BudgetItemAllocation budgetItemAllocation, BudgetCalculationType calculationType) {
        return allMatches("findByBudgetItemAllocationAndCalculationType", "budgetItemAllocation", budgetItemAllocation, "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByBudgetItemAllocation(
            final BudgetItemAllocation budgetItemAllocation
    ){
        return allMatches("findByBudgetItemAllocation", "budgetItemAllocation", budgetItemAllocation);
    }

    public List<BudgetCalculation> findByBudgetItemAllocationAndStatus(final BudgetItemAllocation budgetItemAllocation, final BudgetCalculationStatus status) {
        return allMatches("findByBudgetItemAllocationAndStatus", "budgetItemAllocation", budgetItemAllocation, "status", status);
    }

    public List<BudgetCalculation> findByBudgetItemAllocationAndStatusAndCalculationType(final BudgetItemAllocation budgetItemAllocation, final BudgetCalculationStatus status, final BudgetCalculationType calculationType) {
        return allMatches("findByBudgetItemAllocationAndStatusAndCalculationType", "budgetItemAllocation", budgetItemAllocation, "status", status, "calculationType", calculationType);
    }

    public List<BudgetCalculation> allBudgetCalculations() {
        return allInstances();
    }

    public List<BudgetCalculation> findByBudgetAndCharge(final Budget budget, final Charge charge) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()){
            for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()){
                if (allocation.getCharge().equals(charge)) {
                    result.addAll(findByBudgetItemAllocation(allocation));
                }
            }
        }
        return result;
    }

    public List<BudgetCalculation> findByBudget(final Budget budget) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndCalculationType(item, BudgetCalculationType.AUDITED));
            result.addAll(findByBudgetItemAndCalculationType(item, BudgetCalculationType.BUDGETED));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndCalculationType(final Budget budget, final BudgetCalculationType calculationType) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndCalculationType(item, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetItemAndCalculationType(final BudgetItem budgetItem, final BudgetCalculationType calculationType) {

        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()) {

            result.addAll(findByBudgetItemAllocationAndCalculationType(allocation, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndStatus(final Budget budget, final BudgetCalculationStatus status) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndStatus(item, status));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetItemAndStatus(final BudgetItem budgetItem, final BudgetCalculationStatus status) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()) {

            result.addAll(findByBudgetItemAllocationAndStatus(allocation, status));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndStatusAndCalculationType(final Budget budget, final BudgetCalculationStatus status, final BudgetCalculationType calculationType) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndStatusAndCalculationType(item, status, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetItemAndStatusAndCalculationType(final BudgetItem budgetItem, final BudgetCalculationStatus status, final BudgetCalculationType calculationType) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()) {

            result.addAll(findByBudgetItemAllocationAndStatusAndCalculationType(allocation, status, calculationType));



        }
        return result;
    }

}

