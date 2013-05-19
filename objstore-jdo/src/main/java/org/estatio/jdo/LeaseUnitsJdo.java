package org.estatio.jdo;

import org.joda.time.LocalDate;


import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseUnit;
import org.estatio.dom.lease.LeaseUnits;

public class LeaseUnitsJdo extends LeaseUnits {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    @Hidden
    public LeaseUnit find(Lease lease, Unit unit, LocalDate startDate) {
         return firstMatch(queryForFind(lease, unit, startDate));
     }
     
     private static QueryDefault<LeaseUnit> queryForFind(Lease lease, Unit unit, LocalDate startDate) {
         return new QueryDefault<LeaseUnit>(LeaseUnit.class, "leaseUnit_find", "lease", lease, "unit", unit, "startDate", startDate);
     }

}
