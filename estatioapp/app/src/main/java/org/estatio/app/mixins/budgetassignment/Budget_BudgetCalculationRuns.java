package org.estatio.app.mixins.budgetassignment;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.lease.Lease;

@Mixin
public class Budget_BudgetCalculationRuns {

    private final Budget budget;
    public Budget_BudgetCalculationRuns(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculationRun> budgetCalculationRuns() {
        return budgetCalculationRunRepository.findByBudget(budget);
    }

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

}
