package org.estatio.jdo;

import java.util.List;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.utils.StringUtils;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;

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
        return firstMatch(queryForFindByReference(reference, clockService.now()));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Lease> findLeasesByReference(@Named("Reference") String reference) {
        return allMatches(queryForFindByReference(reference, clockService.now()));
    }

    private static QueryDefault<Lease> queryForFindByReference(String reference, LocalDate date) {
        return new QueryDefault<Lease>(Lease.class, "lease_findLeaseByReference", "r", StringUtils.wildcardToRegex(reference), "date", date);
    }

    ClockService clockService;

    public void setClockService(ClockService clockService) {
        this.clockService = clockService;
    }
}
