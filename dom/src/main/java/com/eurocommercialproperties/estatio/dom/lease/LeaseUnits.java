package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import com.eurocommercialproperties.estatio.dom.asset.Unit;

@Named("LeaseUnits")
public class LeaseUnits extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leases";
    }

    public String iconName() {
        return "Lease";
    }
    // }}

    // {{ newLeaseUnit
    //@Hidden
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseUnit newLeaseUnit(@Named("Lease") Lease lease, @Named("Unit") Unit unit) {
        LeaseUnit lu = newTransientInstance(LeaseUnit.class);
        lu.setLease(lease);
        lu.setUnit(unit);
        persist(lu);
        lease.addToUnits(lu);
        return lu;
    }
    // }}

    // {{ allLeaseUnits
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseUnit> allLeaseUnits() {
        return allInstances(LeaseUnit.class);
    }
    // }}
}
