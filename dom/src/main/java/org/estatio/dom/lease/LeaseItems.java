package org.estatio.dom.lease;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class LeaseItems extends EstatioDomainService<LeaseItem> {

    public LeaseItems() {
        super(LeaseItems.class, LeaseItem.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public LeaseItem newLeaseItem(final Lease lease, final LeaseItemType type) {
        LeaseItem leaseItem = newTransientInstance();
        persistIfNotAlready(leaseItem);
        lease.addToItems(leaseItem);
        leaseItem.setType(type);
        return leaseItem;
    }


}
