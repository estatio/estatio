package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

import org.joda.time.LocalDate;

@Named("Leases")
public class Leases extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leases";
    }

    public String iconName() {
        return "Lease";
    }
    // }}

    // {{ newLease
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Lease newLease(
            final @Named("Reference") String reference, 
            final @Named("Name") String name) {
        Lease lease = newTransientInstance(Lease.class);
        lease.setReference(reference);
        lease.setName(name);
        persist(lease);
        return lease;
    }
    // }}
    
    // {{ findByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(
            final @Named("Reference") String reference) {
        return firstMatch(Lease.class, new Filter<Lease>() {
            @Override
            public boolean accept(final Lease lease) {
                return reference.equals(lease.getReference());
            }
        });
    }
    // }}
    
    // {{ runIndexation
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public void runIndexation(
            final @Named("Until") LocalDate until) {

        // TODO Auto-generated method stub
        
        
    }
    // }}

    // {{ allLeases
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Lease> allLeases() {
        return allInstances(Lease.class);
    }
    // }}
    

}
