package org.estatio.budget.dom.api;

import java.math.BigDecimal;

import org.estatio.budget.dom.partioning.PartitionItem;
import org.estatio.budget.dom.keytable.KeyTable;
import org.estatio.dom.charge.Charge;

public interface PartitionItemCreator {

    PartitionItem updateOrCreatePartitionItem(
            final Charge allocationCharge,
            final KeyTable keyTable,
            final BigDecimal percentage
    );

}
