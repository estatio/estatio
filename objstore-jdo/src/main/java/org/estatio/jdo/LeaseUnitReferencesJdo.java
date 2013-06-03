package org.estatio.jdo;

import org.estatio.dom.lease.tags.LeaseUnitReference;
import org.estatio.dom.lease.tags.LeaseUnitReferenceType;
import org.estatio.dom.lease.tags.LeaseUnitReferences;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.query.QueryDefault;

public class LeaseUnitReferencesJdo extends LeaseUnitReferences {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    @Hidden
    public LeaseUnitReference find(LeaseUnitReferenceType type, String reference) {
        return firstMatch(queryForFind(type, reference));
    }

    private static QueryDefault<LeaseUnitReference> queryForFind(LeaseUnitReferenceType type, String reference) {
        return new QueryDefault<LeaseUnitReference>(LeaseUnitReference.class, "leaseUnitReference_find", "type", type, "reference", reference);
    }

}
