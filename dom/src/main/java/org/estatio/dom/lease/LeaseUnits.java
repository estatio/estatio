package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Unit;

@Named("LeaseUnits")
@Hidden
public class LeaseUnits extends EstatioDomainService {

    public LeaseUnits() {
        super(LeaseUnits.class, LeaseUnit.class);
    }

    // //////////////////////////////////////

    // @Hidden
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    public LeaseUnit newLeaseUnit(@Named("Lease") Lease lease, @Named("Unit") UnitForLease unit) {
        LeaseUnit lu = newTransientInstance(LeaseUnit.class);
        persist(lu);
        lu.modifyLease(lease);
        lu.modifyUnit(unit);
        return lu;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    @Hidden
    public LeaseUnit find(final Lease lease, final Unit unit, LocalDate startDate) {
         return firstMatch(queryForFind(lease, unit, startDate));
    }
     
    private static QueryDefault<LeaseUnit> queryForFind(Lease lease, Unit unit, LocalDate startDate) {
        return new QueryDefault<LeaseUnit>(LeaseUnit.class, "leaseUnit_find", "lease", lease, "unit", unit, "startDate", startDate);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseUnit> allLeaseUnits() {
        return allInstances(LeaseUnit.class);
    }
}
