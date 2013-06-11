package org.estatio.jdo;

import java.util.List;

import org.estatio.dom.asset.FixedAsset;
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
        return firstMatch(queryForFindByReference(reference));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Lease> findLeasesByReference(@Named("Reference") String reference) {
        return allMatches(queryForFindByReference(reference));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeases(FixedAsset fixedAsset, LocalDate activeOnDate) {
        return allMatches(queryForFind(fixedAsset, activeOnDate));
    }

    private static QueryDefault<Lease> queryForFindByReference(String reference) {
        return new QueryDefault<Lease>(Lease.class, "findLeasesByReference", "r", StringUtils.wildcardToRegex(reference));
    }

    private static QueryDefault<Lease> queryForFind(FixedAsset fixedAsset, LocalDate activeOnDate) {
        return new QueryDefault<Lease>(Lease.class, "findLeases", "fixedAsset", fixedAsset, "activeOnDate", activeOnDate);
    }

    ClockService clockService;

    public void setClockService(ClockService clockService) {
        this.clockService = clockService;
    }
}
