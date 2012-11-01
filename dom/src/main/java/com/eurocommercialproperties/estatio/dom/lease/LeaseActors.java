package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

@Named("Lease Actors")
public class LeaseActors extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leaseActors";
    }

    public String iconName() {
        return "LeaseActor";
    }
    // }}

    // {{ newLeaseActor
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseActor newLeaseActor(
            final Lease lease, 
            final Party party, 
            final LeaseActorType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        final LeaseActor leaseActor = newTransientInstance(LeaseActor.class);
        leaseActor.setParty(party);
        leaseActor.setLease(lease);
        leaseActor.setStartDate(startDate);
        leaseActor.setEndDate(endDate);
        leaseActor.setType(type);
        persist(leaseActor);
        return leaseActor;
    }
    // }}

    // {{ findLeaseActor
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseActor findLeaseActor(
            final Lease lease, 
            final Party party, 
            final LeaseActorType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        
        return firstMatch(LeaseActor.class, new Filter<LeaseActor>() {
            @Override
            public boolean accept(final LeaseActor leaseActor) {
                return leaseActor.getLease().equals(lease) & leaseActor.getParty().equals(party) 
                        //TODO handle optional condition fields as they can contain null
                        // leaseActor.getStartDate().equals(startDate) & leaseActor.getEndDate().equals(endDate)
                        ;
            }
        });
    }
    // }}

    
    // {{ allLeaseActors
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseActor> allLeaseActors() {
        return allInstances(LeaseActor.class);
    }
    // }}


}
