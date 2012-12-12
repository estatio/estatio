package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActor;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActors;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class LeaseActorsJdo extends LeaseActors {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseActor findLeaseActor(Lease lease, Party party, LeaseActorType type, @Named("Start Date") LocalDate startDate, @Named("End Date") LocalDate endDate) {
        return firstMatch(queryForFind(lease, party, type, startDate, endDate));
    }
    
    private static QueryDefault<LeaseActor> queryForFind(Lease lease, Party party, LeaseActorType type, LocalDate startDate, LocalDate endDate) {
        return new QueryDefault<LeaseActor>(LeaseActor.class, "leaseActor_find", "lease", lease, "party", party, "type", type, "startDate", startDate, "endDate", endDate);
    }

}
