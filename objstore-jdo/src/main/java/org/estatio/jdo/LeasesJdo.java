package org.estatio.jdo;

import java.util.List;


import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.utils.StringUtils;

public class LeasesJdo extends Leases {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Lease> findLeasesByReference(@Named("Reference") String reference) {
        return allMatches(queryForFindByReference(reference));
    }
    
    private static QueryDefault<Lease> queryForFindByReference(String reference) {
        return new QueryDefault<Lease>(Lease.class, "lease_findLeaseByReference", "r", StringUtils.wildcardToRegex(reference));
    }
}
