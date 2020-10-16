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
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.BudgetAssignmentService;

/**
 * This currently could be inlined into Budget, however it is incomplete and my suspicion is that eventually it
 * may (like the other mixins that do calculations) will depend upon services that are not within Budget.
 */
@Mixin
public class Budget_Reconcile {

    private final Budget budget;
    public Budget_Reconcile(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget reconcile(
            @ParameterLayout(describedAs = "Final calculation will make the calculations permanent and impact the leases")
            final boolean finalCalculation) {
            budgetCalculationService.calculate(budget, BudgetCalculationType.AUDITED);
            if (finalCalculation){
                List<BudgetCalculationResult> results = budgetAssignmentService.calculateResults(budget, BudgetCalculationType.AUDITED);
                budgetAssignmentService.assignNonAssignedCalculationResultsToLeases(results);
                budget.setStatus(Status.RECONCILED);
            }
        return budget;
    }

    public String disableReconcile(){
        return budget.getStatus()==Status.NEW ? "A budget with status new cannot be reconciled" : null;
    }

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

}
