package com.eurocommercialproperties.estatio.objstore.dflt.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;
import org.apache.isis.applib.filter.Filter;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnit;
import com.eurocommercialproperties.estatio.dom.lease.Leases;

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
    @ActionSemantics(Of.SAFE)
    // TODO: Q: Should these annotation live on both the interface and the
    // implementation?
    @MemberOrder(sequence = "1")
    public Lease newLease(@Named("Reference") String reference, @Named("Name") String name) {
        Lease lease = newTransientInstance(Lease.class);
        lease.setReference(reference);
        lease.setName(name);
        persist(lease);
        return lease;
    }

    @Override
    @ActionSemantics(Of.SAFE)
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
    @ActionSemantics(Of.SAFE)
    public List<Lease> allInstances() {
        return allInstances(Lease.class);
    }

    @Override
    @Hidden
    @ActionSemantics(Of.SAFE)
    public LeaseUnit newLeaseUnit(Lease lease, Unit unit) {
        LeaseUnit leaseUnit = newTransientInstance(LeaseUnit.class);
        leaseUnit.setLease(lease);
        leaseUnit.setUnit(unit);
        persist(leaseUnit);
        return leaseUnit;
    }

}
