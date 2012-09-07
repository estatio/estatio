package com.eurocommercialproperties.estatio.objstore.dflt.index;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;

import com.eurocommercialproperties.estatio.dom.index.Index;
import com.eurocommercialproperties.estatio.dom.index.Indices;

public class IndicesDefault extends AbstractFactoryAndRepository implements Indices {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "indices";
    }

    public String iconName() {
        return "Index";
    }

    // }}

    // {{ NewIndex (action)
    @Override
    public Index newIndex(final String reference, String name) {
        final Index index = newTransientInstance(Index.class);
        index.setReference(reference);
        index.setName(name);
        persist(index);
        return index;
    }

    // }}

    // {{ findByReference
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Index findByReference(@Named("Reference") final String reference) {
        return firstMatch(Index.class, new Filter<Index>() {
            @Override
            public boolean accept(final Index index) {
                return reference.equals(index.getReference());
            }
        });
    }

    // {{ AllInstances
    @Override
    @ActionSemantics(Of.SAFE)
    public List<Index> allInstances() {
        return allInstances(Index.class);
    }
    // }}

    
    
}
