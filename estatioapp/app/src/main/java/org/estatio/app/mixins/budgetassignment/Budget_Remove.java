package org.estatio.app.mixins.budgetassignment;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.user.UserService;

import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLink;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.Status;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.budgeting.partioning.PartitionItemRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class Budget_Remove {

    private final Budget budget;
    public Budget_Remove(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public void removeBudget(
            @ParameterLayout(named = "This will delete the budget and all associated data including keytables, calculations, runs, results and lease terms. (You may consider downloading the budget and the keytables beforehand.) Are you sure?")
            final boolean areYouSure
    ) {

        // delete results and runs
        for (BudgetCalculationRun run : budgetCalculationRunRepository.allBudgetCalculationRuns().stream().filter(x->x.getBudget().equals(budget)).collect(Collectors.toList())){
            for (BudgetCalculationResult result : run.getBudgetCalculationResults()){
                // delete links and lease terms
                for (BudgetCalculationResultLink link : budgetCalculationResultLinkRepository.findByCalculationResult(result)){
                    LeaseTermForServiceCharge leaseTermToRemove = null;
                    if (link.getLeaseTermForServiceCharge()!=null) {
                        leaseTermToRemove = link.getLeaseTermForServiceCharge();
                    }
                    link.remove();
                    if (leaseTermToRemove!=null) {
                        leaseTermToRemove.remove();
                    }
                }
            }
            run.remove();
        }

        // delete overrides and values
        for (Lease lease : leaseRepository.findByAssetAndActiveOnDate(budget.getProperty(), budget.getStartDate())){
            for (BudgetOverride override : budgetOverrideRepository.findByLease(lease)) {
                override.remove();
            }
        }

        // delete partition items
        for (BudgetItem budgetItem : budget.getItems()) {
            for (PartitionItem item : partitionItemRepository.findByBudgetItem(budgetItem)) {
                item.remove();
            }
        }

        budget.remove();
    }

    public String disableRemoveBudget(final boolean areYouSure){
        for (BudgetCalculation calculation : budgetCalculationRepository.findByBudget(budget)){
            if (calculation.getStatus().equals(Status.ASSIGNED)){
                return "This budget is assigned already";
            }
        }
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser()) ? "You need administrator rights to remove a budget" : null;
    }

    public String validateRemoveBudget(final boolean areYouSure){
        return areYouSure ? null : "Please confirm";
    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private PartitionItemRepository partitionItemRepository;

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    private BudgetCalculationResultLinkRepository budgetCalculationResultLinkRepository;

    @Inject
    private UserService userService;

}
