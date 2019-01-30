package org.estatio.module.budgetassignment.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.service.BudgetAssignmentService;

@Mixin
public class Unit_RecalculateServiceChargeBudgetResults {

    private final Unit unit;
    public Unit_RecalculateServiceChargeBudgetResults(Unit unit){
        this.unit = unit;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<BudgetCalculationResult> reCalculateServiceChargeBudgetResults(
            final Budget budget
    ) {
        return budgetAssignmentService.calculatResultsForUnit(budget, BudgetCalculationType.BUDGETED, unit);
    }

    public List<Budget> choices0ReCalculateServiceChargeBudgetResults(){
        return budgetRepository.findByProperty(unit.getProperty()).stream().filter(b->b.getStatus()==Status.ASSIGNED).collect(Collectors.toList());
    }

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private BudgetRepository budgetRepository;

}
