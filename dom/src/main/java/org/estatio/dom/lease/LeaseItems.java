package org.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;

@Named("Lease Items")
@Hidden
public class LeaseItems extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leaseitems";
    }

    public String iconName() {
        return "LeaseItem";
    }
    // }}

    // {{ newLeaseItem
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    public LeaseItem newLeaseItem(final Lease lease, final LeaseItemType type) {
        LeaseItem leaseItem = newTransientInstance(LeaseItem.class);
        leaseItem.setLease(lease);
        leaseItem.setType(type);
        persist(leaseItem);
        return leaseItem;
    }
    // }}

    // {{ allLeaseItems
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseItem> allLeaseItems() {
        return allInstances(LeaseItem.class);
    }
    // }}

}
