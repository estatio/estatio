package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

//TODO: Q: do we need separate repositories for each entity or can/should we cluster them?
@Named("Leases")
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

    // {{ newLeaseItem
    
    // TODO: Q: Should these annotation live on both the interface and the
    // implementation?
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseItem newLeaseItem(final Lease lease) {
        LeaseItem leaseItem = newTransientInstance(LeaseItem.class);
        leaseItem.setLease(lease);
        persist(leaseItem);
        return leaseItem;
    }
    // }}

    // {{ allLeaseItems
    @ActionSemantics(Of.SAFE)
    public List<LeaseItem> allLeaseItems() {
        return allInstances(LeaseItem.class);
    }
    // }}

}
