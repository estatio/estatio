package org.estatio.module.budgetassignment.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.service.BudgetAssignmentService;

/**
 * This cannot be inlined (needs to be a mixin) because Budget doesn't know about BudgetAssignmentService.
 */
@Mixin
public class Budget_Calculate {

    private final Budget budget;
    public Budget_Calculate(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget calculate(
            @ParameterLayout(describedAs = "Final calculation will make the calculations permanent and impact the leases")
            final boolean finalCalculation
    ) {
        budgetCalculationService.calculatePersistedCalculations(budget);
        if (finalCalculation){
            List<BudgetCalculationResult> results = budgetAssignmentService.calculateResults(budget, BudgetCalculationType.BUDGETED);
            budgetAssignmentService.assignCalculationResultsToLeases(results);
        }
        return budget;
    }

    public String disableCalculate(){
        return budget.noUnassignedItemsForTypeReason(BudgetCalculationType.BUDGETED);
    }

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

}
