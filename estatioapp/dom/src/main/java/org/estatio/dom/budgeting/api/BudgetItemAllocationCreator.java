package org.estatio.dom.budgeting.api;

import java.math.BigDecimal;

import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;

public interface BudgetItemAllocationCreator {

    BudgetItemAllocation findOrCreateBudgetItemAllocation(
            final Charge allocationCharge,
            final KeyTable keyTable,
            final BigDecimal percentage
    );

}
