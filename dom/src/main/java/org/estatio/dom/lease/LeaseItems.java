package org.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;

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

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<LeaseItem> allLeaseItems() {
        return allInstances();
    }

}
