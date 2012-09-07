package com.eurocommercialproperties.estatio.objstore.dflt.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnit;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnits;

public class LeaseUnitsDefault extends AbstractFactoryAndRepository implements LeaseUnits {

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
    @MemberOrder(sequence = "1")
    public LeaseUnit newLeaseUnit(@Named("Lease") Lease lease, @Named("Unit") Unit unit) {
        LeaseUnit lu = newTransientInstance(LeaseUnit.class);
        lu.setLease(lease);
        lu.setUnit(unit);
        persist(lu);
        lease.addToUnits(lu);
        return lu;
    }

    @Override
    @ActionSemantics(Of.SAFE)
    public List<LeaseUnit> allInstances() {
        return allInstances(LeaseUnit.class);
    }
}
