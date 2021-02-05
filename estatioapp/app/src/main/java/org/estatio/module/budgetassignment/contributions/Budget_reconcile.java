package org.estatio.module.budgetassignment.contributions;

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
import org.estatio.module.budgetassignment.dom.BudgetAssignmentService;

/**
 * This currently could be inlined into Budget, however it is incomplete and my suspicion is that eventually it
 * may (like the other mixins that do calculations) will depend upon services that are not within Budget.
 */
@Mixin
public class Budget_reconcile {

    private final Budget budget;
    public Budget_reconcile(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget reconcile(
            @ParameterLayout(describedAs = "When checked, this will persist the calculations and put the budget on Reconciled, but will NOT impact the lease terms")
    final boolean doNotImpactLeaseTerms) {
        budgetCalculationService.calculate(budget, BudgetCalculationType.AUDITED, budget.getStartDate(), budget.getEndDate(), true);
        budgetAssignmentService
                .calculateResults(budget, BudgetCalculationType.AUDITED);
        budget.setStatus(Status.RECONCILING);
        if (!doNotImpactLeaseTerms) {
            budgetAssignmentService.assignNonAssignedCalculationResultsToLeases(budget);
        }
        budget.setStatus(Status.RECONCILED);
        return budget;
    }

    public boolean hideReconcile(){
        return budget.getStatus()!=Status.ASSIGNED;
    }

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

}
