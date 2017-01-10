package org.estatio.dom.budgeting.api;

import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;

public interface BudgetItemCreator {

    BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge
    );

}
