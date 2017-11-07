package org.estatio.dom.budgeting.api;

import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.module.charge.dom.Charge;

public interface BudgetItemCreator {

    BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge
    );

}
