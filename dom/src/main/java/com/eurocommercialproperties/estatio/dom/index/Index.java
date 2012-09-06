package com.eurocommercialproperties.estatio.dom.index;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
public class Index extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Values (Collection)
    @Persistent(mappedBy = "index", column = "INDEX_ID")
    private Set<IndexValue> values = new LinkedHashSet<IndexValue>();

    @MemberOrder(sequence = "1")
    public Set<IndexValue> getValues() {
        return values;
    }

    public void setValues(final Set<IndexValue> values) {
        this.values = values;
    }
    // }}

}
