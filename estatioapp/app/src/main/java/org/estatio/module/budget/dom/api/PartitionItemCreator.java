package org.estatio.module.budget.dom.api;

import java.math.BigDecimal;

import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

public interface PartitionItemCreator {

    PartitionItem updateOrCreatePartitionItem(
            final Charge allocationCharge,
            final PartitioningTable partitioningTable,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedValue,
            final BigDecimal fixedAuditedValue
    );

}
