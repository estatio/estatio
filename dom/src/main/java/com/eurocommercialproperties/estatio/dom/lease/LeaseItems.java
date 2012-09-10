package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

//TODO: Q: do we need separate repositories for each entity or can/should we cluster them?
@Hidden
@Named("Leases")
public interface LeaseItems {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseItem newLeaseItem(@Named("Lease") Lease lease);

    @ActionSemantics(Of.SAFE)
    List<LeaseItem> allInstances();

}
