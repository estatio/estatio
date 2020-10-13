package org.estatio.module.budgetassignment.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.user.UserService;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.capex.contributions.BudgetItem_IncomingInvoiceItems;

/**
 * This cannot be inlined (needs to be a mixin) because Budget doesn't know about BudgetCalculationResultLinkRepository
 */
@Mixin
public class Budget_Remove {

    private final Budget budget;
    public Budget_Remove(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public void removeBudget(
            @ParameterLayout(named = "This will delete the budget and all associated data including keytables and calculations. (You may consider downloading the budget and the keytables beforehand.) Are you sure?")
            final boolean areYouSure
    ) {

        budget.removeNewCalculationsOfType(BudgetCalculationType.BUDGETED);

        // delete partition items
        for (BudgetItem budgetItem : budget.getItems()) {
            for (PartitionItem item : partitionItemRepository.findByBudgetItem(budgetItem)) {
                item.remove();
            }
        }
        
        budget.remove();
    }

    public String disableRemoveBudget(){
        if (budget.getStatus()!=Status.NEW) return "This budget is not in a state of new";
        for (BudgetItem budgetItem : budget.getItems()) {
            if (factoryService.mixin(BudgetItem_IncomingInvoiceItems.class, budgetItem).invoiceItems().size()>0) return "There are invoice items attached";
        }
        return null;
    }

    public String validateRemoveBudget(final boolean areYouSure){
        return areYouSure ? null : "Please confirm";
    }

    @Inject
    private PartitionItemRepository partitionItemRepository;

    @Inject
    private UserService userService;

    @Inject
    private FactoryService factoryService;

}
