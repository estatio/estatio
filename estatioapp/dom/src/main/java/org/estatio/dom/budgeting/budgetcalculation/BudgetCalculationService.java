package org.estatio.dom.budgeting.budgetcalculation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.budgetassignment.BudgetCalculationLinkRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationService {

    public List<BudgetCalculation> calculatePersistedCalculations(final Budget budget) {

        removeTemporaryCalculations(budget);

        List<BudgetCalculation> budgetCalculations = new ArrayList<>();
        for (BudgetCalculationViewmodel result : getCalculations(budget)){
            budgetCalculations.add(
                    budgetCalculationRepository.updateOrCreateTemporaryBudgetCalculation(
                    result.getBudgetItemAllocation(),
                    result.getKeyItem(),
                    result.getValue(),
                    result.getValue(),
                    result.getCalculationType())
            );
        }
        return budgetCalculations;
    }

    public void removeTemporaryCalculations(final Budget budget) {
        for (BudgetCalculation calc : budgetCalculationRepository.findByBudgetAndStatus(budget, BudgetCalculationStatus.TEMPORARY)){
            calc.remove();
        }
    }

    public List<BudgetCalculationViewmodel> getCalculations(final Budget budget){
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            budgetCalculationViewmodels.addAll(calculate(budgetItem));

        }
        return budgetCalculationViewmodels;
    }

    private List<BudgetCalculationViewmodel> calculate(final BudgetItem budgetItem) {

        List<BudgetCalculationViewmodel> result = new ArrayList<>();
        for (BudgetItemAllocation itemAllocation : budgetItem.getBudgetItemAllocations()) {

            result.addAll(calculate(itemAllocation));

        }

        return result;
    }

    private List<BudgetCalculationViewmodel> calculate(final BudgetItemAllocation itemAllocation) {

        List<BudgetCalculationViewmodel> results = new ArrayList<>();

        BigDecimal budgetedTotal = percentageOf(itemAllocation.getBudgetItem().getBudgetedValue(), itemAllocation.getPercentage());
        results.addAll(calculateForTotalAndType(itemAllocation, budgetedTotal, BudgetCalculationType.BUDGETED));

        if (itemAllocation.getBudgetItem().getAuditedValue() != null){
            BigDecimal auditedTotal = percentageOf(itemAllocation.getBudgetItem().getAuditedValue(), itemAllocation.getPercentage());
            results.addAll(calculateForTotalAndType(itemAllocation,auditedTotal, BudgetCalculationType.AUDITED));
        }

        return results;
    }

    private List<BudgetCalculationViewmodel> calculateForTotalAndType(final BudgetItemAllocation itemAllocation, final BigDecimal total, final BudgetCalculationType calculationType) {

        List<BudgetCalculationViewmodel> results = new ArrayList<>();

        BigDecimal divider = itemAllocation.getKeyTable().getKeyValueMethod().divider(itemAllocation.getKeyTable());

        for (KeyItem keyItem : itemAllocation.getKeyTable().getItems()) {

            BudgetCalculationViewmodel calculationResult;


            calculationResult = new BudgetCalculationViewmodel(
                    itemAllocation,
                    keyItem,
                    total.multiply(keyItem.getValue()).
                            divide(divider, MathContext.DECIMAL64).
                            setScale(keyItem.getKeyTable().getPrecision(), BigDecimal.ROUND_HALF_UP),
                    calculationType);



            results.add(calculationResult);
        }

        return results;

    }

    private BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {
        return value
                .multiply(percentage)
                .divide(new BigDecimal("100"), MathContext.DECIMAL64);
    }


    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private BudgetCalculationLinkRepository budgetCalculationLinkRepository;


}
