package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import com.eurocommercialproperties.estatio.dom.asset.Unit;

@Named("Leases")
public interface Leases {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Lease newLease(@Named("Reference") String reference, @Named("Name") String name);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") String reference);

    @Hidden
    @MemberOrder(sequence = "1")
    public LeaseUnit newLeaseUnit(Lease lease, Unit unit);

    @ActionSemantics(Of.SAFE)
    List<Lease> allInstances();

}
