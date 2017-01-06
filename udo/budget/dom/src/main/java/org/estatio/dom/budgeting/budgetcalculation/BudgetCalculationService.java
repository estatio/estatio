package org.estatio.dom.budgeting.budgetcalculation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationService {

    public List<BudgetCalculation> calculatePersistedCalculations(final Budget budget) {

        removeNewCalculations(budget);

        List<BudgetCalculation> budgetCalculations = new ArrayList<>();
        for (BudgetCalculationViewmodel result : getBudgetedCalculations(budget)){
            budgetCalculations.add(
                    budgetCalculationRepository.findOrCreateBudgetCalculation(
                    result.getPartitionItem(),
                    result.getKeyItem(),
                    result.getValue(),
                    result.getCalculationType())
            );
        }
        return budgetCalculations;
    }

    public void removeNewCalculations(final Budget budget) {
        for (BudgetCalculation calc : budgetCalculationRepository.findByBudget(budget)){
            calc.removeWithStatusNew();
        }
    }

    public List<BudgetCalculationViewmodel> getBudgetedCalculations(final Budget budget){
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            budgetCalculationViewmodels.addAll(calculate(budgetItem, BudgetCalculationType.BUDGETED));

        }
        return budgetCalculationViewmodels;
    }

    public List<BudgetCalculationViewmodel> getAuditedCalculations(final Budget budget){
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            budgetCalculationViewmodels.addAll(calculate(budgetItem, BudgetCalculationType.ACTUAL));

        }
        return budgetCalculationViewmodels;
    }

    public List<BudgetCalculationViewmodel> getAllCalculations(final Budget budget){
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            budgetCalculationViewmodels.addAll(calculate(budgetItem, BudgetCalculationType.BUDGETED));
            budgetCalculationViewmodels.addAll(calculate(budgetItem, BudgetCalculationType.ACTUAL));

        }
        return budgetCalculationViewmodels;
    }

    private List<BudgetCalculationViewmodel> calculate(final BudgetItem budgetItem, final BudgetCalculationType type) {

        List<BudgetCalculationViewmodel> result = new ArrayList<>();
        for (PartitionItem partitionItem : budgetItem.getPartitionItems()) {

            result.addAll(calculate(partitionItem, type));

        }

        return result;
    }

    private List<BudgetCalculationViewmodel> calculate(final PartitionItem partitionItem, final BudgetCalculationType type) {

        List<BudgetCalculationViewmodel> results = new ArrayList<>();

        switch (type) {
            case BUDGETED:
                BigDecimal budgetedTotal = percentageOf(partitionItem.getBudgetItem().getBudgetedValue(), partitionItem.getPercentage());
                results.addAll(calculateForTotalAndType(partitionItem, budgetedTotal, BudgetCalculationType.BUDGETED));
            break;

            case ACTUAL:
                if (partitionItem.getBudgetItem().getAuditedValue() != null) {
                    BigDecimal auditedTotal = percentageOf(partitionItem.getBudgetItem().getAuditedValue(), partitionItem.getPercentage());
                    results.addAll(calculateForTotalAndType(partitionItem, auditedTotal, BudgetCalculationType.ACTUAL));
                }
            break;
        }

        return results;
    }

    private List<BudgetCalculationViewmodel> calculateForTotalAndType(final PartitionItem partitionItem, final BigDecimal total, final BudgetCalculationType calculationType) {

        List<BudgetCalculationViewmodel> results = new ArrayList<>();

        BigDecimal divider = partitionItem.getKeyTable().getKeyValueMethod().divider(partitionItem.getKeyTable());

        for (KeyItem keyItem : partitionItem.getKeyTable().getItems()) {

            BudgetCalculationViewmodel calculationResult;


            calculationResult = new BudgetCalculationViewmodel(
                    partitionItem,
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


}
