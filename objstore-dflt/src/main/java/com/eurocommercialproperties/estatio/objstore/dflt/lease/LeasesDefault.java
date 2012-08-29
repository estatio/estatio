package com.eurocommercialproperties.estatio.objstore.dflt.lease;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.Leases;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;
import org.apache.isis.applib.filter.Filter;

public class LeasesDefault extends AbstractFactoryAndRepository implements Leases {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leases";
    }

    public String iconName() {
        return "Lease";
    }
    
    @Override
    @QueryOnly
    @MemberOrder(sequence = "1")
    public Lease newLease(@Named("Reference") String reference, @Named("Name") String name) {
        Lease lease = newTransientInstance(Lease.class);
        lease.setReference(reference);
        lease.setName(name);
        persist(lease);
        return lease;
    }

    @Override
    @QueryOnly
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") final String reference) {
        return firstMatch(Lease.class, new Filter<Lease>() {
            @Override
            public boolean accept(final Lease lease) {
                return reference.equals(lease.getReference());
            }
        });
    }

    @Override
    @QueryOnly
    public List<Lease> allInstances() {
        return allInstances(Lease.class);
    }

}
