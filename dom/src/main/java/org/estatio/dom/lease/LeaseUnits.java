package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;


import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.asset.Unit;

@Named("LeaseUnits")
public class LeaseUnits extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leasesUnit";
    }

    public String iconName() {
        return "LeaseUnit";
    }
    // }}

    // {{ newLeaseUnit
    //@Hidden
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    public LeaseUnit newLeaseUnit(@Named("Lease") Lease lease, @Named("Unit") Unit unit) {
        LeaseUnit lu = newTransientInstance(LeaseUnit.class);
        lu.setLease(lease);
        lu.setUnit(unit);
        persist(lu);
        lease.addToUnits(lu);
        return lu;
    }
    // }}

    // {{ find
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    @Hidden
    public LeaseUnit find(final Lease lease, final Unit unit, LocalDate startDate) {
        return firstMatch(LeaseUnit.class, new Filter<LeaseUnit>() {
            @Override
            public boolean accept(final LeaseUnit leaseUnit) {
                return leaseUnit.getLease().equals(lease) &&  leaseUnit.getUnit().equals(unit);
            }
        });
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
