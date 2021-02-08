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


@Mixin
public class Budget_assign {

    private final Budget budget;
    public Budget_assign(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget assign(
            @ParameterLayout(describedAs = "When checked, this will persist the calculations and put the budget on Assigned, but will NOT impact the lease terms")
            final boolean doNotImpactLeaseTerms
    ) {
        budgetCalculationService.calculate(budget, BudgetCalculationType.BUDGETED, budget.getStartDate(), budget.getEndDate(), true);
        List<BudgetCalculationResult> results = budgetAssignmentService.calculateResults(budget, BudgetCalculationType.BUDGETED);
        if (!doNotImpactLeaseTerms){
            budgetAssignmentService.assignNonAssignedCalculationResultsToLeases(budget, BudgetCalculationType.BUDGETED);
        }
        budget.setStatus(Status.ASSIGNED);
        return budget;
    }

    public boolean hideAssign(){
        return budget.getStatus()!=Status.NEW;
    }

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

}
