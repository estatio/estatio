package org.estatio.module.budgeting.dom.api;

import java.math.BigDecimal;

import org.estatio.module.budgeting.dom.partioning.PartitionItem;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
import org.estatio.module.charge.dom.Charge;

public interface PartitionItemCreator {

    PartitionItem updateOrCreatePartitionItem(
            final Charge allocationCharge,
            final KeyTable keyTable,
            final BigDecimal percentage
    );

}
