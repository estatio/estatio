package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationService {

    public List<BudgetCalculation> calculatePersistedCalculations(final Budget budget) {

        removeNewCalculations(budget);

        List<BudgetCalculation> budgetCalculations = new ArrayList<>();
        for (BudgetCalculationViewmodel result : getBudgetedCalculations(budget)){
            budgetCalculations.add(
                    budgetCalculationRepository.findOrCreateBudgetCalculation(
                    result.getPartitionItem(),
                    result.getItem(),
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
                results.addAll(calculateForTotalAndType(partitionItem, partitionItem.getBudgetedValue(), BudgetCalculationType.BUDGETED));
            break;

            case ACTUAL:
                if (partitionItem.getBudgetItem().getAuditedValue() != null) {
                    results.addAll(calculateForTotalAndType(partitionItem, partitionItem.getAuditedValue(), BudgetCalculationType.ACTUAL));
                }
            break;
        }

        return results;
    }

    private List<BudgetCalculationViewmodel> calculateForTotalAndType(final PartitionItem partitionItem, final BigDecimal partitionItemValue, final BudgetCalculationType calculationType) {

        List<BudgetCalculationViewmodel> results = new ArrayList<>();

        final PartitioningTable partitioningTable = partitionItem.getPartitioningTable();
        results.addAll(partitioningTable.calculateFor(partitionItem, partitionItemValue, calculationType));

        /*
        BigDecimal divider = keyTable.getKeyValueMethod().divider(keyTable);

        for (KeyItem tableItem : keyTable.getItems()) {

            BudgetCalculationViewmodel calculationResult;


            calculationResult = new BudgetCalculationViewmodel(
                    partitionItem,
                    tableItem,
                    total.multiply(tableItem.getValue()).
                            divide(divider, MathContext.DECIMAL64).
                            setScale(tableItem.getKeyTable().getPrecision(), BigDecimal.ROUND_HALF_UP),
                    calculationType);



            results.add(calculationResult);
        }
        */

        return results;

    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;


}
