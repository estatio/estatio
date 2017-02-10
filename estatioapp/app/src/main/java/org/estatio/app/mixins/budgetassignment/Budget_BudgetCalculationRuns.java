package org.estatio.app.mixins.budgetassignment;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.budget.dom.budget.Budget;

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
