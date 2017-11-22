package org.estatio.module.budgetassignment.dom.calculationresult;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.lease.dom.Lease;

@DomainService(repositoryFor = BudgetCalculationRun.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationRunRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationRun> {

    public BudgetCalculationRunRepository() {
        super(BudgetCalculationRunRepository.class, BudgetCalculationRun.class);
    }

    public BudgetCalculationRun createBudgetCalculationRun(
            final Lease lease,
            final Budget budget,
            final BudgetCalculationType type,
            final Status status){

        BudgetCalculationRun budgetCalculationRun = newTransientInstance(BudgetCalculationRun.class);
        budgetCalculationRun.setLease(lease);
        budgetCalculationRun.setBudget(budget);
        budgetCalculationRun.setType(type);
        budgetCalculationRun.setStatus(status);

        persist(budgetCalculationRun);

        return budgetCalculationRun;
    }

    public BudgetCalculationRun findOrCreateNewBudgetCalculationRun(
            final Lease lease,
            final Budget budget,
            final BudgetCalculationType type
    ){
        BudgetCalculationRun run = findUnique(lease, budget, type);
        return run== null ? createBudgetCalculationRun(lease, budget, type, Status.NEW) : run;
    }

    public BudgetCalculationRun findUnique(final Lease lease, final Budget budget, final BudgetCalculationType type){
        return uniqueMatch("findUnique", "lease", lease, "budget", budget, "type", type);
    }

    public List<BudgetCalculationRun> allBudgetCalculationRuns(){
        return allInstances();
    }

    public List<BudgetCalculationRun> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    public List<BudgetCalculationRun> findByBudgetAndType(final Budget budget, final BudgetCalculationType type) {
        return allMatches("findByBudgetAndType", "budget", budget, "type", type);
    }

    public List<BudgetCalculationRun> findByBudgetAndTypeAndStatus(final Budget budget, final BudgetCalculationType type, final Status status) {
        return allMatches("findByBudgetAndTypeAndStatus", "budget", budget, "type", type, "status", status);
    }

    public List<BudgetCalculationRun> findByBudget(final Budget budget) {
        return allMatches("findByBudget", "budget", budget);
    }
}

