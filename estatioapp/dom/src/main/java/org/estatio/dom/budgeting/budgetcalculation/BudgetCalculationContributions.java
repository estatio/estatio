package org.estatio.dom.budgeting.budgetcalculation;


import org.apache.isis.applib.annotation.*;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BudgetCalculationContributions {

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget calculate(final Budget budget){
        budgetCalculationRepository.resetAndUpdateOrCreateBudgetCalculations(
                budget,
                budgetCalculationService.calculate(budget));
        return budget;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget assignCalculationsToLeases(final Budget budget){
        budgetCalculationService.assignBudgetCalculationsToLeases(budget);
        return budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<BudgetCalculation> budgetedCalculations (final Budget budget) {
        return budgetCalculationRepository.findByBudgetAndCalculationType(budget, CalculationType.BUDGETED);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<BudgetCalculation> auditedCalculations (final Budget budget) {
        return budgetCalculationRepository.findByBudgetAndCalculationType(budget, CalculationType.AUDITED);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> calculations(final BudgetItemAllocation budgetItemAllocation){
        return budgetCalculationRepository.findByBudgetItemAllocation(budgetItemAllocation);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> budgetedCalculations (final BudgetItem budgetItem) {
        return budgetCalculationRepository.findByBudgetItemAndCalculationType(budgetItem, CalculationType.BUDGETED);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> auditedCalculations (final BudgetItem budgetItem) {
        return budgetCalculationRepository.findByBudgetItemAndCalculationType(budgetItem, CalculationType.AUDITED);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> budgetCalculations (final LeaseTermForServiceCharge leaseTerm) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetCalculationLink link : budgetCalculationLinkRepository.findByLeaseTerm(leaseTerm)){
            result.add(link.getBudgetCalculation());
        }
        return result;
    }

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private BudgetCalculationLinkRepository budgetCalculationLinkRepository;

}
