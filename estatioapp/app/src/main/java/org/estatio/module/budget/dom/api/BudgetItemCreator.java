package org.estatio.module.budget.dom.api;

import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.charge.dom.Charge;

public interface BudgetItemCreator {

    BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge
    );

}
