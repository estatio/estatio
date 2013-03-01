package org.estatio.jdo;


import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActor;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseActors;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

public class LeaseActorsJdo extends LeaseActors {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseActor findLeaseActor(Lease lease, Party party, LeaseActorType type, LocalDate startDate, LocalDate endDate) {
        return firstMatch(queryForFind(lease, party, type, startDate, endDate));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public LeaseActor findLeaseActorWithType(Lease lease, LeaseActorType type, LocalDate date) {
        return firstMatch(queryForFindWithType(lease, type, date));
    }
    
    private static QueryDefault<LeaseActor> queryForFind(Lease lease, Party party, LeaseActorType type, LocalDate startDate, LocalDate endDate) {
        return new QueryDefault<LeaseActor>(LeaseActor.class, "leaseActor_find", "lease", lease, "party", party, "type", type, "startDate", startDate, "endDate", endDate);
    }

    private static QueryDefault<LeaseActor> queryForFindWithType(Lease lease, LeaseActorType type, LocalDate date) {
        return new QueryDefault<LeaseActor>(LeaseActor.class, "leaseActor_findWithType", "lease", lease, "type", type, "date", date);
    }

    
}
