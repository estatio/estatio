package org.estatio.dom.budgeting.api;

import java.math.BigDecimal;

import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;

public interface BudgetItemCreator {

    BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge,
            final BigDecimal budgetedValue
    );

    BudgetItem updateOrCreateBudgetItem(
            final Charge budgetItemCharge,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue
    );

}
