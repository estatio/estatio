package com.eurocommercialproperties.estatio.dom.index;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

@Named("Indices")
public interface Indices {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Index newIndex(@Named("Reference") String reference, @Named("Name") String name);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Index findByReference(@Named("Reference") String reference);

    List<Index> allInstances();

}
