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

    public List<BudgetCalculation> calculate(final Budget budget, final BudgetCalculationType type) {

        removeNewCalculationsOfType(budget, type);

        List<BudgetCalculation> budgetCalculations = new ArrayList<>();
        for (BudgetCalculationViewmodel result : getCalculationsForType(budget, type)){
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

    public void removeNewCalculationsOfType(final Budget budget, final BudgetCalculationType type) {
        budgetCalculationRepository.findByBudgetAndTypeAndStatus(budget, type, Status.NEW).forEach(
                c->budgetCalculationRepository.delete(c)
        );
    }

    public List<BudgetCalculationViewmodel> getCalculationsForType(final Budget budget, final BudgetCalculationType type){
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            budgetCalculationViewmodels.addAll(calculate(budgetItem, type));

        }
        return budgetCalculationViewmodels;
    }

    public List<BudgetCalculationViewmodel> getAllCalculations(final Budget budget){
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            budgetCalculationViewmodels.addAll(calculate(budgetItem, BudgetCalculationType.BUDGETED));
            budgetCalculationViewmodels.addAll(calculate(budgetItem, BudgetCalculationType.AUDITED));

        }
        return budgetCalculationViewmodels;
    }

    private List<BudgetCalculationViewmodel> calculate(final BudgetItem budgetItem, final BudgetCalculationType type) {

        List<BudgetCalculationViewmodel> result = new ArrayList<>();
        for (PartitionItem partitionItem : budgetItem.getPartitionItemsForType(type)) {

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

            case AUDITED:
                if (partitionItem.getBudgetItem().getAuditedValue() != null) {
                    results.addAll(calculateForTotalAndType(partitionItem, partitionItem.getAuditedValue(), BudgetCalculationType.AUDITED));
                }
            break;
        }

        return results;
    }

    private List<BudgetCalculationViewmodel> calculateForTotalAndType(final PartitionItem partitionItem, final BigDecimal partitionItemValue, final BudgetCalculationType calculationType) {

        List<BudgetCalculationViewmodel> results = new ArrayList<>();

        final PartitioningTable partitioningTable = partitionItem.getPartitioningTable();
        results.addAll(partitioningTable.calculateFor(partitionItem, partitionItemValue, calculationType));

        return results;

    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;


}
