package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

@Named("Lease Actors")
@Hidden
public interface LeaseActors {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseActor newLeaseActor(Lease Lease, Party party, LeaseActorType type, LocalDate startDate, LocalDate endDate);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseActor findLeaseActor(Lease Lease, Party party, LeaseActorType type, LocalDate startDate, LocalDate endDate);

    @ActionSemantics(Of.SAFE)
    List<LeaseActor> allInstances();

}
