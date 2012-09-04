package com.eurocommercialproperties.estatio.objstore.dflt.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;
import org.apache.isis.applib.filter.Filter;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActor;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActors;
import com.eurocommercialproperties.estatio.dom.party.Party;

public class LeaseActorsDefault extends AbstractFactoryAndRepository implements LeaseActors {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leaseActors";
    }

    public String iconName() {
        return "PropertActor";
    }

    // }}

    // {{ NewLease (hidden)
    @Override
    @QueryOnly
    @MemberOrder(sequence = "1")
    public LeaseActor newLeaseActor(Lease lease, Party party, LeaseActorType type, LocalDate from, LocalDate thru) {
        final LeaseActor leaseActor = newTransientInstance(LeaseActor.class);
        leaseActor.setParty(party);
        leaseActor.setLease(lease);
        leaseActor.setFrom(from);
        leaseActor.setThru(thru);
        leaseActor.setType(type);
        persist(leaseActor);
        return leaseActor;
    }

    // }}

    // {{ AllInstances
    @Override
    public List<LeaseActor> allInstances() {
        return allInstances(LeaseActor.class);
    }
    // }}

    @Override
    public LeaseActor findLeaseActor(final Lease lease, final Party party, LeaseActorType type, final LocalDate from, final LocalDate thru) {
        
        return firstMatch(LeaseActor.class, new Filter<LeaseActor>() {
            @Override
            public boolean accept(final LeaseActor leaseActor) {
                return leaseActor.getLease().equals(lease) & leaseActor.getParty().equals(party) 
                        //TODO handle optional condition fields as they can contain null
                        // leaseActor.getFrom().equals(from) & leaseActor.getThru().equals(thru)
                        ;
            }
        });
    }

}
