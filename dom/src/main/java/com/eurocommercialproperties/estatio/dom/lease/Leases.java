package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

@Named("Leases")
public interface Leases {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Lease newLease(@Named("Reference") String reference, @Named("Name") String name);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") String reference);

    @ActionSemantics(Of.SAFE)
    List<Lease> allInstances();

}
