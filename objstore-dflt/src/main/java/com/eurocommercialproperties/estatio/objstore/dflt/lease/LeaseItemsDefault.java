package com.eurocommercialproperties.estatio.objstore.dflt.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItem;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItems;

public class LeaseItemsDefault extends AbstractFactoryAndRepository implements LeaseItems {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leaseitems";
    }

    public String iconName() {
        return "LeaseItem";
    }

    @Override
    @ActionSemantics(Of.SAFE)
    // TODO: Q: Should these annotation live on both the interface and the
    // implementation?
    @MemberOrder(sequence = "1")
    public LeaseItem newLeaseItem(@Named("Lease") Lease lease) {
        LeaseItem leaseItem = newTransientInstance(LeaseItem.class);
        leaseItem.setLease(lease);
        persist(leaseItem);
        return leaseItem;
    }

    @Override
    @ActionSemantics(Of.SAFE)
    public List<LeaseItem> allInstances() {
        return allInstances(LeaseItem.class);
    }
}
