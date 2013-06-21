package org.estatio.dom.lease;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Unit;

@Hidden
public class LeaseUnits extends EstatioDomainService<LeaseUnit> {

    public LeaseUnits() {
        super(LeaseUnits.class, LeaseUnit.class);
    }

    // //////////////////////////////////////

    // @Hidden
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public LeaseUnit newLeaseUnit(Lease lease, UnitForLease unit) {
        LeaseUnit lu = newTransientInstance(LeaseUnit.class);
        persist(lu);
        lu.modifyLease(lease);
        lu.modifyUnit(unit);
        return lu;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public LeaseUnit find(final Lease lease, final Unit unit, LocalDate startDate) {
         return firstMatch("leaseUnit_find", "lease", lease, "unit", unit, "startDate", startDate);
    }

}
