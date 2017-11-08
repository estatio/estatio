package org.estatio.module.budgeting.dom.api;

import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.module.charge.dom.Charge;

public interface BudgetItemCreator {

    BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge
    );

}
