package org.estatio.module.budgetassignment.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;

/**
 * This cannot be inlined (needs to be a mixin) because Budget doesn't know about BudgetCalculationResultLinkRepository
 */
@Mixin
public class Budget_calculationResults {

    private final Budget budget;
    public Budget_calculationResults(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculationResult> calculationResults() {
        return budgetCalculationResultRepository.findByBudget(budget);
    }

    @Inject private BudgetCalculationResultRepository budgetCalculationResultRepository;

}
