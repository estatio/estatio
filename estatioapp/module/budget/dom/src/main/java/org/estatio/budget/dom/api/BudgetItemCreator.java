package org.estatio.budget.dom.api;

import org.estatio.budget.dom.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;

public interface BudgetItemCreator {

    BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge
    );

}
