package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Leases")
public interface Leases {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Lease newLease(@Named("Reference") String reference, @Named("Name") String name);

    @QueryOnly
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") String reference);

    @QueryOnly
    List<Lease> allInstances();

}
