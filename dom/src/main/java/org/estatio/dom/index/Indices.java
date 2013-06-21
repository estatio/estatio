package org.estatio.dom.index;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioDomainService;

public class Indices extends EstatioDomainService<Index> {

    public Indices() {
        super(Indices.class, Index.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Index newIndex(final @Named("Reference") String reference, final @Named("Name") String name) {
        final Index index = newTransientInstance();
        index.setReference(reference);
        index.setName(name);
        persist(index);
        return index;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Indices", sequence = "4")
    public Index findIndexByReference(final @Named("Reference") String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Index> allIndices() {
        return allInstances();
    }



}
