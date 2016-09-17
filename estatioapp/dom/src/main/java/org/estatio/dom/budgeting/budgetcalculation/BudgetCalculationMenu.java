package org.estatio.dom.budgeting.budgetcalculation;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.dom.budgetassignment.BudgetAssignmentService;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetCalculationMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<BudgetCalculation> allBudgetCalculations(){
        return budgetCalculationRepository.allBudgetCalculations();
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public void calculateAndAssignAllBudgetsActiveOnDate(final LocalDate localDate) throws Exception {

        for (Budget budget : budgetRepository.allBudgets()){
            if (budget.getInterval().contains(localDate)) {
                budget.calculate();
                budgetAssignmentService.assignBudgetCalculations(budget);
            }
        }

    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private BudgetRepository budgetRepository;

}
