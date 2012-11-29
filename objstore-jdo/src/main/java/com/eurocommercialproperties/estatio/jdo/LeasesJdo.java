package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.Leases;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class LeasesJdo extends Leases {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindByReference(reference));
    }
    
    private static QueryDefault<Lease> queryForFindByReference(String reference) {
        return new QueryDefault<Lease>(Lease.class, "lease_findLeaseByReference", "r", reference);
    }

}
