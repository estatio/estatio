package org.estatio.dom.budgeting.budgetcalculation;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.charge.Charge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DomainService(repositoryFor = BudgetCalculation.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationRepository extends UdoDomainRepositoryAndFactory<BudgetCalculation> {

    public BudgetCalculationRepository() {
        super(BudgetCalculationRepository.class, BudgetCalculation.class);
    }

    @Programmatic
    public void resetAndUpdateOrCreateBudgetCalculations(final Budget budget, final List<BudgetCalculationResult> budgetCalculationResults){

        for (BudgetCalculationResult result : budgetCalculationResults){
            if (result.getBudgetItemAllocation().getBudgetItem().getBudget() != budget){
                throw new IllegalArgumentException("All budgetCalculations should have same budget");
            }
        }

        //set all existing audited calculations to 0
        for (BudgetCalculation calculation : findByBudgetAndCalculationType(budget, CalculationType.AUDITED)){
            calculation.setValue(BigDecimal.ZERO);
            calculation.setSourceValue(BigDecimal.ZERO);
        }

        // update or create calculations with new results
        for (BudgetCalculationResult result : budgetCalculationResults){

            updateOrCreateBudgetCalculation(
                    result.getBudgetItemAllocation(),
                    result.getKeyItem(),
                    result.getValue(),
                    result.getSourceValue(),
                    result.getCalculationType());

        }

    }

    public BudgetCalculation updateOrCreateBudgetCalculation(
            final BudgetItemAllocation budgetItemAllocation,
            final KeyItem keyItem,
            final BigDecimal value,
            final BigDecimal sourceValue,
            final CalculationType calculationType){

        BudgetCalculation existingCalculation = findByBudgetItemAllocationAndKeyItemAndCalculationType(budgetItemAllocation, keyItem, calculationType);

        if (existingCalculation != null) {
            existingCalculation.setValue(value);
            existingCalculation.setSourceValue(sourceValue);
            return existingCalculation;
        }

        return createBudgetCalculation(budgetItemAllocation, keyItem, value, sourceValue, calculationType);
    }

    private BudgetCalculation createBudgetCalculation(
            final BudgetItemAllocation budgetItemAllocation,
            final KeyItem keyItem,
            final BigDecimal value,
            final BigDecimal sourceValue,
            final CalculationType calculationType){

        BudgetCalculation budgetCalculation = newTransientInstance(BudgetCalculation.class);
        budgetCalculation.setBudgetItemAllocation(budgetItemAllocation);
        budgetCalculation.setKeyItem(keyItem);
        budgetCalculation.setValue(value);
        budgetCalculation.setSourceValue(sourceValue);
        budgetCalculation.setCalculationType(calculationType);

        persist(budgetCalculation);

        return budgetCalculation;
    }

    public BudgetCalculation findByBudgetItemAllocationAndKeyItemAndCalculationType(
            final BudgetItemAllocation budgetItemAllocation,
            final KeyItem keyItem,
            final CalculationType calculationType
            ){
        return uniqueMatch(
                "findByBudgetItemAllocationAndKeyItemAndCalculationType",
                "budgetItemAllocation", budgetItemAllocation,
                "keyItem", keyItem,
                "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByBudgetItemAllocationAndCalculationType(BudgetItemAllocation budgetItemAllocation, CalculationType calculationType) {
        return allMatches("findByBudgetItemAllocationAndCalculationType", "budgetItemAllocation", budgetItemAllocation, "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByBudgetItemAllocation(
            final BudgetItemAllocation budgetItemAllocation
    ){
        return allMatches("findByBudgetItemAllocation", "budgetItemAllocation", budgetItemAllocation);
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

            result.addAll(findByBudgetItemAndCalculationType(item, CalculationType.AUDITED));
            result.addAll(findByBudgetItemAndCalculationType(item, CalculationType.BUDGETED));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndCalculationType(final Budget budget, final CalculationType calculationType) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndCalculationType(item, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetItemAndCalculationType(final BudgetItem budgetItem, final CalculationType calculationType) {

        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()) {

            result.addAll(findByBudgetItemAllocationAndCalculationType(allocation, calculationType));

        }
        return result;
    }

}
