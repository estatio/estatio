package org.estatio.module.budget.dom.api;

import java.math.BigDecimal;

import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.charge.dom.Charge;

public interface PartitionItemCreator {

    PartitionItem updateOrCreatePartitionItem(
            final Charge allocationCharge,
            final KeyTable keyTable,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedValue,
            final BigDecimal fixedAuditedValue
    );

}
