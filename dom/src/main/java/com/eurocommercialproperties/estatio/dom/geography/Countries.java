package com.eurocommercialproperties.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

@Named("Countries")
public interface Countries {

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Country newCountry(@Named("reference") String reference, @Named("Name") String name);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Country findByReference(@Named("reference") String reference);
    
    List<Country> allInstances();

}
