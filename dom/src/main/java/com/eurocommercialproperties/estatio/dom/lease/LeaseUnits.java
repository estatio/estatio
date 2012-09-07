package com.eurocommercialproperties.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import com.eurocommercialproperties.estatio.dom.asset.Unit;

@Named("LeaseUnits")
public interface LeaseUnits {

    //@Hidden
    @MemberOrder(sequence = "1")
    public LeaseUnit newLeaseUnit(Lease lease, Unit unit);

    @ActionSemantics(Of.SAFE)
    List<LeaseUnit> allInstances();

}
