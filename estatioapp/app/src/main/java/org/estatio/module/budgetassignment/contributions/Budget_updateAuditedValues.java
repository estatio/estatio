package org.estatio.module.budgetassignment.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budgetassignment.dom.BudgetService;

@Mixin
public class Budget_updateAuditedValues {

    private final Budget budget;
    public Budget_updateAuditedValues(Budget budget){
        this.budget = budget;
    }

    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget updateAuditedValues() {
        budgetService.calculateAuditedBudgetItemValues(budget);
        return budget;
    }

    public String disableUpdateAuditedValues(){
        return budget.getStatus()==Status.RECONCILED ? "The budget is reconciled" : null;
    }

    @Inject
    private BudgetService budgetService;

}
