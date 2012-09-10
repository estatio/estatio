package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

@Hidden
@Named("Leases")
public interface LeaseTerms {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseTerm newLeaseTerm(LeaseItem leaseItem);

    @ActionSemantics(Of.SAFE)
    List<LeaseTerm> allInstances();

}
